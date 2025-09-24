package org.ses.gadgetbuilder.impl.adapters.getters;

import org.ses.gadgetbuilder.adapters.GetterMethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Impact;

import java.net.URL;

@Impact(Impact.SSRF)
public class URLMethodInvokeAdapter extends GetterMethodInvokeAdapter {
    @Override
    public Object getInvocationTarget(String command) throws Exception {
        return new URL(command);
    }

    @Override
    public String getMethodName() {
        return "getContent";
    }

    @Override
    public Class<?> getTargetInterface() {
        return null;
    }
}

package org.ses.gadgetbuilder.adapters;

public abstract class GetterMethodInvokeAdapter extends MethodInvokeAdapter {

    @Override
    public Class<?>[] getParamTypes() {
        return new Class[0];
    }

    @Override
    public Object[] getParams() {
        return new Object[0];
    }

    public String getGetterMethodProperty() {
        return this.getMethodName().substring(3,4).toLowerCase() + this.getMethodName().substring(4);
    }
}

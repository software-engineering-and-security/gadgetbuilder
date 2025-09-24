package org.ses.gadgetbuilder.adapters;

import org.ses.gadgetbuilder.chains.command.CommandFormat;

public abstract class MethodInvokeAdapter implements SinkAdapter{

    public CommandFormat commandFormat;

    public MethodInvokeAdapter() {
        this.commandFormat = CommandFormat.getCommandFormatFromImpact(this.getImpact());
    }

    public abstract Object getInvocationTarget(String command) throws Exception;
    public abstract String getMethodName();
    public abstract Class<?>[] getParamTypes();
    public abstract Object[] getParams();

    public String getCommandFormat() {
        return this.commandFormat.getCommandFormat();
    }

    public abstract Class<?> getTargetInterface();

}

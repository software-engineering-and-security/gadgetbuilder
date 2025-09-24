package org.ses.gadgetbuilder.adapters;

import org.ses.gadgetbuilder.chains.command.CommandFormat;

public abstract class InitializeAdapter implements SinkAdapter {

    public CommandFormat commandFormat;

    public InitializeAdapter() {
        this.commandFormat = CommandFormat.getCommandFormatFromImpact(this.getImpact());
    }

    public abstract Class<?> getConstructorClass();
    public abstract Class<?>[] getParamTypes();
    public abstract Object[] getParams(String command) throws Exception;


}

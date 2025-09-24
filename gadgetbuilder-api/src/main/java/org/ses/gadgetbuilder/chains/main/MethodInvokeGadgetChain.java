package org.ses.gadgetbuilder.chains.main;

import org.ses.gadgetbuilder.adapters.MethodInvokeAdapter;
import org.ses.gadgetbuilder.chains.trampolines.Trampoline;

public abstract class MethodInvokeGadgetChain<T extends Trampoline, A extends MethodInvokeAdapter> extends GadgetChain<T> {

    public MethodInvokeGadgetChain(T _trampoline, A _adapter) {
        super(_trampoline);
        this.methodInvokeAdapter = _adapter;
        this.commandFormat = _adapter.commandFormat;
    }

    protected A methodInvokeAdapter;

    public void setMethodInvokeAdapter(A _adapter) {
        this.methodInvokeAdapter = _adapter;
    }
    public MethodInvokeAdapter getMethodInvokeAdapter() {return this.methodInvokeAdapter;}

    @Override
    protected String getCommandFormat() {
        if (this.methodInvokeAdapter == null) return "Command format depends on sink adapter";
        return super.getCommandFormat();
    }


}

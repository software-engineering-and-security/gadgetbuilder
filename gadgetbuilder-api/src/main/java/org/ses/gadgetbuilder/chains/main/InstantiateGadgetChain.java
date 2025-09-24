package org.ses.gadgetbuilder.chains.main;

import org.ses.gadgetbuilder.adapters.InitializeAdapter;
import org.ses.gadgetbuilder.chains.trampolines.Trampoline;

public abstract class InstantiateGadgetChain<T extends Trampoline, A extends InitializeAdapter> extends GadgetChain<T> {

    protected A initializeAdapter;


    public InstantiateGadgetChain(T _trampoline, A _adapter) {
        super(_trampoline);
        this.initializeAdapter = _adapter;
        this.commandFormat = _adapter.commandFormat;

    }

    public A getInitializeAdapter() {
        return initializeAdapter;
    }

    public void setInitializeAdapter(A initializeAdapter) {
        this.initializeAdapter = initializeAdapter;
    }

    @Override
    protected String getCommandFormat() {
        if (this.initializeAdapter == null) return "Command format depends on sink adapter";
        return super.getCommandFormat();
    }
}

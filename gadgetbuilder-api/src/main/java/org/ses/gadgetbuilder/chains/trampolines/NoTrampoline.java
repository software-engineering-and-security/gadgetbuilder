package org.ses.gadgetbuilder.chains.trampolines;

public class NoTrampoline implements Trampoline {
    private static NoTrampoline instance;

    private NoTrampoline() {}

    public static NoTrampoline getInstance() {
        if (instance == null) NoTrampoline.instance = new NoTrampoline();
        return instance;
    }

}

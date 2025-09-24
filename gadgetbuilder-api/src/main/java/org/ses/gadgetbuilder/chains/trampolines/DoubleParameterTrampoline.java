package org.ses.gadgetbuilder.chains.trampolines;

public interface DoubleParameterTrampoline<T,P1,P2> extends Trampoline {
    Object wrapPayload(T payload, P1 param1, P2 param2);
}

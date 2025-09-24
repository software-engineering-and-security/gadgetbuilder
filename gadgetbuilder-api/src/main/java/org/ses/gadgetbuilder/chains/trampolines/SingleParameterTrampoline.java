package org.ses.gadgetbuilder.chains.trampolines;

public interface SingleParameterTrampoline<T, P> extends Trampoline {
    Object wrapPayload(T payload, P param) throws Exception;
}

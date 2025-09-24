package org.ses.gadgetbuilder.chains.trampolines;

public interface NoParameterTrampoline<T> extends Trampoline {
    Object wrapPayload(T payload) throws Exception;
}

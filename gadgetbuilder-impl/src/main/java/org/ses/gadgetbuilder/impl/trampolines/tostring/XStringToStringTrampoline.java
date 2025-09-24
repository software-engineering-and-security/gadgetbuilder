package org.ses.gadgetbuilder.impl.trampolines.tostring;

import com.sun.org.apache.xpath.internal.objects.XString;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.chains.trampolines.noparam.ToStringTrampoline;
import org.ses.gadgetbuilder.impl.trampolines.equals.ConcurrentHashMapEquals;
import org.ses.gadgetbuilder.impl.trampolines.equals.HashMapEquals;
import org.ses.gadgetbuilder.impl.trampolines.equals.HashtableEquals;

@Authors(Authors.BofeiC)
public class XStringToStringTrampoline implements ToStringTrampoline {
    @Override
    public Object wrapPayload(Object payload) throws Exception {

        XString xString = new XString("foo");
        ConcurrentHashMapEquals equalsTrampoline = new ConcurrentHashMapEquals();

        return equalsTrampoline.wrapPayload(xString, payload);
    }
}

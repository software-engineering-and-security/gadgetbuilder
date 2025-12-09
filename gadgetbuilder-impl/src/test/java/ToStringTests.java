import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ses.gadgetbuilder.chains.trampolines.noparam.ToStringTrampoline;
import org.ses.gadgetbuilder.impl.trampolines.tostring.BadAttributeValueToStringImpl;
import org.ses.gadgetbuilder.impl.trampolines.tostring.UIDefaultsToStringTrampoline;
import org.ses.gadgetbuilder.util.Serialization;

import java.io.Serializable;

public class ToStringTests {

    public static Target target;

    @BeforeAll
    public static void setup() {
        target = new Target();
        Target.FLAG = false;
    }

    public static void testToStringTrampoline(ToStringTrampoline trampoline) throws Exception {
        byte[] payload = Serialization.serialize((Serializable) trampoline.wrapPayload(target));
        try {
            Serialization.deserialize(payload);
        } catch (Exception ignored) {  }

        Assertions.assertTrue(Target.FLAG);
    }

    @Test
    public void uidefaults_to_string_test() throws Exception {
        testToStringTrampoline(new UIDefaultsToStringTrampoline());
    }

    @Test
    public void badattributevalueexpexception_to_string_test() throws Exception {
        testToStringTrampoline(new BadAttributeValueToStringImpl());
    }


}

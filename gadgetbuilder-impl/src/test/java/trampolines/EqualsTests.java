import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ses.gadgetbuilder.chains.trampolines.singleparam.EqualsTrampoline;
import org.ses.gadgetbuilder.impl.trampolines.equals.ConcurrentHashMapEquals;
import org.ses.gadgetbuilder.impl.trampolines.equals.HashMapEquals;
import org.ses.gadgetbuilder.impl.trampolines.equals.HashtableEquals;
import org.ses.gadgetbuilder.impl.trampolines.equals.JComponentEquals;
import org.ses.gadgetbuilder.util.Serialization;

import java.io.Serializable;

public class EqualsTests {

    public static Target target;

    @BeforeEach
    public void setup() {
        target = new Target();
        Target.FLAG = false;
    }

    public static void testEqualsTrampoline(EqualsTrampoline trampoline) throws Exception {
        byte[] payload = Serialization.serialize((Serializable) trampoline.wrapPayload(target, target));
        try {
            Serialization.deserialize(payload);
        } catch (Exception ignored) {  }

        Assertions.assertTrue(Target.FLAG);
    }

    @Test
    public void hashmap_equals_test() throws Exception {
        testEqualsTrampoline(new HashMapEquals());
    }

    @Test
    public void hashtable_equals_test() throws Exception {
        testEqualsTrampoline(new HashtableEquals());
    }

    @Test
    public void concurrent_hashmap_equals_test() throws Exception {
        testEqualsTrampoline(new ConcurrentHashMapEquals());
    }

    @Test
    public void jcomponent_equals_test() throws Exception {
        testEqualsTrampoline(new JComponentEquals());
    }




}

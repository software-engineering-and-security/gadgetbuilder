import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ses.gadgetbuilder.chains.trampolines.noparam.HashCodeTrampoline;
import org.ses.gadgetbuilder.impl.trampolines.hashcode.ConcurrentHashMapTrampoline;
import org.ses.gadgetbuilder.impl.trampolines.hashcode.HashCodeHashMapTrampoline;
import org.ses.gadgetbuilder.impl.trampolines.hashcode.HashCodeHashSetTrampoline;
import org.ses.gadgetbuilder.impl.trampolines.hashcode.HashCodeHashTableTrampoline;
import org.ses.gadgetbuilder.util.Serialization;

import java.io.Serializable;

public class HashCodeTests {

    public static Target target;

    @BeforeEach
    public void setup() {
        target = new Target();
        Target.FLAG = false;
    }

    public static void testHashCodeTrampoline(HashCodeTrampoline trampoline) throws Exception {
        byte[] payload = Serialization.serialize((Serializable) trampoline.wrapPayload(target));
        try {
            Serialization.deserialize(payload);
        } catch (Exception ignored) {  }

        Assertions.assertTrue(Target.FLAG);
    }

    @Test
    public void hashmap_hashcode_test() throws Exception {
        testHashCodeTrampoline(new HashCodeHashMapTrampoline());
    }

    @Test
    public void concurrent_hashmap_hashcode_test() throws Exception {
        testHashCodeTrampoline(new ConcurrentHashMapTrampoline());
    }

    @Test
    public void hashset_hashcode_test() throws Exception {
        testHashCodeTrampoline(new HashCodeHashSetTrampoline());
    }

    @Test
    public void hashtable_hashcode_test() throws Exception {
        testHashCodeTrampoline(new HashCodeHashTableTrampoline());
    }


}

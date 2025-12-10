import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ses.gadgetbuilder.chains.trampolines.doubleparam.CompareTrampoline;
import org.ses.gadgetbuilder.impl.trampolines.compare.CC4Compare;
import org.ses.gadgetbuilder.impl.trampolines.compare.ConcurrentSkipListMapCompare;
import org.ses.gadgetbuilder.impl.trampolines.compare.PriorityQueueCompare;
import org.ses.gadgetbuilder.util.Serialization;

import java.io.Serializable;

public class CompareTests {

    public static Target target;

    @BeforeEach
    public void setup() {
        target = new Target();
        Target.FLAG = false;
    }

    public static void testCompareTrampoline(CompareTrampoline trampoline) throws Exception {
        byte[] payload = Serialization.serialize((Serializable) trampoline.wrapPayload(target, Target.compareInt1, Target.compareInt1));
        try {
            Serialization.deserialize(payload);
        } catch (Exception ignored) {  }

        Assertions.assertTrue(Target.FLAG);
    }

    @Test
    public void cc4_treebag_compare_test() throws Exception {
        testCompareTrampoline(new CC4Compare());
    }

    @Test
    public void concurrent_skiplistmap_compare_test() throws Exception {

        // ConcurrentSkipListMap readObject only uses cpr starting with jdk-10
        if (!System.getProperty("java.version").startsWith("1.")) {
            testCompareTrampoline(new ConcurrentSkipListMapCompare());
        }
    }

    @Test
    public void priorityqueue_compare_test() throws Exception {
        testCompareTrampoline(new PriorityQueueCompare());
    }

}

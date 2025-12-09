import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ses.gadgetbuilder.impl.trampolines.AnnotationInvocationHandlerMapGet;
import org.ses.gadgetbuilder.impl.trampolines.CompositeInvocationHandlerMapGet;
import org.ses.gadgetbuilder.util.Serialization;

import java.io.Serializable;

public class MapGetTests {

    public static Target target;

    @BeforeAll
    public static void setup() {
        target = new Target();
        Target.FLAG = false;
    }


    @Test
    public void test_annotation_invocation_handler_reaches_MapGet() {

        if (System.getProperty("java.version").startsWith("1.7") || System.getProperty("java.version").startsWith("1.6")) {
            AnnotationInvocationHandlerMapGet mapGet = new AnnotationInvocationHandlerMapGet();
            byte[] payload = Serialization.serialize((Serializable) mapGet.wrapPayload(target, "bar"));
            try {
                Serialization.deserialize(payload);
            } catch (Exception ignored) { }

            Assertions.assertTrue(Target.FLAG);
        }
    }

    @Test
    public void test_composite_invocation_handler_reaches_MapGet() {

        CompositeInvocationHandlerMapGet mapGet = new CompositeInvocationHandlerMapGet();
        byte[] payload = Serialization.serialize((Serializable) mapGet.wrapPayload(target, "bar"));

        try {
            Serialization.deserialize(payload);
        } catch (Exception ignored) {}

        Assertions.assertTrue(Target.FLAG);
    }

}

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ses.gadgetbuilder.impl.trampolines.mapget.AbstractMapGet;
import org.ses.gadgetbuilder.impl.trampolines.mapget.AnnotationInvocationHandlerMapGet;
import org.ses.gadgetbuilder.impl.trampolines.mapget.CompositeInvocationHandlerMapGet;
import org.ses.gadgetbuilder.util.Serialization;

import java.io.Serializable;

public class MapGetTests {

    public static Target target;

    @BeforeEach
    public void setup() {
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
        } catch (Throwable ignored) {}

        Assertions.assertTrue(Target.FLAG);
    }

    @Test
    public void test_abstract_map_reaches_MapGet() throws Exception {

        AbstractMapGet mapGet = new AbstractMapGet();
        byte[] payload = Serialization.serialize((Serializable) mapGet.wrapPayload(target, "bar"));

        try {
            Serialization.deserialize(payload);
        } catch (Throwable ignored) {}

        Assertions.assertTrue(Target.FLAG);
    }

}

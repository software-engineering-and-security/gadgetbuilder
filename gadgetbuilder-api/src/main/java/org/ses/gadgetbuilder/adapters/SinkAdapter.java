package org.ses.gadgetbuilder.adapters;

import org.ses.gadgetbuilder.annotations.Impact;

public interface SinkAdapter {
    default String getImpact() {
        Impact impactAnnotation = this.getClass().getAnnotation(Impact.class);
        if (impactAnnotation == null) return "Undefined";
        else return impactAnnotation.value();
    }
}

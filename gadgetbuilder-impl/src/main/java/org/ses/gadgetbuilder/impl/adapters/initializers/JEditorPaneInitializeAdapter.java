package org.ses.gadgetbuilder.impl.adapters.initializers;

import org.ses.gadgetbuilder.adapters.InitializeAdapter;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Impact;

import javax.swing.*;

@Authors({Authors.YihengZhang})
@Impact(Impact.SSRF)
public class JEditorPaneInitializeAdapter extends InitializeAdapter {

    @Override
    public Class<?> getConstructorClass() {
        return JEditorPane.class;
    }

    @Override
    public Class<?>[] getParamTypes() {
        return new Class[]{String.class};
    }

    @Override
    public Object[] getParams(String command) throws Exception {
        return new Object[] {command};
    }

}
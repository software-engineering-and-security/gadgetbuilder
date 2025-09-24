package org.ses.gadgetbuilder.chains.command;

public class FileWriteCommandFormat implements CommandFormat {

    @Override
    public String getCommandFormat() {
        return "<file>:<b64_data>";
    }

    @Override
    public boolean isValidCommandFormat(String command) {
        return command.split(":").length == 2;
    }
}

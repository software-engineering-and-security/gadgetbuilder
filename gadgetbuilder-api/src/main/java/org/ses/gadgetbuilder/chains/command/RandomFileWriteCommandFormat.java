package org.ses.gadgetbuilder.chains.command;

public class RandomFileWriteCommandFormat implements CommandFormat {

    @Override
    public String getCommandFormat() {
        return "<destDir>:<ascii-data>";
    }

    @Override
    public boolean isValidCommandFormat(String command) {
        return command.split(":").length == 2;
    }
}

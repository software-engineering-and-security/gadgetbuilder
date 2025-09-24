package org.ses.gadgetbuilder.chains.command;

public class ZeroFileCommandFormat implements CommandFormat {
    @Override
    public String getCommandFormat() {
        return "<file_to_overwrite_or_create_empty>";
    }

    @Override
    public boolean isValidCommandFormat(String command) {
        return command.split(" ").length == 1;
    }
}

package org.ses.gadgetbuilder.chains.command;

public class SetPropertyCommandFormat implements CommandFormat {
    @Override
    public String getCommandFormat() {
        return "<key>:<value>";
    }

    @Override
    public boolean isValidCommandFormat(String command) {
        return command.split(":").length == 2;
    }
}

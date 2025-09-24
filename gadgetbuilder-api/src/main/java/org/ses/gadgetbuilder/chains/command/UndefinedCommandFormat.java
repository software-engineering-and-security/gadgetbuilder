package org.ses.gadgetbuilder.chains.command;

public class UndefinedCommandFormat implements CommandFormat {
    @Override
    public String getCommandFormat() {
        return "Command format not defined, check the source code of the payload class.";
    }

    @Override
    public boolean isValidCommandFormat(String command) {
        return true;
    }
}

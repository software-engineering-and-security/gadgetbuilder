package org.ses.gadgetbuilder.chains.command;

public class RCECommandFormat implements CommandFormat {
    @Override
    public String getCommandFormat() {
        return "<shell-command>";
    }

    @Override
    public boolean isValidCommandFormat(String command) {
        return true;
    }
}

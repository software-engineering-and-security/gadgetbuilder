package org.ses.gadgetbuilder.chains.command;

import org.ses.gadgetbuilder.annotations.Impact;

public interface CommandFormat {

    String getCommandFormat();
    boolean isValidCommandFormat(String command);

    static CommandFormat getCommandFormatFromImpact(String impact) {
        switch (impact) {
            case Impact.RCE:
                return new RCECommandFormat();
            case Impact.JNDI:
                return new JNDICommandFormat();
            case Impact.LoadClass:
                return new RemoteClassCommandFormat();
            case Impact.ZeroFile:
                return new ZeroFileCommandFormat();
            case Impact.RandomFileWrite:
                return new RandomFileWriteCommandFormat();
            case Impact.FileWrite:
                return new FileWriteCommandFormat();
            case Impact.SetProperty:
                return new SetPropertyCommandFormat();
            case Impact.DNSLookup:
            case Impact.SSRF:
                return new URLCommandFormat();
            default:
                return new UndefinedCommandFormat();
        }
    }
}

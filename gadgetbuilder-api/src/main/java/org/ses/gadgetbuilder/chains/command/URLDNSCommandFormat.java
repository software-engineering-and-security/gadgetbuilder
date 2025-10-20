package org.ses.gadgetbuilder.chains.command;

import java.net.MalformedURLException;
import java.net.URL;

public class URLDNSCommandFormat implements  CommandFormat {
    @Override
    public String getCommandFormat() {
        return "<valid_fqdn>, e.g. foo.com";
    }

    @Override
    public boolean isValidCommandFormat(String command) {
        return command.split("\\.").length > 1;
    }
}

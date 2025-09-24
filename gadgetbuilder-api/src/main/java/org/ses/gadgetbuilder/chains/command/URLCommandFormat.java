package org.ses.gadgetbuilder.chains.command;

import java.net.MalformedURLException;
import java.net.URL;

public class URLCommandFormat implements  CommandFormat {
    @Override
    public String getCommandFormat() {
        return "<valid_url_identifier>, e.g. http://localhost:8000";
    }

    @Override
    public boolean isValidCommandFormat(String command) {
        try {
            new URL(command);
        } catch (MalformedURLException e) {
            return false;
        }

        return true;
    }
}

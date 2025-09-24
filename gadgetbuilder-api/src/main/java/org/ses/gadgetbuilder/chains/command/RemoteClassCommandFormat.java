package org.ses.gadgetbuilder.chains.command;

import java.net.MalformedURLException;
import java.net.URL;

public class RemoteClassCommandFormat implements CommandFormat {
    @Override
    public String getCommandFormat() {
        return "<base_url>:<classname>";
    }

    @Override
    public boolean isValidCommandFormat(String command) {

        if (command.split(":").length < 2) return false;


        String classname = command.split(":")[1];
        String url =  command.substring(0, classname.length() - 1);

        try {
            URL testURL = new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }

        return true;
    }
}

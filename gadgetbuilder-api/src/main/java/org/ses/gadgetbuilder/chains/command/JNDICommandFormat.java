package org.ses.gadgetbuilder.chains.command;

public class JNDICommandFormat implements CommandFormat {
    @Override
    public String getCommandFormat() {
        return "(rmi,ldap)://<attacker_server>[:<attacker_port>]/<classname>";
    }

    @Override
    public boolean isValidCommandFormat(String command) {
        int sep = command.lastIndexOf('/');
        if ( sep < 0 || (!command.startsWith("ldap") && !command.startsWith("rmi")))
            return false;
        return true;
    }
}

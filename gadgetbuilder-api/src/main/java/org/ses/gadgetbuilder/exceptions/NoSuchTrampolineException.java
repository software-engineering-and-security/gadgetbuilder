package org.ses.gadgetbuilder.exceptions;

public class NoSuchTrampolineException extends Exception{

    public NoSuchTrampolineException(String trampolineName) {
        super("No trampoline with simple name: " + trampolineName + " loaded.");
    }
}

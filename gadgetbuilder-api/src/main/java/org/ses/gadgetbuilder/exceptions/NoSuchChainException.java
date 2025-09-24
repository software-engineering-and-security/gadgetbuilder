package org.ses.gadgetbuilder.exceptions;

public class NoSuchChainException extends Exception {
    public NoSuchChainException(String chainName) {
        super("No gadget chain with simple name: " + chainName + " loaded.");
    }
}

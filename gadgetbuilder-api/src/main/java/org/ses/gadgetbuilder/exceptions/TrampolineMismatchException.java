package org.ses.gadgetbuilder.exceptions;

public class TrampolineMismatchException extends Exception{

    public TrampolineMismatchException(Class gadgetChainClass, Class trampolineIface) {
        super(String.format("The gadget chain %s requires a trampoline of type %s", gadgetChainClass.getSimpleName(), trampolineIface.getSimpleName()));
    }

}

package org.ses.gadgetbuilder.exceptions;

public class AdapterMismatchException extends Exception{
    public AdapterMismatchException(Class gadgetChainClass, Class adapterIface) {
        super(String.format("The gadget chain %s requires a sink adapter of type %s", gadgetChainClass.getSimpleName(), adapterIface.getSimpleName()));
    }

    public AdapterMismatchException(String message) {
        super(message);
    }

}

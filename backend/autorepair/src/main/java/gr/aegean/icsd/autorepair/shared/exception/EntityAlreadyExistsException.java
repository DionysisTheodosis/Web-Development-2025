package gr.aegean.icsd.autorepair.shared.exception;


import gr.aegean.icsd.autorepair.shared.exceptionhandler.ErrorCode;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(String message) {
        super(message);
    }

    public EntityAlreadyExistsException() {
        super(ErrorCode.ERROR_ENTITY_ALREADY_EXISTS);
    }
}
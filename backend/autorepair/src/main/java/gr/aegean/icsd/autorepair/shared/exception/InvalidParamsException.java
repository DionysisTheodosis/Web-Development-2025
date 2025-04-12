package gr.aegean.icsd.autorepair.shared.exception;


import gr.aegean.icsd.autorepair.shared.exceptionhandler.ErrorCode;

public class InvalidParamsException extends RuntimeException {

    public InvalidParamsException(String message) {
        super(message);
    }

    public InvalidParamsException() {
        super(ErrorCode.ERROR_INVALID_FILE_TYPE);
    }
}

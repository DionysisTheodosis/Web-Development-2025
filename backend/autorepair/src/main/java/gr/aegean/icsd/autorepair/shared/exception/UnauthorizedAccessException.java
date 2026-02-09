package gr.aegean.icsd.autorepair.shared.exception;


import gr.aegean.icsd.autorepair.shared.exceptionhandler.ErrorCode;

public class UnauthorizedAccessException extends RuntimeException{
    public UnauthorizedAccessException (String message) {
        super(message);
    }
    public UnauthorizedAccessException () {
        super(ErrorCode.ERROR_UNAUTHORIZED_ACCESS);
    }
}

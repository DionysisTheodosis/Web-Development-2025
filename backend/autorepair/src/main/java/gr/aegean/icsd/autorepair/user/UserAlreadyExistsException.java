package gr.aegean.icsd.autorepair.user;


import gr.aegean.icsd.autorepair.shared.exceptionhandler.ErrorCode;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
    public UserAlreadyExistsException() {
        super(ErrorCode.ERROR_USER_ALREADY_EXISTS);
    }
}

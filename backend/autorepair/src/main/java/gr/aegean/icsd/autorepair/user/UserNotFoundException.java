package gr.aegean.icsd.autorepair.user;


import gr.aegean.icsd.autorepair.shared.exceptionhandler.ErrorCode;

public class UserNotFoundException  extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException() {
        super(ErrorCode.ERROR_USER_NOT_FOUND);
    }
}


package gr.aegean.icsd.autorepair.shared.exception;


import gr.aegean.icsd.autorepair.shared.exceptionhandler.ErrorCode;

public class UnsupportedFileTypeException extends RuntimeException {

    public UnsupportedFileTypeException(String message) {
        super(message);
    }

    public UnsupportedFileTypeException() {
        super(ErrorCode.ERROR_UNSUPPORTED_FILE_TYPE);
    }
}
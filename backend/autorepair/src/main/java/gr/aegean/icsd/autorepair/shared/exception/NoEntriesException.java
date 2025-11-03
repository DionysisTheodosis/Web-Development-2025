package gr.aegean.icsd.autorepair.shared.exception;


import gr.aegean.icsd.autorepair.shared.exceptionhandler.ErrorCode;

public class NoEntriesException extends RuntimeException {
    public NoEntriesException(String message) {
        super(message);
    }

    public NoEntriesException() {
        super(ErrorCode.ERROR_NO_ENTRIES_PROVIDED);
    }
}
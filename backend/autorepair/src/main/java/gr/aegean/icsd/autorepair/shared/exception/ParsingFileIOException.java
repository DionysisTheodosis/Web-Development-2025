package gr.aegean.icsd.autorepair.shared.exception;


import gr.aegean.icsd.autorepair.shared.exceptionhandler.ErrorCode;

public class ParsingFileIOException extends RuntimeException {
    public ParsingFileIOException(String message) {
        super(message);
    }

    public ParsingFileIOException() {
        super(ErrorCode.ERROR_PARSING_FILE);
    }
}

package gr.aegean.icsd.autorepair.shared.exception;

public class InvalidSortByParameterException extends RuntimeException {

    public InvalidSortByParameterException(String message) {
        super(message);
    }

    public InvalidSortByParameterException() {
        super("Error: invalid sort parameter");
    }
}
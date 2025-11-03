package gr.aegean.icsd.autorepair.shared.security;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException() {
        super("JWT Token expired. Please log in again.");
    }
    public TokenExpiredException(String message) {
        super(message);
    }
}

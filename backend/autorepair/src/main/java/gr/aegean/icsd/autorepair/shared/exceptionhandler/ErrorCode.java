package gr.aegean.icsd.autorepair.shared.exceptionhandler;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorCode {
    public static final String ERROR_SLOT_NOT_FOUND = "Slot not found.";
    public static final String ERROR_USER_NOT_FOUND = "User not found.";
    public static final String ERROR_USER_ALREADY_EXISTS = "User already exists.";
    public static final String ERROR_INVALID_FILE_TYPE = "File type is invalid.";
    public static final String ERROR_PARSING_FILE = "An error occurred while parsing the file.";
    public static final String ERROR_ENTITY_ALREADY_EXISTS = "Entity already exists.";
    public static final String ERROR_NO_ENTRIES_PROVIDED = "Error: No entries provided.";
    public static final String ERROR_NOT_VALID_DATE_FORMAT = "Error: No valid date format should be yyyy-mm-dd.";
    public static final String ERROR_NOT_VALID_PARAMS = "Error: No valid parameters provided.";
    public static final String ERROR_UNSUPPORTED_FILE_TYPE = "Error: Unsupported file type.";
    public static final String ERROR_APPOINTMENT_NOT_FOUND = "Error: Appointment not found.";
    public static final String ERROR_INVALID_OPERATION = "Error: INVALID OPERATION.";
    public static final String ERROR_UNAUTHORIZED_ACCESS = "Error: Unauthorized access.";
    public static final String ERROR_ACCOUNT_LOCKED = "Unable to login because your account is locked.";
}

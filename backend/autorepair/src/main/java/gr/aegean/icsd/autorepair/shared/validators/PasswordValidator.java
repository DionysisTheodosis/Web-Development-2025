package gr.aegean.icsd.autorepair.shared.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        //It's empty because we don't need a default value on password
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        /* Password must contain:
         - At least 8 characters long
         - At least one lowercase letter (a-z)
         - At least one uppercase letter (A-Z)
         - At least one digit (0-9)
         - At least one special character from: !@#$%^&*()_+-=[]{};':"\\|,.<>/?
        */

        log.debug("Inside PasswordValidator isValid");
        
        if (value == null) {
            return false;
        }

        // Minimum 8 characters
        if (value.length() < 8) {
            return false;
        }

        // Check for at least one lowercase letter
        if (!value.matches(".*[a-z].*")) {
            return false;
        }

        // Check for at least one uppercase letter
        if (!value.matches(".*[A-Z].*")) {
            return false;
        }

        // Check for at least one digit
        if (!value.matches(".*\\d.*")) {
            return false;
        }

        // Check for at least one special character: !@#$%^&*()_+-=[]{};':"\\|,.<>/?
        if (!value.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            return false;
        }

        return true;
    }
}
package gr.aegean.icsd.autorepair.shared.validators;

import gr.aegean.icsd.autorepair.user.UserRole;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserRoleValidator implements ConstraintValidator<ValidUserRole, UserRole> {

    @Override
    public void initialize(ValidUserRole constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(UserRole value, ConstraintValidatorContext context) {
        log.debug("Inside UserRole Validator isValid: {}", value);
        return value != null;
    }
}

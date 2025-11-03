package gr.aegean.icsd.autorepair.shared.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MechanicRoleValidator implements ConstraintValidator<ValidMechanicRole, String> {

    @Override
    public void initialize(ValidMechanicRole constraintAnnotation) {
        //It's empty because we don't need a default value on USER ROLE
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Check if the field or parameter equals MECHANIC
        log.debug("Inside MechanicRoleValidator isValid");
        return value != null && value.equals("MECHANIC");
    }
}
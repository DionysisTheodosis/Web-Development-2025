package gr.aegean.icsd.autorepair.shared.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerRoleValidator implements ConstraintValidator<ValidCustomerRole, String> {

    @Override
    public void initialize(ValidCustomerRole constraintAnnotation) {
        //It's empty because we don't need a default value on CustomerROLE
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Check if the field or parameter equals CUSTOMER
        log.debug("Inside CustomerRoleValidator isValid");
        return value != null && value.equals("CUSTOMER");
    }
}
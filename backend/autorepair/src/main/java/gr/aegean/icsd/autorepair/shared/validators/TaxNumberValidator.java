package gr.aegean.icsd.autorepair.shared.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class TaxNumberValidator implements ConstraintValidator<ValidTaxNumber, String> {

    @Override
    public void initialize(ValidTaxNumber constraintAnnotation) {
        //It's empty because we don't need a default value on TaxNumber
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Check if the Tax number is exactly 11 digits
        log.debug("Inside TaxNumberValidator isValid");
        return value != null && value.matches("\\d{9}");
    }
}


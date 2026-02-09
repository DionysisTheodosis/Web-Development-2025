package gr.aegean.icsd.autorepair.shared.validators;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class AcquisitionYearValidator implements ConstraintValidator<ValidAcquisitionYear, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        int currentYear = LocalDate.now().getYear();

        return value >= 1900 && value <= currentYear;
    }
}
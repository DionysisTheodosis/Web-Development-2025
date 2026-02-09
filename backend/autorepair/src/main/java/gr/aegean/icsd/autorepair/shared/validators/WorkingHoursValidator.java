package gr.aegean.icsd.autorepair.shared.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class WorkingHoursValidator implements ConstraintValidator<ValidWorkingHours, LocalDateTime> {

    private static final LocalTime OPENING_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(16, 0);

    @Override
    public void initialize(ValidWorkingHours constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalTime time = value.toLocalTime();
        return !time.isBefore(OPENING_TIME) && !time.isAfter(CLOSING_TIME);
    }
}

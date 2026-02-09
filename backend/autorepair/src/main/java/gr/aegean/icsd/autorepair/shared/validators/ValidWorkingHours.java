package gr.aegean.icsd.autorepair.shared.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WorkingHoursValidator.class)
@Documented
public @interface ValidWorkingHours {
    String message() default "Appointment time must be between 08:00 and 16:00";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

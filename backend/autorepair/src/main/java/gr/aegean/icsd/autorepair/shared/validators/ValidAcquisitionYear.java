package gr.aegean.icsd.autorepair.shared.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AcquisitionYearValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAcquisitionYear {

    String message() default "Acquisition year must be between 1900 and the current year";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

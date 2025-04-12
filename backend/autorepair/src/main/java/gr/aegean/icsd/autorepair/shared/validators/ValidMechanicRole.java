package gr.aegean.icsd.autorepair.shared.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MechanicRoleValidator.class)
@Documented
public @interface ValidMechanicRole {

    String message() default "Invalid user role should be MECHANIC";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
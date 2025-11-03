package gr.aegean.icsd.autorepair.shared.validators;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TaxNumberValidator.class)
@Documented
public @interface ValidTaxNumber {

    String message() default "Invalid Tax number format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


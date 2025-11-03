package gr.aegean.icsd.autorepair.shared.validators;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomerRoleValidator.class)
@Documented
public @interface ValidCustomerRole {

    String message() default "Invalid user role should be CUSTOMER";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


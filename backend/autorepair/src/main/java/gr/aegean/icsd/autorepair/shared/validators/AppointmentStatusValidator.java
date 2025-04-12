package gr.aegean.icsd.autorepair.shared.validators;

import gr.aegean.icsd.autorepair.appointment.AppointmentStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppointmentStatusValidator implements ConstraintValidator<ValidAppointmentStatus, AppointmentStatus> {

    @Override
    public void initialize(ValidAppointmentStatus constraintAnnotation) {
        //It's empty because we don't need a default value on status
    }
    @Override
    public boolean isValid(AppointmentStatus value, ConstraintValidatorContext context) {
        // Check if the Status is either CREATED or ATTENDED OR COMPLETED OR CANCELED
        log.debug("Inside Status Validator isValid");
        return value != null && (value.equals(AppointmentStatus.COMPLETED) || value.equals(AppointmentStatus.CREATED) || value.equals(AppointmentStatus.IN_PROGRESS) || value.equals(AppointmentStatus.CANCELLED));
    }
}
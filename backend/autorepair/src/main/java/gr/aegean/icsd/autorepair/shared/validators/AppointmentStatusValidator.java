package gr.aegean.icsd.autorepair.shared.validators;

import gr.aegean.icsd.autorepair.appointment.AppointmentStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppointmentStatusValidator implements ConstraintValidator<ValidAppointmentStatus, AppointmentStatus> {

    @Override
    public void initialize(ValidAppointmentStatus constraintAnnotation) {
    }
    @Override
    public boolean isValid(AppointmentStatus value, ConstraintValidatorContext context) {
        log.debug("Inside Status Validator isValid");
        return value != null && (value.equals(AppointmentStatus.COMPLETED) || value.equals(AppointmentStatus.CREATED) || value.equals(AppointmentStatus.IN_PROGRESS) || value.equals(AppointmentStatus.CANCELLED));
    }
}
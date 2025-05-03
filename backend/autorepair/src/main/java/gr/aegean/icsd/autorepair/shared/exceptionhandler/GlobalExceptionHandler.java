package gr.aegean.icsd.autorepair.shared.exceptionhandler;


import gr.aegean.icsd.autorepair.shared.exception.*;
import gr.aegean.icsd.autorepair.shared.security.TokenExpiredException;
import gr.aegean.icsd.autorepair.user.UserAlreadyExistsException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpServerErrorException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
//
//    @ExceptionHandler(DoctorAvailabilityAlreadyExistsException.class)
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public @ResponseBody ErrorResponse handleDoctorAvailabilityAlreadyExistsException(
//            DoctorAvailabilityAlreadyExistsException ex) {
//        log.info(ex.getMessage());
//        return new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
//    }
//
//    @ExceptionHandler(DoctorNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public @ResponseBody ErrorResponse handleDoctorNotExistException(
//            DoctorNotFoundException ex) {
//        log.info(ex.getMessage());
//        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
//    }
//
//    @ExceptionHandler(PatientNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public @ResponseBody ErrorResponse handlePatientNotFoundException(
//            PatientNotFoundException ex) {
//        log.info(ex.getMessage());
//        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
//    }
//
//    @ExceptionHandler(SlotNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public @ResponseBody ErrorResponse handleSlotNotExistException(
//            SlotNotFoundException ex) {
//        log.info(ex.getMessage());
//        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
//    }
//
//    @ExceptionHandler(UserNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public @ResponseBody ErrorResponse handleUserNotFoundException(
//            UserNotFoundException ex) {
//        log.info(ex.getMessage());
//        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
//    }
//
//    @ExceptionHandler(RegistrationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public @ResponseBody ErrorResponse handleRegistrationException(
//            RegistrationException ex) {
//        log.info(ex.getMessage());
//        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
//    }



    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(HttpStatus.CONFLICT.value(), "Data Integrity Violation");
    }

    @ExceptionHandler(OptimisticLockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody ErrorResponse handleOptimisticLockException(OptimisticLockException ex) {
        log.error(ex.getMessage());
        return new ErrorResponse(HttpStatus.CONFLICT.value(), "Optimistic locking failure");
    }

//    @ExceptionHandler(InvalidSortByParameterException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public @ResponseBody ErrorResponse handleInvalidSortByParameterException(InvalidSortByParameterException ex) {
//        log.info(ex.getMessage());
//        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
//    }
//
//    @ExceptionHandler(PatientAlreadyExistsException.class)
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public @ResponseBody ErrorResponse handlePatientAlreadyExistsException(PatientAlreadyExistsException ex) {
//        log.info(ex.getMessage());
//        return new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
//    }
//
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody ErrorResponse handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(NoChangesDetectedException.class)
    @ResponseStatus(HttpStatus.NOT_MODIFIED)
    public @ResponseBody ErrorResponse handleNoChangesDetectedException(NoChangesDetectedException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(HttpStatus.NOT_MODIFIED.value(), ex.getMessage());
    }
//
    @ExceptionHandler(InvalidFileTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleInvalidFileTypeException(InvalidFileTypeException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
//
    @ExceptionHandler(ParsingFileIOException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleParsingFileIOException(ParsingFileIOException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
//
//    @ExceptionHandler(MedicalHistoryNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public @ResponseBody ErrorResponse handleMedicalHistoryNotFoundException(
//            MedicalHistoryNotFoundException ex) {
//        log.info(ex.getMessage());
//        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
//    }
//
//    @ExceptionHandler(MedicalHistorySaveException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public @ResponseBody ErrorResponse handleMedicalHistorySaveException(MedicalHistorySaveException ex) {
//        log.error(ex.getMessage());
//        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
//    }
//
//    @ExceptionHandler(PatientSaveException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public @ResponseBody ErrorResponse handlePatientSaveException(PatientSaveException ex) {
//        log.error(ex.getMessage());
//        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
//    }
//
//    @ExceptionHandler(MedicalHistoryRecordNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public @ResponseBody ErrorResponse handleMedicalHistoryRecordNotFoundException(
//            MedicalHistoryRecordNotFoundException ex) {
//        log.info(ex.getMessage());
//        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
//    }
//

    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody ErrorResponse handleEntityAlreadyExistsException(
            EntityAlreadyExistsException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(NoEntriesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleNoEntriesException(
            NoEntriesException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(InvalidParamsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleInvalidParamsException(
            InvalidParamsException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(UnsupportedFileTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleUnsupportedFileTypeException(
            UnsupportedFileTypeException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
//
//    @ExceptionHandler(DoctorAvailabilityNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public @ResponseBody ErrorResponse handleDoctorAvailabilityNotFoundException(
//            DoctorAvailabilityNotFoundException ex) {
//        log.info(ex.getMessage());
//        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
//    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleConstraintViolationException(
            ConstraintViolationException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(InvalidOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleInvalidOperationException(
            InvalidOperationException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody ErrorResponse handleUnauthorizedAccessException(
            UnauthorizedAccessException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    }
//
//    @ExceptionHandler(AppointmentNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public @ResponseBody ErrorResponse handleAppointmentNotFoundException(
//            AppointmentNotFoundException ex) {
//        log.info(ex.getMessage());
//        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
//    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");

        // Collecting all validation error messages
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorMessage.append(error.getField())
                    .append(" - ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        }

        log.info(ex.getMessage());
        // Return validation errors in the response body
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody ErrorResponse handleTokenExpiredException(
            TokenExpiredException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    }
    @ExceptionHandler(LockedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody ErrorResponse handleLockedException(LockedException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ErrorCode.ERROR_ACCOUNT_LOCKED);
    }

    @ExceptionHandler(EntitySavingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody ErrorResponse handleEntitySavingException(
            EntitySavingException ex) {
        log.info(ex.getMessage());
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }

}
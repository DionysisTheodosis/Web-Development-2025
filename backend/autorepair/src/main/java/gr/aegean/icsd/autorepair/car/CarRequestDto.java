package gr.aegean.icsd.autorepair.car;

import java.time.LocalDate;

import gr.aegean.icsd.autorepair.shared.exception.InvalidParamsException;
import gr.aegean.icsd.autorepair.shared.security.CustomUserDetails;
import gr.aegean.icsd.autorepair.shared.validators.ValidAcquisitionYear;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record CarRequestDto(

        @NotBlank(message = "Serial number is required")
        @Size(min = 5, max = 20, message = "Serial number usually has 17 characters")
        @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Serial number must contain only letters and numbers")
        String serialNumber,

        @NotBlank(message = "Model is required")
        @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9\\s\\-]*$",
                message = "Must start with a letter and contain only letters, numbers, spaces and '-'")
        String model,

        @NotBlank(message = "Brand  is required")
        @Pattern(regexp = "^\\p{L}[\\p{L}\\s\\-0-9']+$", message = "Invalid format")
        String brand,

        @NotNull(message = "Car type is required")
        CarType carType,

        @NotNull(message = "Movement type is required")
        FuelType fuelType,

        @NotNull(message = "Door count is required")
        @Min(value = 2, message = "Car must have at least 2 doors")
        @Max(value = 8, message = "Car cannot have more than 8 doors")
        Integer doorCount,

        @NotNull(message = "Wheel count is required")
        @Min(value = 3, message = "Car must have at least 3 wheels")
        @Max(value = 10, message = "Car cannot have more than 10 wheels")
        Integer wheelCount,

        @NotNull(message = "Production date is required")
        @PastOrPresent(message = "Production date cannot be in the future")
        LocalDate productionDate,

        @NotNull(message = "Acquisition year is required")
        @ValidAcquisitionYear
        Integer acquisitionYear,

        Long ownerId
) {
    public Long resolveOwnerId(CustomUserDetails user) {

        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("SECRETARY"))) {
            if (this.ownerId == null) {
                throw new InvalidParamsException("Car Owner Id is required");
            }
            return this.ownerId;
        }
        return user.getId();
    }
}
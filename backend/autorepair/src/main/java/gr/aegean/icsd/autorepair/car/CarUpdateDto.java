package gr.aegean.icsd.autorepair.car;


import gr.aegean.icsd.autorepair.shared.validators.ValidAcquisitionYear;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CarUpdateDto(

        @Size(min = 5, max = 20, message = "Serial number usually has 17 characters")
        @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Serial number must contain only letters and numbers")
        String serialNumber,

        @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9\\s\\-]*$",
                message = "Must start with a letter and contain only letters, numbers, spaces and '-'")
        String model,

        @Pattern(regexp = "^\\p{L}[\\p{L}\\s\\-']+$", message = "Invalid format")
        String brand,

        CarType carType,

        FuelType fuelType,

        @Min(value = 2, message = "Car must have at least 2 doors")
        @Max(value = 8, message = "Car cannot have more than 8 doors")
        Integer doorCount,

        @Min(value = 3, message = "Car must have at least 3 wheels")
        @Max(value = 10, message = "Car cannot have more than 10 wheels")
        Integer wheelCount,

        @PastOrPresent(message = "Production date cannot be in the future")
        LocalDate productionDate,

        @ValidAcquisitionYear
        Integer acquisitionYear
) {
}
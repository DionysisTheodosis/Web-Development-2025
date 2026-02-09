package gr.aegean.icsd.autorepair.car;

import gr.aegean.icsd.autorepair.appointment.Appointment;
import gr.aegean.icsd.autorepair.shared.validators.ValidAcquisitionYear;
import gr.aegean.icsd.autorepair.user.customer.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "cars", indexes = {
        @Index(name = "idx_car_owner", columnList = "owner_id"),
        @Index(name = "idx_car_serial", columnList = "serial_number")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Serial number is required")
    @Size(min = 5, max = 50, message = "Serial number must be between 5 and 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Serial number must contain only letters and numbers")
    @Column(name = "serial_number", unique = true, nullable = false, length = 50)
    private String serialNumber;

    @NotBlank(message = "Model is required")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9\\s\\-]*$",
            message = "Must start with a letter and contain only letters, numbers, spaces and '-'")
    @Column(nullable = false, length = 50)
    private String model;

    @NotBlank(message = "Brand is required")
    @Pattern(regexp = "^\\p{L}[\\p{L}\\s\\-0-9']+$", message = "Invalid brand format")
    @Column(nullable = false, length = 50)
    private String brand;

    @NotNull(message = "Car type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "car_type", nullable = false, length = 20)
    private CarType carType;

    @NotNull(message = "Movement type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 20)
    private FuelType fuelType;

    @Min(value = 2, message = "Car must have at least 2 doors")
    @Max(value = 8, message = "Car cannot have more than 8 doors")
    @Column(name = "door_count", nullable = false)
    private int doorCount;

    @Min(value = 3, message = "Car must have at least 3 wheels")
    @Max(value = 10, message = "Car cannot have more than 10 wheels")
    @Column(name = "wheel_count", nullable = false)
    private int wheelCount;

    @NotNull(message = "Production date is required")
    @PastOrPresent(message = "Production date cannot be in the future")
    @Column(name = "production_date", nullable = false)
    private LocalDate productionDate;

    @ValidAcquisitionYear
    @Column(name = "acquisition_year", nullable = false)
    private int acquisitionYear;

    @NotNull(message = "Car owner is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Customer owner;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();
}

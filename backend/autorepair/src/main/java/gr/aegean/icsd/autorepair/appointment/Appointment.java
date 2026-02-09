package gr.aegean.icsd.autorepair.appointment;

import gr.aegean.icsd.autorepair.car.Car;
import gr.aegean.icsd.autorepair.user.customer.Customer;
import gr.aegean.icsd.autorepair.user.mechanic.Mechanic;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "appointments", indexes = {
        @Index(name = "idx_appointment_datetime", columnList = "date_time"),
        @Index(name = "idx_appointment_status", columnList = "status"),
        @Index(name = "idx_appointment_mechanic", columnList = "assigned_mechanic_id"),
        @Index(name = "idx_appointment_car", columnList = "car_id"),
        @Index(name = "idx_appointment_customer", columnList = "customer_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Date and time is required")
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @NotNull(message = "Reason is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentReason reason;

    @Column(name = "problem_description", columnDefinition = "TEXT")
    private String problemDescription;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.CREATED;

    @Column(name = "total_cost", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalCost = BigDecimal.ZERO;

    @NotNull(message = "Car is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_mechanic_id")
    private Mechanic assignedMechanic;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WorkItem> workItems = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
    }

    public void recalculateTotalCost() {
        this.totalCost = workItems.stream()
                .map(WorkItem::getCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}


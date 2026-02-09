package gr.aegean.icsd.autorepair.appointment;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "work_items", indexes = {
        @Index(name = "idx_workitem_appointment", columnList = "appointment_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Description is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String materials;

    @Column(name = "completion_time")
    private LocalTime completionTime;

    @NotNull(message = "Cost is required")
    @Positive(message = "Cost must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    @NotNull(message = "Appointment is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;
}


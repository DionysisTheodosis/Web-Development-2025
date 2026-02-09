package gr.aegean.icsd.autorepair.user.customer;

import gr.aegean.icsd.autorepair.appointment.Appointment;
import gr.aegean.icsd.autorepair.car.Car;
import gr.aegean.icsd.autorepair.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set; // ✅ Import Set

@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_taxnumber", columnList = "tax_number")
})
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(exclude = {"cars","appointments"})
public class Customer extends User {

    @Column(name="tax_number", unique = true, nullable = false, length = 20)
    private String taxNumber;

    @Column(nullable = false)
    private String address;


    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Car> cars = new HashSet<>();


    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Appointment> appointments = new HashSet<>();
}
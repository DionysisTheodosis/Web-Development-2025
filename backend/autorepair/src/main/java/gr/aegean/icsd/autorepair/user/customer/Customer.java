package gr.aegean.icsd.autorepair.user.customer;

import gr.aegean.icsd.autorepair.appointment.Appointment;
import gr.aegean.icsd.autorepair.car.Car;
import gr.aegean.icsd.autorepair.user.User;
import gr.aegean.icsd.autorepair.user.UserDetailsDto;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(exclude = {"cars","appointments"})
public class Customer extends User {

    @Column(name="tax_number",unique = true, nullable = false, length = 20)
    private String taxNumber;

    @Column(nullable = false)
    private String address;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Car> cars = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();


}
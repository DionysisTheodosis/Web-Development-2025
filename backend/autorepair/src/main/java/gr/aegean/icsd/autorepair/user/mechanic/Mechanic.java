package gr.aegean.icsd.autorepair.user.mechanic;

import gr.aegean.icsd.autorepair.appointment.Appointment;
import gr.aegean.icsd.autorepair.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mechanics")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(exclude = "assignedAppointments")
public class Mechanic extends User {

    @Column(nullable = false, length = 100)
    private String specialty;
    @OneToMany(mappedBy = "assignedMechanic", fetch = FetchType.LAZY)
    private List<Appointment> assignedAppointments = new ArrayList<>();


}

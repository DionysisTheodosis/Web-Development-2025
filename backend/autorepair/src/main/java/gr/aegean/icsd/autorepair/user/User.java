package gr.aegean.icsd.autorepair.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(unique = true, nullable = false, length = 50)
    protected String username;

    @Column(unique = true, nullable = false, length = 100)
    protected String email;

    @Column(nullable = false)
    protected String password;

    @Column(name="first_name",nullable = false, length = 50)
    protected String firstName;

    @Column(name="last_name",nullable = false, length = 50)
    protected String lastName;

    @Column(name="identity_number",unique = true, nullable = false, length = 20)
    protected String identityNumber;

    @Column(nullable = false)
    private boolean active = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    protected UserRole role;


}

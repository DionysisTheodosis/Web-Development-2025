package gr.aegean.icsd.autorepair.user;

import gr.aegean.icsd.autorepair.shared.validators.ValidName;
import gr.aegean.icsd.autorepair.shared.validators.ValidPassword;
import gr.aegean.icsd.autorepair.shared.validators.ValidPersonalID;
import gr.aegean.icsd.autorepair.shared.validators.ValidUserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_lastname", columnList = "last_name"),
        @Index(name = "idx_user_role", columnList = "role")
})
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

    @ValidName
    @Column(unique = true, nullable = false, length = 50)
    protected String username;


    @Column(unique = true, nullable = false, length = 100)
    protected String email;

    @ValidPassword
    @Column(nullable = false)
    protected String password;

    @ValidName
    @Column(name="first_name",nullable = false, length = 50)
    protected String firstName;

    @ValidName
    @Column(name="last_name",nullable = false, length = 50)
    protected String lastName;

    @ValidPersonalID
    @Column(name="identity_number",unique = true, nullable = false, length = 20)
    protected String identityNumber;

    @Column(nullable = false)
    private boolean active = false;

    @ValidUserRole
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    protected UserRole role;


}

package gr.aegean.icsd.autorepair.user;


import gr.aegean.icsd.autorepair.shared.security.AuthInfoDto;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    long countByActiveFalse();

    long countByRoleAndActiveTrue(UserRole role);



    @Query("""
SELECT new gr.aegean.icsd.autorepair.shared.security.AuthInfoDto(
    u.id,u.firstName,u.lastName, u.username, u.role
)
FROM User u
WHERE u.username = :username
""")
    AuthInfoDto findAuthInfoByUsername(String username);

    @Transactional
    Optional<User> findByUsername(String username);
    Page<User> findAll(Specification<User> specification, Pageable pageable);
}

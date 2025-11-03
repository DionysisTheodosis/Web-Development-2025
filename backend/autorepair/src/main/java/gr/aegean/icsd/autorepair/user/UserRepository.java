package gr.aegean.icsd.autorepair.user;


import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User,Long> {


    @Modifying
    @Transactional
    Optional<User> findByUsername(String username);
    Page<User> findAll(Specification<User> specification, Pageable pageable);
}

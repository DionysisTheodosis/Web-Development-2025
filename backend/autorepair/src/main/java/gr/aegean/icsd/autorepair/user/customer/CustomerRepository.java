package gr.aegean.icsd.autorepair.user.customer;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {


    @Query("SELECT c FROM Customer c WHERE c.taxNumber = :taxNumber")
    Optional<Customer> findByTaxNumber(@Param("taxNumber") String taxNumber);



}

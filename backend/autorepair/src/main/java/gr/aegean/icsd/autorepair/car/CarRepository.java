package gr.aegean.icsd.autorepair.car;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CarRepository extends JpaRepository<Car,Long>, JpaSpecificationExecutor<Car> {

    Optional<Car> findCarByIdAndOwner_Id(Long id, Long owner_id);
    Boolean existsBySerialNumber(String serialNumber);
}

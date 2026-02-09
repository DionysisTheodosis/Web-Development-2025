package gr.aegean.icsd.autorepair.car;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;

@Builder
@Relation(collectionRelation = "cars", itemRelation = "car")
public record CarResponseDto(
        @JsonProperty("id")
        Long id,
        String serialNumber,
        String brand,
        String model,
        CarType carType,
        FuelType fuelType,

        Integer doorCount,

        Integer wheelCount,
        LocalDate productionDate,
        Integer acquisitionYear,
        OwnerInfo ownerInfo
) {
}

package gr.aegean.icsd.autorepair.car;

import gr.aegean.icsd.autorepair.user.customer.Customer;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Component
public class CarMapper {
    public CarResponseDto toDto(@NonNull Car car) {
        return toDto(car, null);
    }

    public CarResponseDto toDto(@NonNull Car car, OwnerInfo ownerInfo) {
        return new CarResponseDto(
                car.getId(),
                car.getSerialNumber(),
                car.getBrand(),
                car.getModel(),
                car.getCarType(),
                car.getFuelType(),
                car.getDoorCount(),
                car.getWheelCount(),
                car.getProductionDate(),
                car.getAcquisitionYear(),
                ownerInfo);
    }


    public Car toEntity(@NonNull CarRequestDto dto, @NonNull Customer owner) {
        return Car.builder()
                .serialNumber(dto.serialNumber())
                .brand(dto.brand())
                .model(dto.model())
                .carType(dto.carType())
                .fuelType(dto.fuelType())
                .doorCount(dto.doorCount())
                .wheelCount(dto.wheelCount())
                .productionDate(dto.productionDate())
                .acquisitionYear(dto.acquisitionYear())
                .owner(owner)
                .build();
    }

    public Set<CarRequestDto> mapFileRepresentationToDto(List<CarFileRepresentation> representations, Long forcedOwnerId) {
        return representations.stream()
                .map(rep -> {
                    if (forcedOwnerId != null) {
                        rep.setOwnerId(forcedOwnerId);
                    }
                    return toDtoFromFile(rep);
                })
                .collect(Collectors.toSet());
    }

    private CarRequestDto toDtoFromFile(CarFileRepresentation rep) {
        CarType carType = null;
        try {
            if (rep.getCarType() != null) carType = CarType.valueOf(rep.getCarType().toUpperCase());
        } catch (IllegalArgumentException e) { /* Log or ignore */ }

        FuelType fuelType = null;
        try {
            if (rep.getFuelType() != null) fuelType = FuelType.valueOf(rep.getFuelType().toUpperCase());
        } catch (IllegalArgumentException e) { /* Log or ignore */ }

        LocalDate pDate = null;
        try {
            if (rep.getProductionDate() != null) pDate = rep.getProductionDate();
        } catch (DateTimeParseException e) { /* Log or ignore */ }

        return new CarRequestDto(
                rep.getSerialNumber(),
                rep.getBrand(),
                rep.getModel(),
                carType,
                fuelType,
                rep.getDoorCount(),
                rep.getWheelCount(),
                pDate,
                rep.getAcquisitionYear(),
                rep.getOwnerId()
        );
}}
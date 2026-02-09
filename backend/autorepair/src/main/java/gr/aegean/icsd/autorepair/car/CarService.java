package gr.aegean.icsd.autorepair.car;

import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import gr.aegean.icsd.autorepair.shared.exception.*;
import gr.aegean.icsd.autorepair.shared.security.CustomUserDetails;
import gr.aegean.icsd.autorepair.shared.utils.AuthUtils;
import gr.aegean.icsd.autorepair.shared.utils.CsvParser;
import gr.aegean.icsd.autorepair.shared.validators.GenericValidator;
import gr.aegean.icsd.autorepair.shared.validators.ValidCsvFileType;
import gr.aegean.icsd.autorepair.shared.validators.ValidExcelFileType;
import gr.aegean.icsd.autorepair.user.customer.Customer;
import gr.aegean.icsd.autorepair.user.customer.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final CustomerService customerService;
    private final CarMapper carMapper;
    private final CsvParser csvParser;
    private final GenericValidator genericValidator;
    private final AuthUtils authUtils;
    private final ObjectProvider<CarService> selfProvider;

    @Transactional
    public CarResponseDto create(CarRequestDto request) {

        CustomUserDetails user = authUtils.getCurrentUser();

        Long finalOwnerId = request.resolveOwnerId(user);
        if (carRepository.existsBySerialNumber(request.serialNumber())) {
            throw new InvalidOperationException("Car with this Serial Number already exists.");
        }

        Customer owner = customerService.findById(finalOwnerId);

        Car car = carMapper.toEntity(request, owner);
        return carMapper.toDto(carRepository.save(car));
    }

    @Transactional
    public CarResponseDto getById(Long id) {

        Optional<Car> carOptional = switch ( authUtils.getCurrentUser().getUserRole()) {

            case "CUSTOMER" -> carRepository.findCarByIdAndOwner_Id(id,authUtils.getAuthenticatedUserId());

            case "MECHANIC" -> Optional.empty();

            case "SECRETARY" -> carRepository.findById(id);

            default -> throw new AccessDeniedException("Unknown role");
        };
        return carOptional
                .map(carMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found."));
    }

    @Transactional
    public void delete(Long id) {

        CustomUserDetails user = authUtils.getCurrentUser();
        String role = user.getUserRole();

        Car carToDelete = getCarSecurely(id);
        carRepository.delete(carToDelete);
    }

    @Transactional
    public void bulkDelete(List<Long> ids) {
        ids.forEach(this::delete);
    }

    @Transactional
    public Page<CarResponseDto> searchCars(
            Pageable pageable,
            Long requestedOwnerId,
            Long requestedMechanicId,
            String ownerTaxNumber,
            String ownerUsername,
            String brand,
            String model,
            String serialNumber,
            CarType carType,
            FuelType fuelType,
            LocalDate dateFrom,
            LocalDate dateTo,
            String keyword,
            Boolean withOwnerInfo
    ) {
        CustomUserDetails currentUser = authUtils.getCurrentUser();

        Long finalOwnerId = null;
        Long finalMechanicId = null;

        String role = currentUser.getUserRole();

        switch (role) {
            case "CUSTOMER" -> {
                finalOwnerId = currentUser.getId();
                finalMechanicId = requestedMechanicId;
            }
            case "MECHANIC" -> {
                finalMechanicId = currentUser.getId();
                finalOwnerId = requestedOwnerId;
            }
            case "SECRETARY" -> {
                finalOwnerId = requestedOwnerId;
                finalMechanicId = requestedMechanicId;
            }
            default -> throw new AccessDeniedException("User role not authorized to search cars.");
        }

        Specification<Car> spec = CarSpecifications.withFilters(
                finalOwnerId,
                finalMechanicId,
                ownerUsername,
                ownerTaxNumber,
                brand,
                model,
                serialNumber,
                carType,
                fuelType,
                dateFrom,
                dateTo,
                keyword
        );
        boolean shouldIncludeOwner = Boolean.TRUE.equals(withOwnerInfo);
        return carRepository.findAll(spec, pageable)
                .map(car -> {
                    OwnerInfo info = null;
                    if (shouldIncludeOwner && car.getOwner() != null) {
                        info = new OwnerInfo(
                                car.getOwner().getId(),
                                car.getOwner().getFirstName() + " "+ car.getOwner().getLastName(),
                                role.equals("SECRETARY") ?car.getOwner().getTaxNumber():null
                        );
                    }
                    return carMapper.toDto(car, info);
                });
    }

    @Transactional
    public CarResponseDto update(Long id, CarUpdateDto dto) {
        return updateCarInternal(getCarSecurely(id), dto);
    }


    private CarResponseDto updateCarInternal(Car car, CarUpdateDto dto) {
        boolean updated = false;

        updated |= updateSerialNumberIfNeeded(car, dto.serialNumber());
        updated |= updateBrandIfNeeded(car, dto.brand());
        updated |= updateModelIfNeeded(car, dto.model());

        updated |= updateCarTypeIfNeeded(car, dto.carType());
        updated |= updateFuelTypeIfNeeded(car, dto.fuelType());

        updated |= updateDoorCountIfNeeded(car, dto.doorCount());
        updated |= updateWheelCountIfNeeded(car, dto.wheelCount());
        updated |= updateAcquisitionYearIfNeeded(car, dto.acquisitionYear());

        // Dates
        updated |= updateProductionDateIfNeeded(car, dto.productionDate());

        if (!updated) {
            throw new NoChangesDetectedException();
        }

        try {
            car = carRepository.save(car);
        } catch (Exception ex) {
            throw new EntitySavingException("Unexpected exception during saving car");
        }

        return carMapper.toDto(car);
    }


    private Car getCarSecurely(Long id) {
        CustomUserDetails user = authUtils.getCurrentUser();
        String role = user.getUserRole();

        return switch (role) {
            case "SECRETARY" -> carRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + id));

            case "CUSTOMER" -> carRepository.findCarByIdAndOwner_Id(id, user.getId())
                    .orElseThrow(() -> new AccessDeniedException("Car not found or access denied"));

            default -> throw new AccessDeniedException("Unauthorized to perform operation.");
        };
    }


    private boolean updateSerialNumberIfNeeded(Car car, String newSerialNumber) {
        if (newSerialNumber == null || newSerialNumber.isBlank()) return false;

        newSerialNumber = newSerialNumber.toUpperCase();

        if (car.getSerialNumber().equals(newSerialNumber)) return false;

        if (carRepository.existsBySerialNumber(newSerialNumber)) {
            throw new InvalidOperationException("Car with this Serial Number already exists.");
        }

        car.setSerialNumber(newSerialNumber);
        return true;
    }

    private boolean updateBrandIfNeeded(Car car, String brand) {
        if (brand == null || brand.isBlank()) return false;
        if (car.getBrand().equals(brand)) return false;
        car.setBrand(brand);
        return true;
    }

    private boolean updateModelIfNeeded(Car car, String model) {
        if (model == null || model.isBlank()) return false;
        if (car.getModel().equals(model)) return false;
        car.setModel(model);
        return true;
    }

    private boolean updateCarTypeIfNeeded(Car car, CarType carType) {
        if (carType == null) return false;
        if (car.getCarType() == carType) return false;
        car.setCarType(carType);
        return true;
    }

    private boolean updateFuelTypeIfNeeded(Car car, FuelType fuelType) {
        if (fuelType == null) return false;
        if (car.getFuelType() == fuelType) return false;
        car.setFuelType(fuelType);
        return true;
    }

    private boolean updateDoorCountIfNeeded(Car car, Integer doorCount) {
        if (doorCount == null) return false;
        if (doorCount.equals(car.getDoorCount())) return false;
        car.setDoorCount(doorCount);
        return true;
    }

    private boolean updateWheelCountIfNeeded(Car car, Integer wheelCount) {
        if (wheelCount == null) return false;
        if (wheelCount.equals(car.getWheelCount())) return false;
        car.setWheelCount(wheelCount);
        return true;
    }

    private boolean updateAcquisitionYearIfNeeded(Car car, Integer acquisitionYear) {
        if (acquisitionYear == null) return false;
        if (acquisitionYear.equals(car.getAcquisitionYear())) return false;
        car.setAcquisitionYear(acquisitionYear);
        return true;
    }

    private boolean updateProductionDateIfNeeded(Car car, LocalDate productionDate) {
        if (productionDate == null) return false;
        if (productionDate.isEqual(car.getProductionDate())) return false;
        car.setProductionDate(productionDate);
        return true;
    }



    public String addCarsByCsv(@ValidCsvFileType MultipartFile file, Long requestedOwnerId) {
        try {
            Long finalOwnerId = resolveImportOwnerId(requestedOwnerId);


            List<CarFileRepresentation> carsFromFile = csvParser.parseCsv(file, CarFileRepresentation.class);
            return processCarsFileRepresentations(carsFromFile, finalOwnerId);

        } catch (IOException e) {
            log.error("Error processing CSV", e);
            throw new ParsingFileIOException();
        }
    }


    public String addCarsByExcel(@ValidExcelFileType MultipartFile file, Long requestedOwnerId) {
        try (InputStream inputStream = file.getInputStream()) {
            Long finalOwnerId = resolveImportOwnerId(requestedOwnerId);

            List<CarFileRepresentation> carsFromFile = Poiji.fromExcel(inputStream, PoijiExcelType.XLSX, CarFileRepresentation.class);

            return processCarsFileRepresentations(carsFromFile, finalOwnerId);

        } catch (IOException e) {
            log.error("Error processing Excel", e);
            throw new ParsingFileIOException();
        }
    }

    private Long resolveImportOwnerId(Long requestedOwnerId) {
        CustomUserDetails user = authUtils.getCurrentUser();
        String role = user.getUserRole();

        if ("CUSTOMER".equals(role)) {
            return user.getId();
        }

        if ("SECRETARY".equals(role)) {
            return requestedOwnerId;
        }

        throw new AccessDeniedException("Unauthorized to import cars");
    }


    private String processCarsFileRepresentations(List<CarFileRepresentation> carsFromFile, Long forcedOwnerId) {

        Set<CarRequestDto> carDtos = carMapper.mapFileRepresentationToDto(carsFromFile, forcedOwnerId);

        Set<CarRequestDto> validCarDtos = genericValidator.validateAndFilter(carDtos);

        int sumOfSavedCars = saveMultipleCars(validCarDtos);

        if (sumOfSavedCars == 0 && !carsFromFile.isEmpty()) {
            return "Failed to save cars. Check errors logs.";
        }
        return "Successfully processed. Saved " + sumOfSavedCars + " cars.";
    }


    public int saveMultipleCars(Set<CarRequestDto> cars) {
        AtomicInteger sum = new AtomicInteger();
        CarService self = selfProvider.getObject();
        cars.forEach(carDto -> {
            try {
                self.create(carDto);
                sum.getAndIncrement();
            } catch (Exception ex) {
                log.warn("Failed to save car with SN {}: Missing Owner ID or other error.", carDto.serialNumber());
            }
        });
        return sum.get();
    }


}
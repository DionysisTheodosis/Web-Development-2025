package gr.aegean.icsd.autorepair.car;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER','SECRETARY')")
    public ResponseEntity<CarResponseDto> createCar(@Valid @RequestBody CarRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER','SECRETARY','MECHANIC')")
    public ResponseEntity<CarResponseDto> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'SECRETARY')")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/bulk-delete")
    public ResponseEntity<Void> deleteMultipleCars(@RequestBody List<Long> ids) {
        carService.bulkDelete(ids);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'SECRETARY')")
    public ResponseEntity<?> searchCars(
            @ParameterObject @PageableDefault(sort = "productionDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String ownerTaxNumber,
            @RequestParam(required = false) String ownerUsername,
            @RequestParam(required = false) Long mechanicId,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String serialNumber,
            @RequestParam(required = false) CarType carType,
            @RequestParam(required = false) FuelType fuelType,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean withOwnerInfo,
            PagedResourcesAssembler<CarResponseDto> assembler
    ) {
        return ResponseEntity.ok(assembler.toModel(
                carService.searchCars(
                        pageable, ownerId,mechanicId,ownerTaxNumber,ownerUsername,brand, model, serialNumber,
                        carType, fuelType, dateFrom, dateTo, keyword,withOwnerInfo
                )
        ));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CarResponseDto> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CarUpdateDto request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(carService.update(id, request));
    }




    @PreAuthorize("hasAnyAuthority('SECRETARY', 'CUSTOMER')")
    @PostMapping(value = "/csv", consumes = {"multipart/form-data"})
    public ResponseEntity<String> addCarsByCsv(
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) Long ownerId
    ) {
        return ResponseEntity.ok(carService.addCarsByCsv(file, ownerId));
    }


    @PreAuthorize("hasAnyAuthority('SECRETARY', 'CUSTOMER')")
    @PostMapping(value = "/excel", consumes = {"multipart/form-data"})
    public ResponseEntity<String> addCarsByExcel(
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) Long ownerId
    ) {
        return ResponseEntity.ok(carService.addCarsByExcel(file, ownerId));
    }


    @PreAuthorize("hasAnyAuthority('SECRETARY', 'CUSTOMER')")
    @GetMapping("/template/{type}")
    public ResponseEntity<Resource> downloadTemplate(@PathVariable String type) {
        String filename = "cars_template." + ("excel".equalsIgnoreCase(type) ? "xlsx" : "csv");
        String mediaType = "excel".equalsIgnoreCase(type)
                ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                : "text/csv";

        Resource resource = new ClassPathResource("fileTemplates/" + filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(mediaType))
                .body(resource);
    }

}
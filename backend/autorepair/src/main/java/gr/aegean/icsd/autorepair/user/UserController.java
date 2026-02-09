package gr.aegean.icsd.autorepair.user;

import gr.aegean.icsd.autorepair.user.customer.Customer;
import gr.aegean.icsd.autorepair.user.customer.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User")
public class UserController {
    private final UserService userService;
    private final CustomerService customerService;
    private final PdfService pdfService;
    private final ExcelService excelService;

    @PreAuthorize("hasAuthority('SECRETARY')")
    @PostMapping("")
    public ResponseEntity<HttpStatus> createUser(@RequestBody @Valid UserRegistrationDto userRegistrationDto){
        userService.saveUser(userRegistrationDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasAnyAuthority('CUSTOMER','MECHANIC','SECRETARY')")
    @PatchMapping("/me/password")
    public ResponseEntity<HttpStatus> changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto,HttpServletResponse response) {
        this.userService.changePassword(changePasswordDto,response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasAnyAuthority('SECRETARY')")
    @PatchMapping("/{id}/active-account")
    public ResponseEntity<HttpStatus> modifyLocked(@PathVariable Long id,@RequestParam  boolean active) {
            this.userService.modifyLockedAccount(id,active);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasAnyAuthority('MECHANIC','CUSTOMER','SECRETARY')")
    @GetMapping("/me")
    public ResponseEntity<UserDetailsDto> getMineUserDetails() {
        UserDetailsDto user;
        user = this.userService.getUserEntityDetails();
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }


    @PreAuthorize("hasAuthority('SECRETARY')")
    @GetMapping("")
    public ResponseEntity<?> searchPageUsersByExactMatch(
            @ParameterObject
            @PageableDefault(page = 0, size = 10, sort = "lastName", direction = Sort.Direction.ASC)
            Pageable pageable,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "taxNumber", required = false) String taxNumber,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(required = false) UserRole role,
            PagedResourcesAssembler<UserDetailsDto> assembler
    ) {
        Page<UserDetailsDto> records = userService.searchPageUsers(
                username, lastName, taxNumber, keyword, pageable,role
        );

        return ResponseEntity.ok(assembler.toModel(records));
    }

    @PreAuthorize("hasAuthority('SECRETARY')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDto> getUserDetails(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.loadUserById(id));
    }

    @PreAuthorize("hasAuthority('SECRETARY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
            this.userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted Successfully");
    }

    @PreAuthorize("hasAuthority('SECRETARY')")
    @DeleteMapping("/bulk-delete")
    public ResponseEntity<String> deleteUser(@RequestBody Set<Long> ids) {
        this.userService.deleteUsers(ids);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted Successfully");
    }

    @PreAuthorize("hasAnyAuthority('CUSTOMER','MECHANIC')")
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyAccount(HttpServletResponse response) {
        this.userService.deleteMyAccount(response);
        return ResponseEntity.status(HttpStatus.OK).body("Your User Account Deleted Successfully");
    }

    //todo to change the info on the swagger for csv
    @Operation(
            description = """
                    Post endpoint to insert new users  by providing the csv file with users
                    and this can only been accessed from secretary.
                    
                    The Csv file will contain the following columns in this exact name and order :
                      - `FirstName`: Patients first name.
                      - `LastName`: Patients last name.
                      - `Email`: Patients unique email address.
                      - `Password`: Password for patients account.
                      - `PersonalID`: Patients unique personal id.
                      - `AMKA`: Patients unique AMKA.""",
            summary = "Post endpoint to create new users from csv file"
    )
    @PreAuthorize("hasAuthority('SECRETARY')")
    @PostMapping(value = "/csv", consumes = {"multipart/form-data"})
    ResponseEntity<String> addUsersByCsv(@RequestPart("file") MultipartFile file) {

        return ResponseEntity.ok(userService.addUsersByCsv(file));

    }
    //todo to change the info on the swagger for excel
    @Operation(
            description = """
                    Post endpoint to insert new users  by providing the excel file with users
                    and this can only been accessed from secretary.
                    
                    The Excel file will contain the following columns in this exact name and order :
                      - `FirstName`: Patients first name.
                      - `LastName`: Patients last name.
                      - `Email`: Patients unique email address.*
                      - `Password`: Password for patients account.
                      - `PersonalID`: Patients unique personal id.
                      - `AMKA`: Patients unique AMKA.""",
            summary = "Post endpoint to create new users from excel file"
    )
    @PreAuthorize("hasAuthority('SECRETARY')")
    @PostMapping(value = "/excel", consumes = {"multipart/form-data"})
    ResponseEntity<String> addUsersByExcel(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(userService.addUsersByExcel(file));
    }

    @PreAuthorize("hasAnyAuthority('SECRETARY','CUSTOMER','MECHANIC')")
    @PatchMapping(value = "/me")
    ResponseEntity<UserDetailsDto> updateOwnUserDetails(@RequestBody UserUpdateDto dto) {
        return ResponseEntity.ok(userService.updateOwnUserDetails(dto));
    }


    @PreAuthorize("hasAuthority('SECRETARY')")
    @GetMapping("/request-activation")
    public ResponseEntity<?> requestActivation(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder,
            @RequestParam(value = "keyword", required = false) String keyword,
            PagedResourcesAssembler<UserDetailsDto> assembler
    ) {
        Page<UserDetailsDto> records = userService.searchPageInactiveUsersByKeyword(
                keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder.toUpperCase()), sortBy))
        );

        return ResponseEntity.status(HttpStatus.OK).body(assembler.toModel(records));
    }
    @PreAuthorize("hasAuthority('SECRETARY')")
    @GetMapping("/template/{type}")
    public ResponseEntity<Resource> downloadTemplate(@PathVariable String type) {
        String filename = "users_template." + ("excel".equalsIgnoreCase(type) ? "xlsx" : "csv");
        String mediaType = "excel".equalsIgnoreCase(type)
                ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                : "text/csv";

        Resource resource = new ClassPathResource("fileTemplates/" + filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(mediaType))
                .body(resource);
    }


    @PreAuthorize("hasAuthority('SECRETARY')")
    @PatchMapping("/{id}")
    public  ResponseEntity<UserDetailsDto> updateUser(@PathVariable Long id ,@RequestBody @Valid UserUpdateDto dto){
        return ResponseEntity.ok(userService.updateUserById(id,dto));
    }
    @GetMapping("/{id}/export-profile/pdf")
    public ResponseEntity<byte[]> exportCustomerProfile(@PathVariable Long id) {
        try {
            // 1. Fetch Customer with Cars/Appointments (Ensure Transactional!)
            // You might need a method in UserService: userService.getCustomerWithDetails(id)
            Customer customer = customerService.getCustomerWithDetails(id);

            // 2. Generate PDF
            byte[] pdfBytes = pdfService.generateCustomerProfilePdf(customer);

            // 3. Download
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customer_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/export-profile/excel")
    public ResponseEntity<byte[]> exportCustomerProfileExcel(@PathVariable Long id) {
        try {
            // 1. Fetch SAME data as PDF (Reuse logic!)
            Customer customer = customerService.getCustomerWithDetails(id);

            // 2. Generate Excel
            byte[] excelBytes = excelService.generateCustomerProfileExcel(customer);

            // 3. Return File
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customer_" + id + ".xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelBytes);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

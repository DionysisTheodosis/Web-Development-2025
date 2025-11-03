package gr.aegean.icsd.autorepair.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAnyAuthority('CUSTOMER','MECHANIC','SECRETARY')")
    @PatchMapping("/me/password")
    public ResponseEntity<HttpStatus> changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto) {
        this.userService.changePassword(changePasswordDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasAnyAuthority('SECRETARY')")
    @PatchMapping("/{id}/active-account")
    public ResponseEntity<HttpStatus> modifyLocked(@PathVariable Long id,@RequestParam  boolean active) {
            this.userService.modifyLockedAccount(id,active);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

//    @PreAuthorize("hasAnyAuthority('MECHANIC','CUSTOMER','SECRETARY')")
//    @GetMapping("")
//    public ResponseEntity<List<UserDetailsDto>> getOwnUserDetails() {
//        List<UserDetailsDto> user;
//        try{
//            System.out.println("HOOOOOOOLA");
//        user = this.userRepository.findAllUserDetails();
//        }
//        catch (Exception e){
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(user);
//    }

    @PreAuthorize("hasAuthority('SECRETARY')")
    @GetMapping("")
    public ResponseEntity<Set<UserDetailsDto>> getOwnUserDetails() {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.loadAllOtherUsers());
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

    @PreAuthorize("hasAnyAuthority('CUSTOMER','MECHANIC')")
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyAccount(HttpServletResponse response) {
        this.userService.deleteMyAccount(response);
        return ResponseEntity.status(HttpStatus.OK).body("Your User Account Deleted Successfully");
    }

    @GetMapping("/search-by-exact-match")
    @PreAuthorize("hasAuthority('SECRETARY')")
    public ResponseEntity<Page<UserDetailsDto>> searchPageUsersByExactMatch(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "lastName") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "taxNumber", required = false) String taxNumber
    ) {
        Page<UserDetailsDto> records = userService.searchPageUsers(
                username, lastName, taxNumber, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder.toUpperCase()), sortBy))
        );

        return ResponseEntity.status(HttpStatus.OK).body(records);
    }


    @GetMapping("/search-by-keyword")
    @PreAuthorize("hasAuthority('SECRETARY')")
    public ResponseEntity<Page<UserDetailsDto>> searchPageUsersByKeyword(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "lastName") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        Page<UserDetailsDto> records = userService.searchPageUsersByKeyword(
              keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder.toUpperCase()), sortBy))
        );

        return ResponseEntity.status(HttpStatus.OK).body(records);
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

    //todo to make the update of userdetails by secretary

    @PreAuthorize("hasAuthority('SECRETARY')")
    @GetMapping("/request-activation")
    public ResponseEntity<Page<UserDetailsDto>> requestActivation(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        Page<UserDetailsDto> records = userService.searchPageInactiveUsersByKeyword(
                keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder.toUpperCase()), sortBy))
        );

        return ResponseEntity.status(HttpStatus.OK).body(records);
    }


/*    @PreAuthorize("hasAnyAuthority('DOCTOR','PATIENT','SECRETARY')")
    @PatchMapping("/me/account-details")
    public ResponseEntity<UserDetailsDto> updateOwnUserDetails(@RequestBody @Valid UserDetailsDto userDetailsDto) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.changeOwnUserDetails(userDetailsDto));
    }*/
}

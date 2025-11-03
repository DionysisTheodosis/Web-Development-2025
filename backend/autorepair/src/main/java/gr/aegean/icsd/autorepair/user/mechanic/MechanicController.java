package gr.aegean.icsd.autorepair.user.mechanic;

import gr.aegean.icsd.autorepair.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MechanicController {
    private final MechanicService mechanicService;

    //todo να τα φτιάξω δεν τα έχω υλοποιημενα
    @PreAuthorize("hasAuthority('SECRETARY')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDto> getMechanicDetails(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.mechanicService.loadMechanicById(id));
    }


    @GetMapping("/search-by-exact-match")
    @PreAuthorize("hasAuthority('SECRETARY')")
    public ResponseEntity<Page<UserDetailsDto>> searchPageMechanicsByExactMatch(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "lastName") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "taxNumber", required = false) String taxNumber
    ) {
        Page<UserDetailsDto> records = mechanicService.searchPageMechanics(
                username, lastName, taxNumber, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder.toUpperCase()), sortBy))
        );

        return ResponseEntity.status(HttpStatus.OK).body(records);
    }


    @GetMapping("/search-by-keyword")
    @PreAuthorize("hasAuthority('SECRETARY')")
    public ResponseEntity<Page<UserDetailsDto>> searchPageMechanicsByKeyword(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "lastName") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        Page<UserDetailsDto> records = mechanicService.searchPageMechanicsByKeyword(
                keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder.toUpperCase()), sortBy))
        );

        return ResponseEntity.status(HttpStatus.OK).body(records);
    }
}

package gr.aegean.icsd.autorepair.user;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.hateoas.server.core.Relation;

@Builder
@Relation(collectionRelation = "users", itemRelation = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDetailsDto(
        Long id,
        String username,
        String firstName,
        String lastName,
        String email,
        String identityNumber,
        String role,
        Boolean active,
        String specialty,
        String address,
        String taxNumber
) {
}
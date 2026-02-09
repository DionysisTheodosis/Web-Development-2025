package gr.aegean.icsd.autorepair.shared.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import gr.aegean.icsd.autorepair.user.UserRole;
import lombok.Builder;
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthInfoDto(
        Long id,
        String firstName,
        String lastName,
        String username,
        UserRole role
) {
}

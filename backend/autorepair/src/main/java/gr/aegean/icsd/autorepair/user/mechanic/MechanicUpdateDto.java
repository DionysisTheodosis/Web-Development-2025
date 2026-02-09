package gr.aegean.icsd.autorepair.user.mechanic;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.aegean.icsd.autorepair.user.UserUpdateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Schema(
        description = "Mechanic Update DTO",
        example = "{" +
                "  \"username\": \"Dionysis\"," +
                "  \"email\": \"theo_32@htm.com\"," +
                "  \"firstName\": \"Dionysis\"," +
                "  \"lastName\": \"Dionysis\"," +
                "  \"identityNumber\": \"AT123456\"," +
                "  \"specialty\": \"Engineer\"" +
                "}"
)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
public class MechanicUpdateDto extends UserUpdateDto {
    @JsonProperty("specialty")
    private String specialty;

}
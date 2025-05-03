package gr.aegean.icsd.autorepair.user.mechanic;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.aegean.icsd.autorepair.user.UserUpdateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Schema(
        description = "Mechanic Update DTO",
        example = "{" +
                "  \"username\": \"Dionysis\"," +
                "  \"email\": \"theo_32@htm.com\"," +
                "  \"firstName\": \"Dionysis\"," +
                "  \"lastName\": \"Dionysis\"," +
                "  \"identityNumber\": \"AT123456\"," +
                "  \"specialty\": \"Computer Engineer\"" +
                "}"
)
@SuperBuilder
@Getter
public class MechanicUpdateDto extends UserUpdateDto {

    @NotEmpty(message = "Specialization is required for mechanic")
    @JsonProperty("specialty")
    private final String specialty;



}
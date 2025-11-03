package gr.aegean.icsd.autorepair.user.mechanic;


import com.fasterxml.jackson.annotation.JsonProperty;
import gr.aegean.icsd.autorepair.user.UserRegistrationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Schema(
        description = "Mechanic registration DTO",
        example = "{" +
                "  \"username\": \"Dionysis\"," +
                "  \"password\": \"Dion123@!\"," +
                "  \"email\": \"theo_32@htm.com\"," +
                "  \"firstName\": \"Dionysis\"," +
                "  \"lastName\": \"Dionysis\"," +
                "  \"identityNumber\": \"AT123456\"," +
                "  \"role\": \"mechanic\"," +
                "  \"specialty\": \"Computer Engineer\"" +
                "}"
)
@SuperBuilder
@Getter
public class MechanicRegistrationDto extends UserRegistrationDto {

    @NotEmpty(message = "Specialization is required for mechanic")
    @JsonProperty("specialty")
    private final String specialty;



}

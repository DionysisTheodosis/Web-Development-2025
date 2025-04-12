package gr.aegean.icsd.autorepair.shared.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(name = "Login")
@Builder
public record LoginRequestDto(

        @Schema(example = "johndoe",requiredMode = REQUIRED)
        //@Email(message = "Invalid email address")
        @JsonProperty(value = "username")
        String username,

        @Schema(example = "pass0",requiredMode = REQUIRED)
        @NotBlank(message = "Password cannot be blank")
        @JsonProperty(value = "password")
        String password


) {

}



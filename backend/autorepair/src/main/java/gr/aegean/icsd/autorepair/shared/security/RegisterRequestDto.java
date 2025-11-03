package gr.aegean.icsd.autorepair.shared.security;


import gr.aegean.icsd.autorepair.shared.validators.ValidTaxNumber;
import gr.aegean.icsd.autorepair.shared.validators.ValidName;
import gr.aegean.icsd.autorepair.shared.validators.ValidPassword;
import gr.aegean.icsd.autorepair.shared.validators.ValidPersonalID;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RegisterRequestDto(

        @ValidName
        String firstName,

        @ValidName
        String lastName,

        @Email(message = "Invalid email format")
        String email,

        @ValidPassword
        @NotBlank(message = "Password cannot be blank")
        String password,

        @ValidPersonalID
        String personalID,

        @ValidTaxNumber
        String amka


) {
}

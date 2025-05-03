package gr.aegean.icsd.autorepair.user.customer;


import gr.aegean.icsd.autorepair.shared.validators.ValidName;
import gr.aegean.icsd.autorepair.shared.validators.ValidPersonalID;
import jakarta.validation.constraints.Email;
import lombok.Builder;

@Builder
public record CustomerDetailsDto(

        @ValidName
        String firstName,

        @ValidName
        String lastName,

        @Email(message = "Invalid email format")
        String email,

        @ValidPersonalID
        String personalID,
        String taxNumber,
        String address

) {
}

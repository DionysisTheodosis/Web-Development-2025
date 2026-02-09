package gr.aegean.icsd.autorepair.user.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.aegean.icsd.autorepair.user.UserRegistrationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Schema(
        description = "Customer registration DTO",
        example = "{" +
                "  \"username\": \"JaneDoe\"," +
                "  \"password\": \"Jane123@!\"," +
                "  \"email\": \"jane_doe@example.com\"," +
                "  \"firstName\": \"Jane\"," +
                "  \"lastName\": \"Doe\"," +
                "  \"identityNumber\": \"AT654321\"," +
                "  \"role\": \"customer\"," +
                "  \"taxNumber\": \"TAX12345\"," +
                "  \"address\": \"123 Main St\"" +
                "}"
)
@Getter
@SuperBuilder
@Jacksonized
public class CustomerRegistrationDto extends UserRegistrationDto {

    @JsonProperty("taxNumber")
    private  String taxNumber;
    // todo i need to make the address validator
    @JsonProperty("address")
    private   String address;
}

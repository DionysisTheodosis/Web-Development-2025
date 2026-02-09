package gr.aegean.icsd.autorepair.user.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.aegean.icsd.autorepair.user.UserUpdateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Schema(
        description = "Customer Update DTO",
        example = "{" +
                "  \"username\": \"JaneDoe\"," +
                "  \"email\": \"jane_doe@example.com\"," +
                "  \"firstName\": \"Jane\"," +
                "  \"lastName\": \"Doe\"," +
                "  \"identityNumber\": \"AT654321\"," +
                "  \"taxNumber\": \"TAX12345\"," +
                "  \"address\": \"123 Main St\"" +
                "}"
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder
public class CustomerUpdateDto extends UserUpdateDto {

    @JsonProperty("taxNumber")
    private  String taxNumber;

    @JsonProperty("address")
    private   String address;
}
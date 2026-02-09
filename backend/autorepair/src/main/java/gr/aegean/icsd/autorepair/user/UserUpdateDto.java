package gr.aegean.icsd.autorepair.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gr.aegean.icsd.autorepair.shared.validators.ValidName;
import gr.aegean.icsd.autorepair.shared.validators.ValidPersonalID;
import gr.aegean.icsd.autorepair.user.customer.CustomerUpdateDto;
import gr.aegean.icsd.autorepair.user.mechanic.MechanicRegistrationDto;
import gr.aegean.icsd.autorepair.user.mechanic.MechanicUpdateDto;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
@JsonTypeInfo(use= JsonTypeInfo.Id.SIMPLE_NAME, include= JsonTypeInfo.As.PROPERTY, property="role" , visible = true )
@JsonSubTypes({
        @JsonSubTypes.Type(value = gr.aegean.icsd.autorepair.user.mechanic.MechanicUpdateDto.class, name = "mechanic"),
        @JsonSubTypes.Type(value = gr.aegean.icsd.autorepair.user.customer.CustomerUpdateDto.class, name = "customer"),
        @JsonSubTypes.Type(value = gr.aegean.icsd.autorepair.user.SecretaryUpdateDto.class, name = "secretary")
})

@Schema(
        description = "User update base DTO",
        discriminatorProperty = "role",
        oneOf = {MechanicRegistrationDto.class, CustomerUpdateDto.class},
        discriminatorMapping = {
                @DiscriminatorMapping(value = "mechanic", schema = MechanicUpdateDto.class),
                @DiscriminatorMapping(value = "customer", schema = CustomerUpdateDto.class)
        }
)
@SuperBuilder
@Getter
@NoArgsConstructor
public abstract class UserUpdateDto   {
    @JsonProperty("username")
    protected String username;
    @JsonProperty("email")
    protected  String email;

    @JsonProperty("firstname")
    protected String firstName;

    @JsonProperty("lastname")
    protected String lastName;

    @JsonProperty("identityNumber")
    protected  String identityNumber;
}

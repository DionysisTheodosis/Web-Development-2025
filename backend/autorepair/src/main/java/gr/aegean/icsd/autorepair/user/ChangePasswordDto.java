package gr.aegean.icsd.autorepair.user;



import gr.aegean.icsd.autorepair.shared.validators.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChangePasswordDto(


        @NotBlank(message = "Old Password cannot be blank")
        String oldPassword,

        @ValidPassword
        @NotBlank(message = "New Password cannot be blank")
        String newPassword
) {
}

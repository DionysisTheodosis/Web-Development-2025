package gr.aegean.icsd.autorepair.user;


import com.opencsv.bean.CsvBindByName;
import com.poiji.annotation.ExcelCellName;
import gr.aegean.icsd.autorepair.shared.validators.ValidName;
import gr.aegean.icsd.autorepair.shared.validators.ValidPersonalID;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class UserFileRepresentation {
    String username;

    @ExcelCellName("Email")
    @Email
    @CsvBindByName(column = "Email")
    String email;

    @ExcelCellName("Password")
    @NotBlank
    @CsvBindByName(column = "Password")
    String password;

    @ExcelCellName("FirstName")
    @ValidName
    @CsvBindByName(column = "FirstName")
    String firstName;

    @ExcelCellName("LastName")
    @ValidName
    @CsvBindByName(column = "LastName")
    String lastName;

    @ExcelCellName("IdentityNumber")
    @ValidPersonalID
    @CsvBindByName(column = "IdentityNumber")
    String identityNumber;

    @ExcelCellName("Role")
    @ValidPersonalID
    @CsvBindByName(column = "Role")
    String role;

    @ExcelCellName("TaxNumber")
    @CsvBindByName(column = "TaxNumber")
    String taxNumber;

}


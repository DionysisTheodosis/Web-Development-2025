package gr.aegean.icsd.autorepair.user;


import com.opencsv.bean.CsvBindByName;
import com.poiji.annotation.ExcelCellName;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class UserFileRepresentation {
    @ExcelCellName("Username")
    @CsvBindByName(column = "Username")
    String username;

    @ExcelCellName("Email")
    @CsvBindByName(column = "Email")
    String email;

    @ExcelCellName("Password")
    @CsvBindByName(column = "Password")
    String password;

    @ExcelCellName("FirstName")
    @CsvBindByName(column = "FirstName")
    String firstName;

    @ExcelCellName("LastName")
    @CsvBindByName(column = "LastName")
    String lastName;

    @ExcelCellName("IdentityNumber")
    @CsvBindByName(column = "IdentityNumber")
    String identityNumber;

    @ExcelCellName("Role")
    @CsvBindByName(column = "Role")
    UserRole role;

    @ExcelCellName("TaxNumber")
    @CsvBindByName(column = "TaxNumber")
    String taxNumber;

    @ExcelCellName("Address")
    @CsvBindByName(column = "Address")
    String address;

    @ExcelCellName("Specialty")
    @CsvBindByName(column = "Specialty")
    String specialty;

}


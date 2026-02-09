package gr.aegean.icsd.autorepair.shared.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ExcelFileTypeValidator  implements ConstraintValidator<ValidExcelFileType, MultipartFile> {

    @Override
    public void initialize(ValidExcelFileType constraintAnnotation) {
        //It's empty because we don't need it
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.getContentType() == null) {
            return false;
        }

        String contentType = file.getContentType();
        boolean isValidContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType);

        String fileName = file.getOriginalFilename();
        boolean hasXlsxExtension = fileName != null && fileName.toLowerCase().endsWith(".xlsx");

        return isValidContentType || hasXlsxExtension;
    }
}

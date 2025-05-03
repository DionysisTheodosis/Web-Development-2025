package gr.aegean.icsd.autorepair.shared.utils;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class CsvParser {

    public <T> List<T> parseCsv(MultipartFile file, Class<T> clazz) throws IOException {
        List<T> results = new ArrayList<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(clazz);
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            for (T bean : csvToBean) {
                try {
                    results.add(bean);
                } catch (Exception e) {
                    log.error("Error parsing CSV line, skipping: {}", e.getMessage());
                    // Skip to the next line
                }
            }
        }
        return results;
    }
}

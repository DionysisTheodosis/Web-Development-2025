package gr.aegean.icsd.autorepair.car;

import com.opencsv.bean.CsvDate;
import com.poiji.annotation.ExcelCellName;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CarFileRepresentation {


    @ExcelCellName("Serial Number")
    @CsvBindByName(column = "Serial Number")
    private String serialNumber;

    @ExcelCellName("Brand")
    @CsvBindByName(column = "Brand")
    private String brand;


    @ExcelCellName("Model")
    @CsvBindByName(column = "Model")
    private String model;


    @ExcelCellName("Car Type")
    @CsvBindByName(column = "Car Type")
    private String carType;


    @ExcelCellName("Fuel Type")
    @CsvBindByName(column = "Fuel Type")
    private String fuelType;


    @ExcelCellName("Door Count")
    @CsvBindByName(column = "Door Count")
    private Integer doorCount;


    @ExcelCellName("Wheel Count")
    @CsvBindByName(column = "Wheel Count")
    private Integer wheelCount;

    @ExcelCellName("Production Date")
    @CsvBindByName(column = "Production Date")
    @CsvDate("dd-MM-yyyy")
    private LocalDate productionDate;

    @ExcelCellName("Acquisition Year")
    @CsvBindByName(column = "Acquisition Year")
    private Integer acquisitionYear;

    @ExcelCellName("OwnerId")
    @CsvBindByName(column = "OwnerId")
    private Long ownerId;
}

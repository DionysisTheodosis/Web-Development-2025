package gr.aegean.icsd.autorepair.user;

import gr.aegean.icsd.autorepair.appointment.Appointment;
import gr.aegean.icsd.autorepair.car.Car;
import gr.aegean.icsd.autorepair.user.customer.Customer;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class ExcelService {

    public byte[] generateCustomerProfileExcel(Customer customer) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);

            // --- SHEET 1: PROFILE ---
            Sheet profileSheet = workbook.createSheet("Profile");
            createProfileSection(profileSheet, customer, headerStyle);

            // --- SHEET 2: CARS ---
            Sheet carSheet = workbook.createSheet("Cars");
            createCarsSection(carSheet, customer, headerStyle, dateStyle);

            // --- SHEET 3: APPOINTMENTS ---
            Sheet apptSheet = workbook.createSheet("Appointments");
            createAppointmentsSection(apptSheet, customer, headerStyle, dateStyle);

            // Auto-size columns for all sheets
            autoSizeColumns(profileSheet, 2);
            autoSizeColumns(carSheet, 8);
            autoSizeColumns(apptSheet, 5);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void createProfileSection(Sheet sheet, Customer customer, CellStyle headerStyle) {
        int rowIdx = 0;

        addRow(sheet, rowIdx++, headerStyle, "Full Name", customer.getFirstName() + " " + customer.getLastName());
        addRow(sheet, rowIdx++, headerStyle, "Email", customer.getEmail());
        addRow(sheet, rowIdx++, headerStyle, "Tax Number (AFM)", customer.getTaxNumber());
        addRow(sheet, rowIdx++, headerStyle, "Personal ID", customer.getIdentityNumber());
        addRow(sheet, rowIdx++, headerStyle, "Address", customer.getAddress());
    }

    private void createCarsSection(Sheet sheet, Customer customer, CellStyle headerStyle, CellStyle dateStyle) {
        // Headers
        Row headerRow = sheet.createRow(0);
        String[] headers = {"VIN", "Brand", "Model", "Type", "Year", "Fuel", "Prod. Date", "Doors"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data
        int rowIdx = 1;
        if (customer.getCars() != null) {
            for (Car car : customer.getCars()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(car.getSerialNumber());
                row.createCell(1).setCellValue(car.getBrand());
                row.createCell(2).setCellValue(car.getModel());
                row.createCell(3).setCellValue(car.getCarType() != null ? car.getCarType().name() : "-");
                row.createCell(4).setCellValue(car.getAcquisitionYear());
                row.createCell(5).setCellValue(car.getFuelType() != null ? car.getFuelType().name() : "-");

                Cell dateCell = row.createCell(6);
                if (car.getProductionDate() != null) {
                    dateCell.setCellValue(car.getProductionDate());
                    dateCell.setCellStyle(dateStyle);
                } else {
                    dateCell.setCellValue("-");
                }

                row.createCell(7).setCellValue(car.getDoorCount());
            }
        }
    }

    private void createAppointmentsSection(Sheet sheet, Customer customer, CellStyle headerStyle, CellStyle dateStyle) {
        // Headers
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Date", "Reason", "Cost", "Status", "Description"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data
        int rowIdx = 1;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        if (customer.getAppointments() != null) {
            for (Appointment appt : customer.getAppointments()) {
                Row row = sheet.createRow(rowIdx++);

                // Date String (Excel handles LocalDates differently, usually easier to format as string for display)
                row.createCell(0).setCellValue(appt.getDateTime() != null ? appt.getDateTime().format(dtf) : "-");
                row.createCell(1).setCellValue(appt.getReason() != null ? appt.getReason().name() : "-");
                row.createCell(2).setCellValue(appt.getTotalCost() != null ? appt.getTotalCost().doubleValue() : 0.0);
                row.createCell(3).setCellValue(appt.getStatus() != null ? appt.getStatus().name() : "-");
                row.createCell(4).setCellValue(appt.getProblemDescription());
            }
        }
    }

    // --- Helpers ---

    private void addRow(Sheet sheet, int rowIdx, CellStyle style, String label, String value) {
        Row row = sheet.createRow(rowIdx);
        Cell cellKey = row.createCell(0);
        cellKey.setCellValue(label);
        cellKey.setCellStyle(style);

        Cell cellValue = row.createCell(1);
        cellValue.setCellValue(value != null ? value : "-");
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("dd-mm-yyyy"));
        return style;
    }

    private void autoSizeColumns(Sheet sheet, int cols) {
        for (int i = 0; i < cols; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
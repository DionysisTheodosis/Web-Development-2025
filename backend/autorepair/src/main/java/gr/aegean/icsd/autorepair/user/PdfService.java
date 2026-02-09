package gr.aegean.icsd.autorepair.user;

import gr.aegean.icsd.autorepair.user.customer.Customer;
import gr.aegean.icsd.autorepair.car.Car;
import gr.aegean.icsd.autorepair.appointment.Appointment;
import org.openpdf.text.*;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public byte[] generateCustomerProfilePdf(Customer customer) throws IOException {

        Document document = new Document(PageSize.A4);
        document.setMargins(30, 30, 40, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            BaseFont bf = BaseFont.createFont("/fonts/Roboto-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            Font titleFont = new Font(bf, 18, Font.BOLD, new java.awt.Color(44, 62, 80));
            Font sectionHeaderFont = new Font(bf, 11, Font.BOLD, java.awt.Color.WHITE);
            Font labelFont = new Font(bf, 9, Font.BOLD, java.awt.Color.DARK_GRAY);
            Font valueFont = new Font(bf, 9, Font.NORMAL);
            Font tableHeaderFont = new Font(bf, 8, Font.BOLD, java.awt.Color.WHITE);
            Font tableBodyFont = new Font(bf, 8, Font.NORMAL);

            writer.setPageEvent(new FooterEvent(new Font(bf, 8, Font.ITALIC, java.awt.Color.GRAY)));
            document.open();


            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);

            PdfPCell titleCell = new PdfPCell(new Phrase("CUSTOMER PROFILE REPORT / ΑΝΑΦΟΡΑ ΠΕΛΑΤΗ", titleFont));
            titleCell.setBorder(Rectangle.BOTTOM);
            titleCell.setBorderWidth(2f);
            titleCell.setBorderColor(new java.awt.Color(44, 62, 80));
            titleCell.setPaddingBottom(10);
            headerTable.addCell(titleCell);

            document.add(headerTable);
            document.add(new Paragraph("Generated on: " + LocalDate.now().format(dateFormatter), valueFont));
            document.add(new Paragraph(" "));


            document.add(createSectionHeader("CLIENT DETAILS / ΣΤΟΙΧΕΙΑ ΠΕΛΑΤΗ", sectionHeaderFont));

            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1.2f, 2f, 1.2f, 2f});
            infoTable.setSpacingBefore(10);
            infoTable.setSpacingAfter(20);

            addDetailRow(infoTable, "Full Name (Oν/Επώνυμο):", customer.getFirstName() + " " + customer.getLastName(),
                    "Tax ID (ΑΦΜ):", customer.getTaxNumber(), labelFont, valueFont);
            addDetailRow(infoTable, "Email:", customer.getEmail(),
                    "Identity No (Α.Τ):", customer.getIdentityNumber(), labelFont, valueFont);
            addDetailRow(infoTable, "Address (Διεύθυνση):", customer.getAddress(),
                    "", "", labelFont, valueFont);

            document.add(infoTable);


            document.add(createSectionHeader("VEHICLES / ΟΧΗΜΑΤΑ", sectionHeaderFont));

            if (customer.getCars() != null && !customer.getCars().isEmpty()) {
                PdfPTable carTable = new PdfPTable(9);
                carTable.setWidthPercentage(100);
                carTable.setSpacingBefore(10);
                carTable.setSpacingAfter(20);

                carTable.setWidths(new float[]{2.2f, 1.5f, 1.5f, 1.2f, 0.6f, 0.6f, 1.2f, 1.5f, 0.9f});
                carTable.setHeaderRows(1);

                String[] headers = {"VIN", "Brand", "Model", "Type", "Dr", "Wh", "Fuel", "Prod.", "Acq."};
                for (String h : headers) addTableHeader(carTable, h, tableHeaderFont);

                int rowCount = 0;
                for (Car car : customer.getCars()) {
                    java.awt.Color bgColor = (rowCount++ % 2 == 0) ? java.awt.Color.WHITE : new java.awt.Color(245, 245, 245);

                    addTableCell(carTable, car.getSerialNumber(), tableBodyFont, bgColor, Element.ALIGN_LEFT);
                    addTableCell(carTable, car.getBrand(), tableBodyFont, bgColor, Element.ALIGN_LEFT);
                    addTableCell(carTable, car.getModel(), tableBodyFont, bgColor, Element.ALIGN_LEFT);
                    addTableCell(carTable, car.getCarType() != null ? car.getCarType().name() : "-", tableBodyFont, bgColor, Element.ALIGN_LEFT);
                    addTableCell(carTable, String.valueOf(car.getDoorCount()), tableBodyFont, bgColor, Element.ALIGN_CENTER);
                    addTableCell(carTable, String.valueOf(car.getWheelCount()), tableBodyFont, bgColor, Element.ALIGN_CENTER);
                    addTableCell(carTable, car.getFuelType() != null ? car.getFuelType().name() : "-", tableBodyFont, bgColor, Element.ALIGN_LEFT);
                    addTableCell(carTable,
                            car.getProductionDate() != null ? car.getProductionDate().format(dateFormatter) : "-",
                            tableBodyFont, bgColor, Element.ALIGN_CENTER);
                    addTableCell(carTable, String.valueOf(car.getAcquisitionYear()), tableBodyFont, bgColor, Element.ALIGN_CENTER);
                }
                document.add(carTable);
            } else {
                document.add(new Paragraph("No registered vehicles found. / Δεν βρέθηκαν εγγραφές αυτοκινήτων ", valueFont));
                document.add(new Paragraph(" "));
            }

            document.add(createSectionHeader("APPOINTMENTS / ΡΑΝΤΕΒΟΥ", sectionHeaderFont));

            if (customer.getAppointments() != null && !customer.getAppointments().isEmpty()) {
                PdfPTable apptTable = new PdfPTable(5);
                apptTable.setWidthPercentage(100);
                apptTable.setSpacingBefore(10);
                apptTable.setSpacingAfter(20);
                apptTable.setHeaderRows(1);

                apptTable.setWidths(new float[]{1.5f, 1.5f, 4.0f, 1.2f, 1.5f});

                String[] apptHeaders = {"Date", "Reason", "Description", "Status", "Cost"};
                for (String h : apptHeaders) addTableHeader(apptTable, h, tableHeaderFont);

                DecimalFormat cf = new DecimalFormat("#,##0.00€");
                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yy");

                int rowCount = 0;
                for (Appointment a : customer.getAppointments()) {
                    java.awt.Color bgColor = (rowCount++ % 2 == 0) ? java.awt.Color.WHITE : new java.awt.Color(245, 245, 245);

                    addTableCell(apptTable, a.getDateTime() != null ? a.getDateTime().format(df) : "-", tableBodyFont, bgColor, Element.ALIGN_CENTER);
                    addTableCell(apptTable, String.valueOf(a.getReason()), tableBodyFont, bgColor, Element.ALIGN_LEFT);
                    addTableCell(apptTable, a.getProblemDescription(), tableBodyFont, bgColor, Element.ALIGN_LEFT);
                    addTableCell(apptTable, String.valueOf(a.getStatus()), tableBodyFont, bgColor, Element.ALIGN_CENTER);
                    addTableCell(apptTable, a.getTotalCost() != null ? cf.format(a.getTotalCost()) : "-", tableBodyFont, bgColor, Element.ALIGN_RIGHT);
                }
                document.add(apptTable);
            } else {
                document.add(new Paragraph("No appointments found. / Δεν βρέθηκαν Ραντεβού.", valueFont));
            }

            document.close();
        } catch (DocumentException e) {
            throw new IOException("PDF Generation Failed", e);
        }
        return out.toByteArray();
    }



    private PdfPTable createSectionHeader(String title, Font font) {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase(title, font));
        cell.setBackgroundColor(new java.awt.Color(44, 62, 80));
        cell.setPadding(6);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
        return table;
    }

    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new java.awt.Color(52, 73, 94));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        cell.setBorderColor(java.awt.Color.WHITE);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font, java.awt.Color bgColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "-", font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(5);
        cell.setBorderColor(java.awt.Color.LIGHT_GRAY);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }

    private void addDetailRow(PdfPTable table, String l1, String v1, String l2, String v2, Font lf, Font vf) {

        addBorderedCell(table, l1, lf);
        addBorderedCell(table, v1, vf);


        if ((l2 == null || l2.isEmpty()) && (v2 == null || v2.isEmpty())) {
            PdfPCell empty = new PdfPCell(new Phrase(""));
            empty.setBorder(Rectangle.NO_BORDER);
            table.addCell(empty);
            table.addCell(empty);
        } else {
            addBorderedCell(table, l2, lf);
            addBorderedCell(table, v2, vf);
        }
    }

    private void addBorderedCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "-", font));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(java.awt.Color.LIGHT_GRAY);
        cell.setPaddingBottom(5);
        cell.setPaddingTop(5);
        table.addCell(cell);
    }
}
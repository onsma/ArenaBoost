package tn.esprit.pidev.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.entities.Investment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    public byte[] exportInvestmentsToExcel(List<Investment> investments) throws IOException {
        // Create a new workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Investments");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] columns = {
                "Investment ID", "Investor ID", "Athlete ID", "Project ID", "Amount", "Investment Type",
                "Expected ROI", "Actual ROI", "Start Date", "End Date", "ROI Percentage", "Description",
                "Is Active", "Exit Amount", "Status", "Created At", "Updated At", "Dividend Payment Frequency",
                "Net Profit", "Current Value", "Investor Notes", "Currency", "Investor Satisfaction"
        };

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Fill data rows
        int rowNum = 1;
        for (Investment investment : investments) {
            Row row = sheet.createRow(rowNum++);

            // Set values for each cell
            row.createCell(0).setCellValue(investment.getInvestmentId());
            row.createCell(1).setCellValue(investment.getInvestor().getInvestorId());
            row.createCell(2).setCellValue(String.valueOf(investment.getAthlete() != null ? investment.getAthlete().getId_athelete() : "N/A"));
            row.createCell(3).setCellValue(String.valueOf(investment.getProject() != null ? investment.getProject().getId_project() : "N/A"));
            row.createCell(4).setCellValue(investment.getAmount());

            // Handle nullable fields
            row.createCell(5).setCellValue(investment.getInvestmentType() != null ? investment.getInvestmentType().toString() : "N/A");
            row.createCell(6).setCellValue(investment.getExpectedROI() != null ? investment.getExpectedROI() : 0.0); // Default to 0.0 if null
            row.createCell(7).setCellValue(investment.getActualROI() != null ? investment.getActualROI() : 0.0); // Default to 0.0 if null
            row.createCell(8).setCellValue(investment.getStartDate() != null ? investment.getStartDate().toString() : "N/A");
            row.createCell(9).setCellValue(investment.getEndDate() != null ? investment.getEndDate().toString() : "N/A");
            row.createCell(10).setCellValue(investment.getRoiPercentage() != null ? investment.getRoiPercentage() : 0.0); // Default to 0.0 if null
            row.createCell(11).setCellValue(investment.getDescription() != null ? investment.getDescription() : "N/A");
            row.createCell(12).setCellValue(investment.getActive() != null ? investment.getActive() : false); // Default to false if null
            row.createCell(13).setCellValue(investment.getExitAmount() != null ? investment.getExitAmount() : 0.0); // Default to 0.0 if null
            row.createCell(14).setCellValue(investment.getStatus() != null ? investment.getStatus().toString() : "N/A");
            row.createCell(15).setCellValue(investment.getCreatedAt() != null ? investment.getCreatedAt().toString() : "N/A");
            row.createCell(16).setCellValue(investment.getUpdatedAt() != null ? investment.getUpdatedAt().toString() : "N/A");
            row.createCell(17).setCellValue(investment.getDividendPaymentFrequency() != null ? investment.getDividendPaymentFrequency().toString() : "N/A");
            row.createCell(18).setCellValue(investment.getNetProfit() != null ? investment.getNetProfit() : 0.0); // Default to 0.0 if null
            row.createCell(19).setCellValue(investment.getCurrentValue() != null ? investment.getCurrentValue() : 0.0); // Default to 0.0 if null
            row.createCell(20).setCellValue(investment.getInvestorNotes() != null ? investment.getInvestorNotes() : "N/A");
            row.createCell(21).setCellValue(investment.getCurrency() != null ? investment.getCurrency() : "N/A");
            row.createCell(22).setCellValue(investment.getInvestorSatisfaction() != null ? investment.getInvestorSatisfaction() : 0); // Default to 0 if null
        }

        // Auto-size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
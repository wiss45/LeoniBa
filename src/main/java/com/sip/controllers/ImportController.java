package com.sip.controllers;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sip.entities.Equipement;
import com.sip.services.ExcelImportService;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/api/import")
public class ImportController {

  @Autowired
  private ExcelImportService excelImportService;
  private static final Pattern UNIT_PRICE_PATTERN = Pattern.compile("(\\d{4})\\s*Unit Price", Pattern.CASE_INSENSITIVE);

  /**
   * Endpoint to import equipment data from Excel file
   *
   * @param file Excel file containing equipment data
   * @param replaceExisting Flag to indicate if existing data should be replaced
   * @return ResponseEntity with import results
   */
  @PostMapping("/equipment")
  public ResponseEntity<?> importEquipmentData(
    @RequestParam("file") MultipartFile file,
    @RequestParam(value = "replaceExisting", defaultValue = "true") boolean replaceExisting) {
    try {
      // Validate file
      if (file.isEmpty()) {
        return ResponseEntity.badRequest().body("Please upload a file");
      }

      // Check file extension
      String fileName = file.getOriginalFilename();
      if (fileName == null || !(fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
        return ResponseEntity.badRequest().body("Please upload an Excel file (xlsx or xls)");
      }

      // Process the file using the service
      List<Equipement> importedEquipment = excelImportService.importEquipmentFromExcel(file, replaceExisting);

      // Return success response
      Map<String, Object> response = new HashMap<>();
      response.put("message", "Equipment data imported successfully");
      response.put("count", importedEquipment.size());
      response.put("replacedExisting", replaceExisting);
      return ResponseEntity.ok(response);

    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Failed to process file: " + e.getMessage());
    } catch (RuntimeException e) {
      // Handle specific exceptions thrown by the service
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(e.getMessage());
    }
  }

  /**
   * Endpoint to check if a file is valid before uploading
   *
   * @param file Excel file to validate
   * @return ResponseEntity with validation result
   */
  /**
   * Endpoint to validate Excel file
   * @param file Excel file to validate
   * @return ResponseEntity with validation result
   */
  @PostMapping("/validate")
  public ResponseEntity<?> validateExcelFile(@RequestParam("file") MultipartFile file) {
    String fileName = file.getOriginalFilename();
    if (fileName == null || !(fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
      return ResponseEntity.badRequest().body("Invalid file format. Please upload an Excel file.");
    }

    try {
      try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);

        if (headerRow == null) {
          return ResponseEntity.badRequest().body("Invalid file: Missing header row");
        }

        boolean hasDescriptionColumn = false;
        boolean hasUnitPriceColumn = false;
        List<String> detectedUnitPriceColumns = new ArrayList<>();

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
          Cell cell = headerRow.getCell(i);
          if (cell != null) {
            String headerValue = cell.getStringCellValue().trim();

            // Check for Equipment Description
            if (headerValue.contains("Equipment Description")) {
              hasDescriptionColumn = true;
            }

            // Check for Unit Price columns using flexible pattern
            Matcher matcher = UNIT_PRICE_PATTERN.matcher(headerValue);
            if (matcher.find()) {
              hasUnitPriceColumn = true;
              detectedUnitPriceColumns.add(headerValue);
            }
          }
        }

        // Validate columns
        if (!hasDescriptionColumn) {
          return ResponseEntity.badRequest().body("Invalid file: Missing Equipment Description column");
        }

        if (!hasUnitPriceColumn) {
          return ResponseEntity.badRequest().body("Invalid file: Missing Unit Price columns");
        }

        // Provide more detailed feedback about detected Unit Price columns
        Map<String, Object> response = new HashMap<>();
        response.put("message", "File is valid");
        response.put("detectedUnitPriceColumns", detectedUnitPriceColumns);

        return ResponseEntity.ok(response);
      }
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Invalid Excel file: " + e.getMessage());
    }
  }

  /**
   * Endpoint to analyze Excel structure and detect column mapping
   *
   * @param file Excel file to analyze
   * @return ResponseEntity with analysis results
   */
  @PostMapping("/analyze")
  public ResponseEntity<?> analyzeExcelStructure(@RequestParam("file") MultipartFile file) {
    try {
      String fileName = file.getOriginalFilename();
      if (fileName == null || !(fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
        return ResponseEntity.badRequest().body("Invalid file format. Please upload an Excel file.");
      }

      Map<String, Object> analysis = new HashMap<>();

      try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);

        if (headerRow == null) {
          return ResponseEntity.badRequest().body("Invalid file: Missing header row");
        }

        Map<String, String> detectedColumns = new HashMap<>();
        List<Map<String, Object>> yearColumns = new ArrayList<>();

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
          Cell cell = headerRow.getCell(i);
          if (cell != null) {
            String headerValue = cell.getStringCellValue().trim();

            // Detect Unit Price columns
            Matcher matcher = UNIT_PRICE_PATTERN.matcher(headerValue);
            if (matcher.find()) {
              int year = Integer.parseInt(matcher.group(1));
              Map<String, Object> yearInfo = new HashMap<>();
              yearInfo.put("columnIndex", i);
              yearInfo.put("year", year);
              yearInfo.put("headerName", headerValue);
              yearColumns.add(yearInfo);
            }
            // Detect other columns
            else if (headerValue.contains("Equipment Description")) {
              detectedColumns.put("description", headerValue);
            } else if (headerValue.contains("Lead time")) {
              detectedColumns.put("leadTime", headerValue);
            } else if (headerValue.contains("Transportation time")) {
              detectedColumns.put("transportationTime", headerValue);
            } else if (headerValue.contains("Installation time")) {
              detectedColumns.put("installationTime", headerValue);
            } else if (headerValue.contains("Supplier")) {
              detectedColumns.put("supplier", headerValue);
            } else if (headerValue.contains("price increase")) {
              detectedColumns.put("priceIncrease", headerValue);
            } else if (headerValue.contains("CapEx Type")) {
              detectedColumns.put("capexType", headerValue);
            }
          }
        }

        // Sort year columns
        yearColumns.sort(Comparator.comparingInt(col -> (Integer)col.get("year")));

        analysis.put("detectedColumns", detectedColumns);
        analysis.put("yearColumns", yearColumns);
        analysis.put("totalColumns", headerRow.getLastCellNum());

        // Create mapping info for Unit Price columns
        Map<String, String> mappingInfo = new HashMap<>();
        if (!yearColumns.isEmpty()) {
          mappingInfo.put("firstUnitPrice", "Will map to " +
            ((Map<String, Object>)yearColumns.get(0)).get("headerName"));
        }
        if (yearColumns.size() > 1) {
          mappingInfo.put("secondUnitPrice", "Will map to " +
            ((Map<String, Object>)yearColumns.get(1)).get("headerName"));
        }
        if (yearColumns.size() > 2) {
          mappingInfo.put("thirdUnitPrice", "Will map to " +
            ((Map<String, Object>)yearColumns.get(2)).get("headerName"));
        }

        analysis.put("mappingInfo", mappingInfo);
      }

      return ResponseEntity.ok(analysis);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Failed to analyze file: " + e.getMessage());
    }
  }
}

package com.sip.services;



import com.sip.entities.Equipement;
import com.sip.entities.Role;
import com.sip.repositories.EquipementRepository;
import com.sip.repositories.PlanRepository;
import com.sip.repositories.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExcelImportService {

  
  private final EquipementRepository equipmentRepository;


  private final UserRepository userRepository;

  private final PlanRepository planRepository;

  
  public ExcelImportService(EquipementRepository equipmentRepository,
		UserRepository userRepository, PlanRepository planRepository) {
	super();
	this.equipmentRepository = equipmentRepository;
	this.userRepository = userRepository;
	this.planRepository = planRepository;
}

// Pattern to identify unit price columns (any year followed by "Unit Price")
  private static final Pattern UNIT_PRICE_PATTERN = Pattern.compile("(\\d{4})\\s*Unit Price", Pattern.CASE_INSENSITIVE);

  /**
   * Import equipment data from Excel file with flexibility for column changes
   * Only admin users can import data
   *
   * @param file Excel file to import
   * @param replaceExisting Whether to replace existing equipment data
   * @return List of imported equipment
   * @throws IOException if file cannot be read
   * @throws RuntimeException if user is not authorized or file structure is invalid
   */
  @Transactional
  public List<Equipment> importEquipmentFromExcel(MultipartFile file, boolean replaceExisting) throws IOException {
    // Check if user is admin
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User currentUser = userRepository.findUserByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

    // Add permission check
    if (currentUser.getRole() != Role.ADMIN) {
      throw new RuntimeException("User has no permission to import Excel. Only Admin can do it.");
    }

    List<Equipement> importedEquipment = new ArrayList<>();

    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = workbook.getSheetAt(0);

      // Parse header row to determine column mapping
      Map<Integer, String> columnMap = new HashMap<>();
      List<Map<Integer, Integer>> yearPriceColumns = new ArrayList<>();

      Row headerRow = sheet.getRow(0);
      if (headerRow == null) {
        throw new RuntimeException("Excel file is missing header row");
      }

      for (int i = 0; i < headerRow.getLastCellNum(); i++) {
        Cell cell = headerRow.getCell(i);
        if (cell != null) {
          String headerValue = cell.getStringCellValue().trim();
          Matcher matcher = UNIT_PRICE_PATTERN.matcher(headerValue);

          // Check for Unit Price columns
          if (matcher.find()) {
            int year = Integer.parseInt(matcher.group(1));
            Map<Integer, Integer> yearColumn = new HashMap<>();
            yearColumn.put(i, year);
            yearPriceColumns.add(yearColumn);
          }
          // Other column mappings remain the same
          else if (headerValue.contains("Equipment Description")) {
            columnMap.put(i, "description");
          }else if (headerValue.contains("First Unit Price")) {
              columnMap.put(i, "FirstUnitPrice");
            }else if (headerValue.contains("Second Unit Price")) {
                columnMap.put(i, "SecondUnitPrice");
            }else if (headerValue.contains("Third Unit Price")) {
                columnMap.put(i, "ThirdUnitPrice");
            } else if (headerValue.contains("Lead time")) {
            columnMap.put(i, "leadTime");
          } else if (headerValue.contains("Transportation time")) {
            columnMap.put(i, "transportationTime");
          } else if (headerValue.contains("Installation time")) {
            columnMap.put(i, "installationTime");
          } else if (headerValue.contains("Supplier")) {
            columnMap.put(i, "supplier");
          } else if (headerValue.contains("price increase")) {
            columnMap.put(i, "priceIncrease");
          } else if (headerValue.contains("CapEx Type")) {
            columnMap.put(i, "capexType");
          }
        }
      }

      // Validate required columns
      if (!columnMap.containsValue("description")) {
        throw new RuntimeException("Excel is missing Equipment Description column");
      }

      // Sort price columns by year
      yearPriceColumns.sort(Comparator.comparingInt(map -> map.values().iterator().next()));

      // Process replacing existing data
      if (replaceExisting) {
        // Instead of deleting, mark existing equipment as inactive
        List<Equipment> existingEquipment = equipmentRepository.findAll();
        existingEquipment.forEach(eq -> {
          // Check if equipment is used in any active plans
          long activePlanCount = planRepository.countByEquipment(eq);
          if (activePlanCount > 0) {
            // If used in plans, mark as inactive instead of deleting
            eq.setActive(false);
            equipmentRepository.save(eq);
          } else {
            // If not used in plans, safe to delete
            equipmentRepository.delete(eq);
          }
        });
      }

      // Process rows
      Iterator<Row> rows = sheet.iterator();
      rows.next(); // Skip header row
      while (rows.hasNext()) {
        Row currentRow = rows.next();
        if (isEmptyRow(currentRow)) {
          continue;
        }

        Equipement equipment = new Equipement();
        equipment.setUser(currentUser);
        equipment.setActive(true); // New equipment is active by default

        // Process non-price columns
        for (Map.Entry<Integer, String> entry : columnMap.entrySet()) {
          int columnIndex = entry.getKey();
          String columnType = entry.getValue();
          Cell cell = currentRow.getCell(columnIndex);

          switch (columnType) {
            case "description":
              equipment.setEquipmentDescription(getStringCellValue(cell));
              break;
            case "FirstUnitPrice":
              equipment.setFirstUnitPrice(getBigDecimalCellValue(cell));
              break;
            case "SecondUnitPrice":
              equipment.setSecondUnitPrice(getBigDecimalCellValue(cell));
              break;
            case "ThirdUnitPrice":
              equipment.setThirdUnitPrice(getBigDecimalCellValue(cell));
              break;
            case "leadTime":
              equipment.setLeadTime(getIntCellValue(cell));
              break;
            case "transportationTime":
              equipment.setTransportationTime(getIntCellValue(cell));
              break;
            case "installationTime":
              equipment.setInstallationTime(getIntCellValue(cell));
              break;
            case "supplier":
              equipment.setSupplier(getStringCellValue(cell));
              break;
            case "priceIncrease":
              equipment.setPercentPriceIncreasePerYear(getDoubleCellValue(cell));
              break;
            case "capexType":
              equipment.setCapexType(getStringCellValue(cell));
              break;
          }
        }

        // Process price columns
        if (!yearPriceColumns.isEmpty()) {
          // First Unit Price (first column)
          if (yearPriceColumns.size() >= 1) {
            int firstPriceColumnIndex = yearPriceColumns.get(0).keySet().iterator().next();
            equipment.setFirstUnitPrice(getBigDecimalCellValue(currentRow.getCell(firstPriceColumnIndex)));
          }

          // Second Unit Price (second column if exists)
          if (yearPriceColumns.size() >= 2) {
            int secondPriceColumnIndex = yearPriceColumns.get(1).keySet().iterator().next();
            equipment.setSecondUnitPrice(getBigDecimalCellValue(currentRow.getCell(secondPriceColumnIndex)));
          }

          // Third Unit Price (third column if exists)
          if (yearPriceColumns.size() >= 3) {
            int thirdPriceColumnIndex = yearPriceColumns.get(2).keySet().iterator().next();
            equipment.setThirdUnitPrice(getBigDecimalCellValue(currentRow.getCell(thirdPriceColumnIndex)));
          }
        }

        // Save only if description is not empty
        if (equipment.getEquipmentDescription() != null && !equipment.getEquipmentDescription().trim().isEmpty()) {
          importedEquipment.add(equipmentRepository.save(equipment));
        }
      }
    }

    return importedEquipment;
  }

  /**
   * Check if a row is empty (all cells are blank)
   */
  private boolean isEmptyRow(Row row) {
    if (row == null) {
      return true;
    }

    boolean isEmpty = true;
    for (int i = 0; i < row.getLastCellNum(); i++) {
      Cell cell = row.getCell(i);
      if (cell != null && cell.getCellType() != CellType.BLANK) {
        if (cell.getCellType() == CellType.STRING) {
          if (!cell.getStringCellValue().trim().isEmpty()) {
            isEmpty = false;
            break;
          }
        } else {
          isEmpty = false;
          break;
        }
      }
    }
    return isEmpty;
  }

  // Helper methods for cell value extraction with proper type handling
  private String getStringCellValue(Cell cell) {
    if (cell == null) {
      return "";
    }
    if (cell.getCellType() == CellType.STRING) {
      return cell.getStringCellValue();
    } else if (cell.getCellType() == CellType.NUMERIC) {
      return String.valueOf(cell.getNumericCellValue());
    }
    return "";
  }

  private BigDecimal getBigDecimalCellValue(Cell cell) {
    if (cell == null) {
      return BigDecimal.ZERO;
    }
    if (cell.getCellType() == CellType.NUMERIC) {
      return BigDecimal.valueOf(cell.getNumericCellValue());
    } else if (cell.getCellType() == CellType.STRING) {
      try {
        return new BigDecimal(cell.getStringCellValue());
      } catch (NumberFormatException e) {
        return BigDecimal.ZERO;
      }
    }
    return BigDecimal.ZERO;
  }

  private int getIntCellValue(Cell cell) {
    if (cell == null) {
      return 0;
    }
    if (cell.getCellType() == CellType.NUMERIC) {
      return (int) cell.getNumericCellValue();
    } else if (cell.getCellType() == CellType.STRING) {
      try {
        return Integer.parseInt(cell.getStringCellValue());
      } catch (NumberFormatException e) {
        return 0;
      }
    }
    return 0;
  }

  private double getDoubleCellValue(Cell cell) {
    if (cell == null) {
      return 0.0;
    }
    if (cell.getCellType() == CellType.NUMERIC) {
      return cell.getNumericCellValue();
    } else if (cell.getCellType() == CellType.STRING) {
      try {
        return Double.parseDouble(cell.getStringCellValue());
      } catch (NumberFormatException e) {
        return 0.0;
      }
    }
    return 0.0;
  }
}

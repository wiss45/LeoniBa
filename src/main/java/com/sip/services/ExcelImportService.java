package com.sip.services;



import com.sip.entities.Equipement;
import com.sip.entities.Role;
import com.sip.entities.User;
import com.sip.enums.ERole;
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
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
    }

    private static final Pattern UNIT_PRICE_PATTERN = Pattern.compile("(\\d{4})\\s*Unit Price", Pattern.CASE_INSENSITIVE);

    
    private boolean isUserAdmin(User user) {
        return user.getRoles().stream()
            .anyMatch(role -> role.getName() == ERole.ADMIN);
    }
    
    @Transactional
    public List<Equipement> importEquipmentFromExcel(MultipartFile file, boolean replaceExisting) throws IOException {
        // Check if user is admin
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!isUserAdmin(currentUser)) {
            throw new RuntimeException("User has no permission to import Excel. Only Admin can do it.");
        }


        List<Equipement> importedEquipment = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

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

                    if (matcher.find()) {
                        int year = Integer.parseInt(matcher.group(1));
                        Map<Integer, Integer> yearColumn = new HashMap<>();
                        yearColumn.put(i, year);
                        yearPriceColumns.add(yearColumn);
                    } else if (headerValue.contains("Equipment Name")) {
                        columnMap.put(i, "name");
                    } else if (headerValue.contains("First Unit Price")) {
                        columnMap.put(i, "firstUnitPrice");
                    } else if (headerValue.contains("Second Unit Price")) {
                        columnMap.put(i, "secondUnitPrice");
                    } else if (headerValue.contains("Third Unit Price")) {
                        columnMap.put(i, "thirdUnitPrice");
                    } else if (headerValue.contains("Lead time")) {
                        columnMap.put(i, "leadTime");
                    } else if (headerValue.contains("Transportation time")) {
                        columnMap.put(i, "transportationTime");
                    } else if (headerValue.contains("Installation time")) {
                        columnMap.put(i, "installationTime");
                    } else if (headerValue.contains("Supplier")) {
                        columnMap.put(i, "supplier");
                    } else if (headerValue.contains("Price")) {
                        columnMap.put(i, "price");
                    } else if (headerValue.contains("CapEx Type")) {
                        columnMap.put(i, "capexType");
                    }
                }
            }

            if (!columnMap.containsValue("name")) {
                throw new RuntimeException("Excel is missing Equipment Name column");
            }

            yearPriceColumns.sort(Comparator.comparingInt(map -> map.values().iterator().next()));

            if (replaceExisting) {
                List<Equipement> existingEquipment = equipmentRepository.findAll();
                existingEquipment.forEach(eq -> {
                    long activePlanCount = planRepository.countByEquipement(eq);
                    if (activePlanCount > 0) {
                        eq.setActive(false);
                        equipmentRepository.save(eq);
                    } else {
                        equipmentRepository.delete(eq);
                    }
                });
            }

            Iterator<Row> rows = sheet.iterator();
            rows.next(); // Skip header row
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                if (isEmptyRow(currentRow)) {
                    continue;
                }

                Equipement equipment = new Equipement();
                equipment.setUser(currentUser);
                equipment.setActive(true);

                for (Map.Entry<Integer, String> entry : columnMap.entrySet()) {
                    int columnIndex = entry.getKey();
                    String columnType = entry.getValue();
                    Cell cell = currentRow.getCell(columnIndex);

                    switch (columnType) {
                        case "name":
                            equipment.setName(getStringCellValue(cell));
                            break;
                        case "firstUnitPrice":
                            equipment.setFirstUnitPrice(getDoubleCellValue(cell));
                            break;
                        case "secondUnitPrice":
                            equipment.setSecondUnitPrice(getDoubleCellValue(cell));
                            break;
                        case "thirdUnitPrice":
                            equipment.setThirdUnitPrice(getDoubleCellValue(cell));
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
                        case "price":
                            equipment.setPrice(getDoubleCellValue(cell));
                            break;
                        case "capexType":
                            equipment.setCapexType(getStringCellValue(cell));
                            break;
                    }
                }

                if (!yearPriceColumns.isEmpty()) {
                    if (yearPriceColumns.size() >= 1) {
                        int firstPriceColumnIndex = yearPriceColumns.get(0).keySet().iterator().next();
                        equipment.setFirstUnitPrice(getDoubleCellValue(currentRow.getCell(firstPriceColumnIndex)));
                    }

                    if (yearPriceColumns.size() >= 2) {
                        int secondPriceColumnIndex = yearPriceColumns.get(1).keySet().iterator().next();
                        equipment.setSecondUnitPrice(getDoubleCellValue(currentRow.getCell(secondPriceColumnIndex)));
                    }

                    if (yearPriceColumns.size() >= 3) {
                        int thirdPriceColumnIndex = yearPriceColumns.get(2).keySet().iterator().next();
                        equipment.setThirdUnitPrice(getDoubleCellValue(currentRow.getCell(thirdPriceColumnIndex)));
                    }
                }

                if (equipment.getName() != null && !equipment.getName().trim().isEmpty()) {
                    importedEquipment.add(equipmentRepository.save(equipment));
                }
            }
        }

        return importedEquipment;
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) return true;

        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : String.valueOf(cell.getNumericCellValue());
    }

    private double getDoubleCellValue(Cell cell) {
        if (cell == null) return 0.0;
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : 0.0;
    }

    private int getIntCellValue(Cell cell) {
        if (cell == null) return 0;
        return cell.getCellType() == CellType.NUMERIC ? (int) cell.getNumericCellValue() : 0;
    }
}

package com.sip.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sip.entities.Equipement;
import com.sip.interfaces.EquipementService;
import com.sip.repositories.EquipementRepository;
import com.sip.requests.EquipementRequest;
import com.sip.responses.EquipementResponse;

@Service
public class EquipementServiceImp implements EquipementService {
	
	private final EquipementRepository equipementRepository ;
	
	
	public EquipementServiceImp(EquipementRepository equipementRepository) {
		
		this.equipementRepository = equipementRepository;
	}

	@Override
	public List<Equipement> getEquipements() {
        return equipementRepository.findAll();
    }

	@Override
	public Map<String, Object> getAllEquipements(Pageable pageable) {
	    Page<Equipement> pageEquipements = equipementRepository.findAll(pageable);

	    List<EquipementResponse> content = pageEquipements.getContent().stream()
	            .map(equipement -> new EquipementResponse(
	                    equipement.getId(),
	                    equipement.getName(),
	                    equipement.getFirstUnitPrice(),
	                    equipement.getSecondUnitPrice(),
	                    equipement.getThirdUnitPrice(),
	                    equipement.getLeadTime(),
	                    equipement.getTransportationTime(),
	                    equipement.getInstallationTime(),
	                    equipement.getSupplier(),
	                    equipement.getPrice(),
	                    equipement.getCapexType()
	            ))
	            .collect(Collectors.toList());

	    Map<String, Object> response = new HashMap<>();
	    response.put("content", content);
	    response.put("currentPage", pageEquipements.getNumber());
	    response.put("totalElements", pageEquipements.getTotalElements());
	    response.put("totalPages", pageEquipements.getTotalPages());

	    return response;
	}
	
	@Override
	public EquipementResponse addEquipement(EquipementRequest equipementRequestDTO) {
	    Equipement equipement = new Equipement();

	    equipement.setName(equipementRequestDTO.getName());
	    equipement.setFirstUnitPrice(equipementRequestDTO.getFirstUnitPrice());
	    equipement.setSecondUnitPrice(equipementRequestDTO.getSecondUnitPrice());
	    equipement.setThirdUnitPrice(equipementRequestDTO.getThirdUnitPrice());
	    equipement.setLeadTime(equipementRequestDTO.getLeadTime());
	    equipement.setTransportationTime(equipementRequestDTO.getTransportationTime());
	    equipement.setInstallationTime(equipementRequestDTO.getInstallationTime());
	    equipement.setSupplier(equipementRequestDTO.getSupplier());
	    equipement.setPrice(equipementRequestDTO.getPrice());
	    equipement.setCapexType(equipementRequestDTO.getCapexType());

	    Equipement savedEquipement = equipementRepository.save(equipement);

	    return new EquipementResponse(
	            savedEquipement.getId(),
	            savedEquipement.getName(),
	            savedEquipement.getFirstUnitPrice(),
	            savedEquipement.getSecondUnitPrice(),
	            savedEquipement.getThirdUnitPrice(),
	            savedEquipement.getLeadTime(),
	            savedEquipement.getTransportationTime(),
	            savedEquipement.getInstallationTime(),
	            savedEquipement.getSupplier(),
	            savedEquipement.getPrice(),
	            savedEquipement.getCapexType()
	    );
	}
	
	
	@Override
	public EquipementResponse updateEquipement(Long id, EquipementRequest equipementRequestDTO) {
	    Optional<Equipement> optionalEquipement = equipementRepository.findById(id);
	    if (optionalEquipement.isPresent()) {
	        Equipement equipement = optionalEquipement.get();

	        equipement.setName(equipementRequestDTO.getName());
	        equipement.setFirstUnitPrice(equipementRequestDTO.getFirstUnitPrice());
	        equipement.setSecondUnitPrice(equipementRequestDTO.getSecondUnitPrice());
	        equipement.setThirdUnitPrice(equipementRequestDTO.getThirdUnitPrice());
	        equipement.setLeadTime(equipementRequestDTO.getLeadTime());
	        equipement.setTransportationTime(equipementRequestDTO.getTransportationTime());
	        equipement.setInstallationTime(equipementRequestDTO.getInstallationTime());
	        equipement.setSupplier(equipementRequestDTO.getSupplier());
	        equipement.setPrice(equipementRequestDTO.getPrice());
	        equipement.setCapexType(equipementRequestDTO.getCapexType());

	        Equipement updatedEquipement = equipementRepository.save(equipement);

	        return new EquipementResponse(
	                updatedEquipement.getId(),
	                updatedEquipement.getName(),
	                updatedEquipement.getFirstUnitPrice(),
	                updatedEquipement.getSecondUnitPrice(),
	                updatedEquipement.getThirdUnitPrice(),
	                updatedEquipement.getLeadTime(),
	                updatedEquipement.getTransportationTime(),
	                updatedEquipement.getInstallationTime(),
	                updatedEquipement.getSupplier(),
	                updatedEquipement.getPrice(),
	                updatedEquipement.getCapexType()
	                );
	        
	    } else {
	        throw new RuntimeException("Equipement not found with id: " + id);
	    }
	}

	
	@Override
	public void deleteEquipement(long id) {
		this.equipementRepository.deleteById(id);
	}

	
	@Override
	public int NombreEquipements() {
		return this.equipementRepository.nombreEquipements();
	}
	
	@Override
    public Map<String, Object> importFromExcel(MultipartFile file) throws Exception {
	        List<Equipement> equipements = new ArrayList<>();

	        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
	            Sheet sheet = workbook.getSheetAt(0);

	            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
	                Row row = sheet.getRow(i);
	                if (row == null) continue;

	                Equipement eq = new Equipement();
	                eq.setName(row.getCell(0).getStringCellValue());
	                eq.setFirstUnitPrice(row.getCell(1).getNumericCellValue());
	                eq.setSecondUnitPrice(row.getCell(2).getNumericCellValue());
	                eq.setThirdUnitPrice(row.getCell(3).getNumericCellValue());
	                eq.setLeadTime((int) row.getCell(4).getNumericCellValue());
	                eq.setTransportationTime((int) row.getCell(5).getNumericCellValue());
	                eq.setInstallationTime((int) row.getCell(6).getNumericCellValue());
	                eq.setSupplier(row.getCell(7).getStringCellValue());
	                eq.setPrice(row.getCell(8).getNumericCellValue());
	                eq.setCapexType(row.getCell(9).getStringCellValue());

	                equipements.add(eq);
	            }
	        }

	        equipementRepository.saveAll(equipements);
	        return Map.of(
	            "message", "Importation réussie",
	            "count", equipements.size(),
	            "replacedExisting", false
	        );
	    }
	
	
	}





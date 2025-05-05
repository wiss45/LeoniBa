package com.sip.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
	


}

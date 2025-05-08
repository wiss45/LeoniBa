package com.sip.interfaces;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.sip.entities.Equipement;
import com.sip.requests.EquipementRequest;
import com.sip.responses.EquipementResponse;

public interface EquipementService {

	void deleteEquipement(long id);

	Map<String, Object> getAllEquipements(Pageable pageable);

	EquipementResponse addEquipement(EquipementRequest equipementRequestDTO);

	EquipementResponse updateEquipement(Long id, EquipementRequest equipementRequestDTO);

	List<Equipement> getEquipements();

	int NombreEquipements();

	Map<String, Object> importFromExcel(MultipartFile file) throws Exception;

}

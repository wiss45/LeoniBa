package com.sip.interfaces;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.sip.entities.Equipement;
import com.sip.requests.EquipementRequest;
import com.sip.responses.EquipementResponse;

public interface EquipementService {

	void deleteEquipement(long id);

	Map<String, Object> getAllEquipements(Pageable pageable);

	EquipementResponse addEquipement(EquipementRequest equipementRequestDTO);

	EquipementResponse updateEquipement(Long id, EquipementRequest equipementRequestDTO);

	List<Equipement> getEquipements();

}

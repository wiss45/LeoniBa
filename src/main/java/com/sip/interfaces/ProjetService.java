package com.sip.interfaces;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.sip.entities.Projet;
import com.sip.requests.ProjetRequest;
import com.sip.responses.ProjetResponse;

public interface ProjetService {

	void deleteProjet(Long id);

	

	ProjetResponse updateProjet(Long id, ProjetRequest request);

	ProjetResponse createProjet(ProjetRequest request);

	Map<String, Object> getAllProjets(Pageable pageable);



	List<Projet> getProjets();

}

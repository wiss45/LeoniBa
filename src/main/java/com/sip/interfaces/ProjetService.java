package com.sip.interfaces;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sip.entities.Projet;
import com.sip.requests.ProjetRequest;
import com.sip.responses.ProjetResponse;

public interface ProjetService {

	ProjetResponse createProjet(ProjetRequest request);

	ProjetResponse updateProjet(Long id, ProjetRequest request);

	List<Projet> getProjets();

	Map<String, Object> getAllProjets(Pageable pageable);

	void deleteProjet(Long id);

	int NombreProjets();

	List<Projet> projetDRAFT();

	Page<ProjetResponse> findByResponsable(String responsable, Pageable pageable);

	

	

}

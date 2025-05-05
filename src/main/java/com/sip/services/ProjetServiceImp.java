package com.sip.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sip.entities.Equipement;
import com.sip.entities.Projet;
import com.sip.interfaces.ProjetService;
import com.sip.repositories.EquipementRepository;
import com.sip.repositories.ProjetRepository;
import com.sip.requests.ProjetRequest;
import com.sip.responses.ProjetResponse;

@Service
public class ProjetServiceImp implements ProjetService  {
	  
	
	    
	    private final ProjetRepository projetRepository;

	    
	    private final EquipementRepository equipementRepository;

	    
	    
	    public ProjetServiceImp(ProjetRepository projetRepository, EquipementRepository equipementRepository) {
			
			this.projetRepository = projetRepository;
			this.equipementRepository = equipementRepository;
		}

	    @Override
	    public ProjetResponse createProjet(ProjetRequest request) {
	        Projet projet = mapToEntity(request);

	        
	        if (request.getEquipements() != null) {
	            List<Equipement> equipements = new ArrayList<>();
	            for (Equipement equipement : request.getEquipements()) {
	                if (equipement.getId() != null) {
	                    
	                    Equipement existingEquipement = equipementRepository.findById(equipement.getId())
	                            .orElseThrow(() -> new IllegalArgumentException("Équipement non trouvé"));
	                    existingEquipement.setProjet(projet); 
	                    equipements.add(existingEquipement);
	                } else {
	                    
	                    equipement.setProjet(projet); 
	                    equipements.add(equipement);
	                }
	            }
	            projet.setEquipements(equipements);
	        }

	        
	        Projet saved = projetRepository.save(projet);
	        return mapToResponse(saved);
	    }


	    @Override
	    public ProjetResponse updateProjet(Long id, ProjetRequest request) {
	        Optional<Projet> optional = projetRepository.findById(id);
	        if (optional.isEmpty()) {
	            throw new RuntimeException("Projet not found with id " + id);
	        }

	        Projet projet = optional.get();

	        projet.setName(request.getName());
	        projet.setCustomer(request.getCustomer());
	        projet.setDeravative(request.getDeravative());
	        projet.setMaxQuantite(request.getMaxQuantite());
	        projet.setA_samples(request.getA_samples());
	        projet.setB_samples(request.getB_samples());
	        projet.setC_samples(request.getC_samples());
	        projet.setD_samples(request.getD_samples());
	        projet.setSop(request.getSop());
	        projet.setSop_1(request.getSop_1());
	        projet.setResponsable(request.getResponsable());
	        projet.setStatus(request.getStatus());
	        projet.setSommePrevisionnel(request.getSommePrevisionnel());
	        projet.setSommeReel(request.getSommeReel());
	        projet.setEquipements(request.getEquipements());

	        

	        Projet updated = projetRepository.save(projet);
	        return mapToResponse(updated);
	    }

	    
	    @Override
	    public List<Projet> getProjets() {
	        return projetRepository.findAll();
	    }

	    @Override
	    public Map<String, Object> getAllProjets(Pageable pageable) {
	        Page<Projet> pageProjets = projetRepository.findAll(pageable);

	        List<ProjetResponse> content = pageProjets.getContent().stream()
	                .map(this::mapToResponse)
	                .collect(Collectors.toList());

	        Map<String, Object> response = new HashMap<>();
	        response.put("content", content);
	        response.put("currentPage", pageProjets.getNumber());
	        response.put("totalElements", pageProjets.getTotalElements());
	        response.put("totalPages", pageProjets.getTotalPages());

	        return response;
	    }


	    @Override
	    public void deleteProjet(Long id) {
	        projetRepository.deleteById(id);
	    }

	    private ProjetResponse mapToResponse(Projet projet) {
	        List<Equipement> allEquipements = new ArrayList<>();

	        // Équipements liés directement au projet
	        if (projet.getEquipements() != null) {
	            allEquipements.addAll(projet.getEquipements());
	        }

	        // Équipements liés via le plan
	        if (projet.getPlan() != null && projet.getPlan().getEquipements() != null) {
	            for (Equipement eq : projet.getPlan().getEquipements()) {
	                if (!allEquipements.contains(eq)) {
	                    allEquipements.add(eq);
	                }
	            }
	        }

	        return new ProjetResponse(
	            projet.getId(),
	            projet.getName(),
	            projet.getCustomer(),
	            projet.getDeravative(),
	            projet.getMaxQuantite(),
	            projet.getA_samples(),
	            projet.getB_samples(),
	            projet.getC_samples(),
	            projet.getD_samples(),
	            projet.getSop(),
	            projet.getSop_1(),
	            projet.getResponsable(),
	            projet.getStatus(),
	            projet.getSommePrevisionnel(),
	            projet.getSommeReel(),
	            allEquipements,
	            projet.getPlan()
	        );
	    }


	    
	    private Projet mapToEntity(ProjetRequest request) {
	        return new Projet(
	            request.getName(),
	            request.getCustomer(),
	            request.getDeravative(),
	            request.getMaxQuantite(),
	            request.getA_samples(),
	            request.getB_samples(),
	            request.getC_samples(),
	            request.getD_samples(),
	            request.getSop(),
	            request.getSop_1(),
	            request.getResponsable(),
	            request.getStatus(),
	            request.getSommePrevisionnel(),
	            request.getSommeReel(),
	            request.getEquipements(),
	            request.getPlan()
	        );
	    }

		@Override
		public int NombreProjets() {
			return this.projetRepository.nombreProjetsActifs();
		}
	  
}
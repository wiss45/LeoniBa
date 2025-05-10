package com.sip.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sip.entities.Equipement;
import com.sip.entities.Plan;
import com.sip.entities.Projet;
import com.sip.interfaces.ProjetService;
import com.sip.repositories.EquipementRepository;
import com.sip.repositories.PlanRepository;
import com.sip.repositories.ProjetRepository;
import com.sip.requests.ProjetRequest;
import com.sip.responses.ProjetResponse;

@Service
public class ProjetServiceImp implements ProjetService {

    private final ProjetRepository projetRepository;
    private final EquipementRepository equipementRepository;
    private final PlanRepository planRepository; 

    public ProjetServiceImp(ProjetRepository projetRepository, EquipementRepository equipementRepository, PlanRepository planRepository) {
        this.projetRepository = projetRepository;
        this.equipementRepository = equipementRepository;
        this.planRepository = planRepository;
    }

    @Override
    public ProjetResponse createProjet(ProjetRequest request) {
        Projet projet = new Projet();
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
        projet.setSommeReel(0.0);
        
        Projet saved = projetRepository.save(projet);
        return mapToResponse(saved);
    }

    @Override
    public ProjetResponse updateProjet(Long id, ProjetRequest request) {
        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé avec l'id " + id));

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

       

        Projet updated = projetRepository.save(projet);
        return mapToResponse(updated);
    }

    @Override
    public List<Projet> getProjets() {
        return projetRepository.findAll();
    }

    @Override
    public Map<String, Object> getAllProjets(Pageable pageable) {
    	
    	// Récupère l'objet d'authentification de l'utilisateur actuellement connecté
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    	// Récupère le nom d'utilisateur (username) de l'utilisateur connecté
    	String currentUsername = authentication.getName();

    	// Vérifie si l'utilisateur a le rôle "ADMIN"
    	boolean isAdmin = authentication.getAuthorities().stream()
    	    .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));

    	// Déclare une variable pour stocker la page de projets à retourner
    	Page<Projet> pageProjets;

    	// Si l'utilisateur est un administrateur, on récupère tous les projets avec pagination
    	if (isAdmin) {
    	    pageProjets = projetRepository.findAll(pageable);
    	} else {
    	    // Sinon, on récupère uniquement les projets dont il est le responsable
    	    pageProjets = projetRepository.findByResponsable(currentUsername, pageable);
    	}

    	// Transforme la liste d'entités Projet en liste de DTO ProjetResponse
    	// La méthode mapToResponse convertit un Projet vers un ProjetResponse (format allégé pour l'API)
    	List<ProjetResponse> content = pageProjets.getContent().stream()
    	    .map(this::mapToResponse)
    	    .collect(Collectors.toList());

    	// Crée un objet Map pour stocker les données à retourner au client (souvent sous forme JSON)
    	Map<String, Object> response = new HashMap<>();

    	// Ajoute la liste de projets transformés à la réponse
    	response.put("content", content);

    	// Ajoute le numéro de la page actuelle
    	response.put("currentPage", pageProjets.getNumber());

    	// Ajoute le nombre total d'éléments (projets) disponibles dans la base
    	response.put("totalElements", pageProjets.getTotalElements());

    	// Ajoute le nombre total de pages disponibles en fonction de la taille de page demandée
    	response.put("totalPages", pageProjets.getTotalPages());

    	// Retourne la réponse complète, qui sera convertie automatiquement en JSON si utilisée dans un contrôleur REST
    	return response;
    }

    @Override
    public void deleteProjet(Long id) {
        projetRepository.deleteById(id);
    }

    private ProjetResponse mapToResponse(Projet projet) {
       

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
                projet.getSommeReel()
        );
    }

    private Projet mapToEntity(ProjetRequest request) {
        Projet projet = new Projet(
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
                request.getSommeReel()
        );

       
        return projet;
    }

    @Override
    public int NombreProjets() {
        return this.projetRepository.nombreProjetsActifs();
    }
    
    @Override 
    public List<Projet> projetDRAFT (){
    	List<Projet> projetsDraft =  this.projetRepository.listprojetsDRAFT();
    	return projetsDraft ;
    }
    
    @Override
    public Page<ProjetResponse> findByResponsable(String responsable, Pageable pageable) {
        return projetRepository.findByResponsableContainingIgnoreCase(responsable, pageable)
                .map(this::mapToResponse);
    }

}
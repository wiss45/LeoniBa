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
}
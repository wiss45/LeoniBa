package com.sip.services;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sip.entities.Equipement;
import com.sip.entities.Plan;
import com.sip.entities.Projet;
import com.sip.enums.StatutEquipement;
import com.sip.interfaces.PlanService;
import com.sip.interfaces.StatutCountPerProjet;
import com.sip.repositories.EquipementRepository;
import com.sip.repositories.PlanRepository;
import com.sip.repositories.ProjetRepository;
import com.sip.requests.PlanRequest;
import com.sip.responses.PlanResponse;

@Service
public class PlanServiceImp implements PlanService {

    @Autowired
    private PlanRepository planRepository;
  
    
    @Autowired
    private EquipementRepository equipementRepository;
    
    
    @Autowired
    private ProjetRepository projetRepository;

    @Override
    public PlanResponse createPlan( PlanRequest request) {
       

        Plan plan = new Plan();
        plan.setProjet(request.getProjet());
        plan.setEquipement(request.getEquipement());
        plan.setOrderNumber(request.getOrderNumber());
        plan.setOrderPrice(request.getOrderPrice());
        plan.setPamNumber(request.getPamNumber());
        plan.setQuantite(request.getQuantite());
        plan.setTargetDate(request.getTargetDate());
        plan.setDeliveryDate(request.getDeliveryDate());
        plan.setRprdate(request.getRprdate());
        plan.setStatut(StatutEquipement.EN_ATTENTE);
        Plan savedPlan = planRepository.save(plan);
        return toResponse(savedPlan);
    }
    
    
    @Override
    public PlanResponse updatePlan(long id, PlanRequest request) {

        Plan plan = this.planRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Plan n'existe pas : " + id));

       
        plan.setOrderNumber(request.getOrderNumber());
        plan.setOrderPrice(request.getOrderPrice());
        plan.setPamNumber(request.getPamNumber());
        plan.setQuantite(request.getQuantite());
        plan.setTargetDate(request.getTargetDate());
        plan.setDeliveryDate(request.getDeliveryDate());
        plan.setRprdate(request.getRprdate());
        plan.setStatut(request.getStatut());

        // Mettre à jour le projet (si fourni)
        if (request.getProjet() != null && request.getProjet().getId() != null) {
            Projet projet = projetRepository.findById(request.getProjet().getId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé : " + request.getProjet().getId()));
            plan.setProjet(projet);
        }

        // Mettre à jour l’équipement (si fourni)
        if (request.getEquipement() != null && request.getEquipement().getId() != null) {
            Equipement equipement = equipementRepository.findById(request.getEquipement().getId())
                .orElseThrow(() -> new RuntimeException("Équipement non trouvé : " + request.getEquipement().getId()));
            plan.setEquipement(equipement);
        }

        Plan savedPlan = planRepository.save(plan);
        return toResponse(savedPlan);
    }


    @Override
    public Map<String, Object> getAllPlans(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Plan> pagePlans = planRepository.findAll(pageable);

        List<PlanResponse> planResponses = pagePlans.getContent()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", planResponses);
        response.put("currentPage", pagePlans.getNumber());
        response.put("totalItems", pagePlans.getTotalElements());
        response.put("totalPages", pagePlans.getTotalPages());

        return response;
    }

    @Override
    public Plan getPlanById(long id) {
    	Plan plan = this.planRepository.findById(id).orElseThrow(()->new RuntimeException ("Plan n'existe pas" + id));
    	return plan ;
    }


    @Override
    public void deletePlan(Long id) {
        if (!planRepository.existsById(id)) {
            throw new RuntimeException("Le plan avec ID " + id + " n'existe pas");
        }
        planRepository.deleteById(id);
    }
    
   /* @Override
    public int nombreEquipementsPlan(Long planId) {
        return this.planRepository.countEquipementsByPlanId(planId);
    }
*/
    
    
    @Override
    public List<PlanResponse> getPlansByProjetId(Long projetId) {
        List<Plan> plans = planRepository.findByProjetId(projetId);

        return plans.stream()
            .map(plan -> new PlanResponse(
                plan.getId(),
                plan.getProjet(),
                plan.getEquipement(),
                plan.getOrderNumber(),
                plan.getOrderPrice(),
                plan.getPamNumber(),
                plan.getQuantite(),
                plan.getTargetDate(),
                plan.getDeliveryDate(),
                plan.getRprdate(),
                plan.getStatut()
            ))
            .collect(Collectors.toList());
    }

    
    private PlanResponse toResponse(Plan plan) {
        PlanResponse response = new PlanResponse();
        response.setId(plan.getId());
        response.setProjet(plan.getProjet());
        response.setEquipement(plan.getEquipement());
        response.setOrderNumber(plan.getOrderNumber());
        response.setOrderPrice(plan.getOrderPrice());
        response.setPamNumber(plan.getPamNumber());
        response.setQuantite(plan.getQuantite());
        response.setTargetDate(plan.getTargetDate());
        response.setDeliveryDate(plan.getDeliveryDate());
        response.setRprdate(plan.getRprdate());
        response.setStatut(plan.getStatut());
        return response;
    }
    
    
    @Override
    public List<PlanResponse> getAllPlans() {
        List<Plan> plans = this.planRepository.findAll();
        return plans.stream()
            .map(this::toResponse) 
            .collect(Collectors.toList());
    }
    
    @Override
    public Map<Long, Map<StatutEquipement, Long>> countPlansByStatutPerProjet() {
        List<StatutCountPerProjet> results = planRepository.countPlansByStatutPerProjet();

        Map<Long, Map<StatutEquipement, Long>> groupedData = new HashMap<>();

        for (StatutCountPerProjet row : results) {
            groupedData
                .computeIfAbsent(row.getProjetId(), k -> new HashMap<>())
                .put(row.getStatut(), row.getCount());
        }

        return groupedData;
    }
    
}

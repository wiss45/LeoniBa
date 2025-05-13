package com.sip.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sip.entities.Plan;
import com.sip.enums.StatutEquipement;
import com.sip.interfaces.PlanService;
import com.sip.requests.PlanRequest;
import com.sip.responses.PlanResponse;

@CrossOrigin("*")
@RestController
@RequestMapping("/plans")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    
    @GetMapping("/allplans")
    public List<PlanResponse> getAlllPlans (){
    	return this.planService.getAllPlans();
    }
    
    @PostMapping("/")
    public ResponseEntity<PlanResponse> createPlan( @RequestBody PlanRequest request) {
        PlanResponse response = planService.createPlan( request);
        return ResponseEntity.ok(response);
    }
    
    
    @PutMapping("/{id}")
    public ResponseEntity<PlanResponse> updatePlan(@PathVariable long id , @RequestBody PlanRequest request) {
        PlanResponse response = planService.updatePlan(id, request);
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/page/")
    public ResponseEntity<Map<String, Object>> getAllPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = planService.getAllPlans(page, size);
        return ResponseEntity.ok(response);
    }

   

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
    
  /*  @GetMapping("/nombreequipementsplan/{id}")
    public int nombreequipementsplan(@PathVariable Long id) {
        return this.planService.nombreEquipementsPlan(id);
    }*/

    
    @GetMapping("/projets/{projetId}")
    public List<PlanResponse> getPlansByProjetId(@PathVariable Long projetId) {
        return planService.getPlansByProjetId(projetId);
    }

    @GetMapping("/{id}")
    public Plan getPlanById ( @PathVariable long id) {
    	return this.planService.getPlanById(id);
    }
    
    @GetMapping("/statut-par-projet")
    public Map<Long, Map<StatutEquipement, Long>> getPlanCountsByStatutPerProjet() {
        return planService.countPlansByStatutPerProjet();
    }
}

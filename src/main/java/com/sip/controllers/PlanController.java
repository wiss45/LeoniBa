package com.sip.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


		@PostMapping("/")
	    public ResponseEntity<PlanResponse> createPlan(@RequestBody PlanRequest request) {
	        PlanResponse response = planService.createPlan(request);
	        return ResponseEntity.ok(response);
	    }

	    
	    @GetMapping("/")
	    public ResponseEntity<Map<String, Object>> getAllPlans(
	            @RequestParam(defaultValue = "0") int page,
	            @RequestParam(defaultValue = "10") int size) {

	        Map<String, Object> response = planService.getAllPlans(page, size);
	        System.out.println(response);
	        return ResponseEntity.ok(response);
	    }

	    
	    @PutMapping("/{id}")
	    public ResponseEntity<PlanResponse> updatePlan(
	            @PathVariable Long id,
	            @RequestBody PlanRequest request) {

	        PlanResponse response = planService.updatePlan(id, request);
	        return ResponseEntity.ok(response);
	    }

	   
	    @DeleteMapping("/{id}")
	    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
	        planService.deletePlan(id);
	        return ResponseEntity.noContent().build();
	    }
}

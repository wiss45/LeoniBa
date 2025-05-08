package com.sip.interfaces;

import java.util.List;
import java.util.Map;

import com.sip.entities.Plan;
import com.sip.requests.PlanRequest;
import com.sip.responses.PlanResponse;

public interface PlanService {

	

	

	void deletePlan(Long id);

	Map<String, Object> getAllPlans(int page, int size);

	

	PlanResponse createPlan(PlanRequest request);

	

	/*int nombreEquipementsPlan(Long planId);*/
    
	
	PlanResponse updatePlan(long id, PlanRequest request);

	List<PlanResponse> getPlansByProjetId(Long projetId);

	

}

package com.sip.interfaces;

import java.util.Map;

import com.sip.requests.PlanRequest;
import com.sip.responses.PlanResponse;

public interface PlanService {

	PlanResponse createPlan(PlanRequest request);

	PlanResponse updatePlan(Long id, PlanRequest request);

	void deletePlan(Long id);

	Map<String, Object> getAllPlans(int page, int size);

}

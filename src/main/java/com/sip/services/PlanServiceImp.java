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

import com.sip.entities.Plan;
import com.sip.interfaces.PlanService;
import com.sip.repositories.PlanRepository;
import com.sip.requests.PlanRequest;
import com.sip.responses.PlanResponse;

@Service
public class PlanServiceImp implements PlanService {

    @Autowired
    private PlanRepository planRepository;

    @Override
    public PlanResponse createPlan(PlanRequest request) {
        Plan plan = new Plan();

        plan.setProjet(request.getProjet());
        plan.setEquipements(request.getEquipements());
        plan.setOrderNumber(request.getOrderNumber());
        plan.setOrderPrice(request.getOrderPrice());
        plan.setPamNumber(request.getPamNumber());
        plan.setQuantite(request.getQuantite());
        plan.setTargetDate(request.getTargetDate());
        plan.setDeliveryDate(request.getDeliveryDate());
        plan.setRprdate(request.getRprdate());

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
        System.out.println(response);
        return response;
    }


    @Override
    public PlanResponse updatePlan(Long id, PlanRequest request) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + id));

        plan.setProjet(request.getProjet());
        plan.setEquipements(request.getEquipements());
        plan.setOrderNumber(request.getOrderNumber());
        plan.setOrderPrice(request.getOrderPrice());
        plan.setPamNumber(request.getPamNumber());
        plan.setQuantite(request.getQuantite());
        plan.setTargetDate(request.getTargetDate());
        plan.setDeliveryDate(request.getDeliveryDate());
        plan.setRprdate(request.getRprdate());

        return toResponse(planRepository.save(plan));
    }

    @Override
    public void deletePlan(Long id) {
        planRepository.deleteById(id);
    }

    private PlanResponse toResponse(Plan plan) {
        PlanResponse response = new PlanResponse();

        response.setId(plan.getId());
        response.setProjet(plan.getProjet());
        response.setEquipements(plan.getEquipements());
        response.setOrderNumber(plan.getOrderNumber());
        response.setOrderPrice(plan.getOrderPrice());
        response.setPamNumber(plan.getPamNumber());
        response.setQuantite(plan.getQuantite());
        response.setTargetDate(plan.getTargetDate());
        response.setDeliveryDate(plan.getDeliveryDate());
        response.setRprdate(plan.getRprdate());

        return response;
    }
}

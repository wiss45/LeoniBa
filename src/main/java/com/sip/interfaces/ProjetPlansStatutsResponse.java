package com.sip.interfaces;

import java.util.List;
import java.util.Map;

import com.sip.enums.StatutEquipement;
import com.sip.responses.PlanResponse;

public class ProjetPlansStatutsResponse {
    private Long projetId;
    private String projetNom;
    private Map<StatutEquipement, Long> statuts;
    private List<PlanResponse> plans;
	public Long getProjetId() {
		return projetId;
	}
	public void setProjetId(Long projetId) {
		this.projetId = projetId;
	}
	public String getProjetNom() {
		return projetNom;
	}
	public void setProjetNom(String projetNom) {
		this.projetNom = projetNom;
	}
	public Map<StatutEquipement, Long> getStatuts() {
		return statuts;
	}
	public void setStatuts(Map<StatutEquipement, Long> statuts) {
		this.statuts = statuts;
	}
	public List<PlanResponse> getPlans() {
		return plans;
	}
	public void setPlans(List<PlanResponse> plans) {
		this.plans = plans;
	}

   
}

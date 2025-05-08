package com.sip.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sip.entities.Equipement;
import com.sip.entities.Plan;
import com.sip.entities.Projet;
import com.sip.responses.PlanResponse;

public interface PlanRepository extends JpaRepository<Plan,Long> {

	long countByEquipement(Equipement equipement);
	
	/*@Query("SELECT COUNT(e) FROM Plan p JOIN p.equipements e WHERE p.projet_id = :projetId")
    int countEquipementsByProjetId(@Param("planId") Long projetId);*/
   
	List<Plan> findByProjetId(Long projetId);


}

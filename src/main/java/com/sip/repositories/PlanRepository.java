package com.sip.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sip.entities.Equipement;
import com.sip.entities.Plan;
import com.sip.entities.Projet;
import com.sip.interfaces.StatutCountPerProjet;
import com.sip.responses.PlanResponse;

public interface PlanRepository extends JpaRepository<Plan,Long> {

	long countByEquipement(Equipement equipement);
	
	/*@Query("SELECT COUNT(e) FROM Plan p JOIN p.equipements e WHERE p.projet_id = :projetId")
    int countEquipementsByProjetId(@Param("planId") Long projetId);*/
   
	List<Plan> findByProjetId(Long projetId);

	@Query("SELECT p.projet.id as projetId, p.projet.name as projetName, p.statut as statut, COUNT(p) as count " +
		       "FROM Plan p GROUP BY p.projet.id, p.projet.name, p.statut")
		List<StatutCountPerProjet> countPlansByStatutPerProjet();

	
	/*List<Plan> findByDateLivraisonAndNotificationEnvoyeeFalse(LocalDate date);*/
	

}

package com.sip.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sip.entities.Equipement;

public interface EquipementRepository extends JpaRepository<Equipement,Long>{

	@Query("SELECT COUNT(e)  FROM Equipement e")
	int nombreEquipements ();
}

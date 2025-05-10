package com.sip.repositories;

import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sip.entities.Projet;

public interface ProjetRepository extends JpaRepository<Projet,Long> {
	
	
  @Query("SELECT COUNT(p) FROM Projet p WHERE p.status = APPROVED")
  int nombreProjetsActifs();
  
  @Query("SELECT p FROM Projet p WHERE p.status = 'DRAFT'")
	List<Projet> listprojetsDRAFT () ;
  
  Page<Projet> findByResponsable(String responsable, Pageable pageable);

  Page<Projet> findByResponsableContainingIgnoreCase(String responsable, Pageable pageable);


}

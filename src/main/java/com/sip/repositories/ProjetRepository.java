package com.sip.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sip.entities.Projet;

public interface ProjetRepository extends JpaRepository<Projet,Long> {
  @Query("SELECT COUNT(p) FROM Projet p WHERE p.status = APPROVED")
  int nombreProjetsActifs();
}

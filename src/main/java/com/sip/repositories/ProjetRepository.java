package com.sip.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sip.entities.Projet;

public interface ProjetRepository extends JpaRepository<Projet,Long> {

}

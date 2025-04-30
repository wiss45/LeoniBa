package com.sip.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sip.entities.Plan;

public interface PlanRepository extends JpaRepository<Plan,Long> {

}

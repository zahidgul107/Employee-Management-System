package com.emp.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.emp.management.model.EmployeeDesignation;

@Repository
public interface EmployeeDesignationRepository extends JpaRepository<EmployeeDesignation, Long> {
//	EmployeeDesignation findByDesignation(String designation);

	Object findByDesignation(String designation);

	
}

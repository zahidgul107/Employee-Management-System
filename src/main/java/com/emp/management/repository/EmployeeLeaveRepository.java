package com.emp.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.emp.management.model.Attendance;
import com.emp.management.model.Employee;
import com.emp.management.model.EmployeeLeave;

@Repository
public interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeave, Long> {

	List<EmployeeLeave> findByStatus(String string);

	/*
	 * @Query("Select a from EmployeeLeave a where a.employee.id=:id")
	 * List<EmployeeLeave> findByEmployeeId(@Param("id") long id);
	 */
	
//	void countByEmail(String email);
	
	
	  //@Query("Select a from EmployeeLeave a where a.status=:status")
	  List<EmployeeLeave> findByEmployeeAndStatus(Employee employee, String status);
	 
	

	
}

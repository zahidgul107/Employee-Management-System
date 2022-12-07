package com.emp.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.emp.management.model.Attendance;
import com.emp.management.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer>{
// @Query(value = "SELECT token FROM employee WHERE token IS NOT NULL", nativeQuery = true)
// public List<Employee> getEmployeeByToken();
// @Query("Select a from Attendance a where a. Employee.firstName=:name")
	
//	@Query("Select a from Employee a where a.employee.token=:token")
//	List<Employee> getEmployeeByToken(@Param("token") String token);
	
//	@Query("Select a from Attendance a where a. Employee.firstName=:name")
//	List<Attendance> findByEmployee(@Param("name") String name);
	
	public List<Employee> findByTokenIsNotNull();
}

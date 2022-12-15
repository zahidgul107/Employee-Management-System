package com.emp.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.emp.management.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{
// @Query(value = "SELECT token FROM employee WHERE token IS NOT NULL", nativeQuery = true)
// public List<Employee> getEmployeeByToken();
// @Query("Select a from Attendance a where a. Employee.firstName=:name")
	
//	@Query("Select a from Employee a where a.employee.token=:token")
//	List<Employee> getEmployeeByToken(@Param("token") String token);
	
//	@Query("Select a from Attendance a where a. Employee.firstName=:name")
//	List<Attendance> findByEmployee(@Param("name") String name);
	
	public List<Employee> findByTokenIsNotNull();
	

//	@Query("SELECT COUNT(u.id) FROM Employee u WHERE u.email=:email") 
//	Long countEmployeeWithEmail(String email);
	
//	@Query("SELECT u FROM Employee u WHERE u.email !=:email")
//	Employee checkByEmail(@Param ("email") String email);
	
	Employee findByEmail(String email);


	Employee findByUserName(String userName);
	
	
	  @Query("SELECT u FROM Employee u WHERE u.userName !=:userName") 
	  public Employee checkByUserName(@Param ("userName") String userName);


	


	
	 
}

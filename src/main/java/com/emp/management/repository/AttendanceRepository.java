package com.emp.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.emp.management.model.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

	@Query("Select a from Attendance a where a.employee.id=:id")
	List<Attendance> findByEmployeeId(@Param("id") int id);
//	@Query("Select a from Attendance a where a. Employee.firstName=:name")
//	List<Attendance> findByEmployee(@Param("name") String name);

}

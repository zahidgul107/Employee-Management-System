package com.emp.management.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.emp.management.model.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

	@Query("Select a from Attendance a where a.employee.id=:id")
	Page<Attendance> findByEmployeeId(@Param("id") long id, Pageable pageable);
//	@Query("Select a from Attendance a where a. Employee.firstName=:name")
//	List<Attendance> findByEmployee(@Param("name") String name);


	Attendance findByDate(String date);


	//List<Attendance> findByTimeOutNull();


	//List<Attendance> findByTimeOutIsNull();


//	List<Attendance> findByTimeOutIsNull(String timeOut);

	//@Query("Select u from Attendance u where u.timeIn !=:null and timeOut =:null")
	//List<Attendance> findByTimeInAndTimeOut(@Param("timeIn")String timeIn,@Param("timeOut")String timeOut);
	
	@Query("SELECT u FROM Attendance u WHERE u.timeOut =:timeOut")
	 List<Attendance> checkByTimeOut(@Param("timeOut") String timeOut);


	List<Attendance> findByEmployeeIdAndTimeOutIsNull(long id);

//	@Query("SELECT e FROM Employee e WHERE e.timeOut IS NULL")
//	List<Attendance> findByTimeOutIsNull(String timeOut);


//	Att checkByTimeOut(Object object);
	
//	Attendance findByTimeOutISNULL(String timeOut); 

}

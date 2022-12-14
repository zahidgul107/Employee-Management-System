package com.emp.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.emp.management.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long>{

	@Query("SELECT u FROM Admin u WHERE u.userName !=:userName")
	public Admin checkByUserName(@Param ("userName") String userName);
	

}

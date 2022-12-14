package com.emp.management.config.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.emp.management.model.Admin;
import com.emp.management.repository.AdminRepository;

@Component
public class AdminConfig {

	
	
	private AdminRepository adminRepo;
	@Autowired
	public AdminConfig(AdminRepository adminRepo) {
		this.adminRepo=adminRepo;
		LoadUser();
	}
	
	
	public void LoadUser() {
		
		Admin admin = new Admin("admin", "admin");
		Admin adminUser = adminRepo.checkByUserName(admin.getUserName());
		if (adminUser != null) {
			BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
			String encrypted = bCrypt.encode(admin.getPassword());
			admin.setPassword(encrypted);
			adminRepo.save(admin);
		}
		
}
}
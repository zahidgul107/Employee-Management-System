package com.emp.management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.emp.management.model.Employee;
import com.emp.management.repository.EmployeeRepository;

@Controller
public class HomeController {
	
	@Autowired
	private EmployeeRepository empRepo;
	
	@GetMapping("/home")
	public String homeView() {
		List<Employee> employee=empRepo.findByTokenIsNotNull();
		for (Employee emplu : employee) {
			emplu.setToken(null);
			empRepo.save(emplu);
		}
		return "home";
	}
}

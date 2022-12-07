package com.emp.management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.emp.management.model.Attendance;
import com.emp.management.model.Employee;
import com.emp.management.model.EmployeeDesignation;
import com.emp.management.repository.AttendanceRepository;
import com.emp.management.repository.EmployeeRepository;

@Controller
public class EmployeeDashboard {
	
	@Autowired
	private AttendanceRepository attendanceRepo;
	
	@Autowired
	private EmployeeRepository employeeRepo;
	
	@GetMapping("/employeeLogin")
	public String employeeLogin(Model model) {
		Employee employee = new Employee();
		model.addAttribute("employee", employee);
		return "login";
	}
	
	@PostMapping("/login")
	public String signIn(@ModelAttribute("employee") Employee employee) {
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		List<Employee> employeeList = employeeRepo.findAll();
		for (Employee emp : employeeList) {
			System.out.println(emp.getUserName());
			System.out.println(emp.getPassword());
			if (bcrypt.matches(employee.getPassword(), emp.getPassword()) && emp.getUserName().equals(employee.getUserName())) {
				
			
		//	if (emp.getUserName().equals(employee.getUserName()) && emp.getPassword().equals(employee.getPassword())) {
				return "redirect:/employeeView";	
			}
		}
		return "redirect:/employeeLogin";
	}
	
	@GetMapping("/employeeView")
	public String employeeView(Model model) {
		Employee employee = new Employee();
		model.addAttribute("employee", employee);
		return "employee_view";
	}
	
	
	@GetMapping("/showAttendanceForm")
	public String showEmployeeForm(Model model) {
		
		// create model attribute to bind form data
		Attendance attendance = new Attendance();
		model.addAttribute("attendance", attendance);
		List<Employee> listEmployees = employeeRepo.findAll();
		
		model.addAttribute("listEmployees", listEmployees);
		
		return "attendance_form";
	}
	
	@PostMapping("/saveAttendance")
	public String saveAttendance(@ModelAttribute("attendance") Attendance attendance) {
		attendanceRepo.save(attendance);
		return "redirect:/employeeView";
		
	}

}

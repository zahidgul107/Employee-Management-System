package com.emp.management.controller;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emp.management.model.Attendance;
import com.emp.management.model.Employee;
import com.emp.management.model.EmployeeDesignation;
import com.emp.management.repository.AttendanceRepository;
import com.emp.management.repository.EmployeeRepository;

import antlr.Token;

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
	public String signIn(@ModelAttribute("employee") Employee employee,RedirectAttributes rd) {
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		List<Employee> employeeList = employeeRepo.findAll();
		
		// create a string of all characters
	    
		for (Employee emp : employeeList) {
			System.out.println(emp.getUserName());
			System.out.println(emp.getPassword());
			if (bcrypt.matches(employee.getPassword(), emp.getPassword()) && emp.getUserName().equals(employee.getUserName())) {
				emp.setToken(randomString());
			    employeeRepo.save(emp);
			    rd.addFlashAttribute("success", "Login In successfully");
		//	if (emp.getUserName().equals(employee.getUserName()) && emp.getPassword().equals(employee.getPassword())) {
				return "redirect:/employeeView";	
			}
		}
		rd.addFlashAttribute("fail", "username or password incorrect");
		return "redirect:/employeeLogin";
	}
	
	@GetMapping("/employeeView")
	public String employeeView(Model model) {
		Employee employee = new Employee();
		model.addAttribute("employee", employee);
		return "employee_view";
	}
	
	
	@GetMapping("/showAttendanceForm{token}")
	public String showEmployeeForm(@PathVariable(value="token") String token,Model model) {
		
		// create model attribute to bind form data
		Attendance attendance = new Attendance();
		model.addAttribute("attendance", attendance);
		List<Employee> listEmployees = employeeRepo.findByTokenIsNotNull();
		model.addAttribute("listEmployees", listEmployees);
		return "attendance_form";
	}
	
	@PostMapping("/saveAttendance")
	public String saveAttendance(@ModelAttribute("attendance") Attendance attendance,RedirectAttributes rd) {
		attendanceRepo.save(attendance);
		rd.addFlashAttribute("success","Attendance submitted successfull");
		return "redirect:/employeeView";
		
	}
	public String randomString() {
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	    // create random string builder
	    StringBuilder sb = new StringBuilder();

	    // create an object of Random class
	    Random random = new Random();

	    // specify length of random string
	    int length = 7;

	    for(int i = 0; i < length; i++) {

	      // generate random index number
	      int index = random.nextInt(alphabet.length());

	      // get character specified by index
	      // from the string
	      char randomChar = alphabet.charAt(index);

	      // append the character to string builder
	      sb.append(randomChar);
	    }

	    String randomString = sb.toString();
	    System.out.println(randomString);

		
		return randomString;
		
	}

}

package com.emp.management.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.standard.expression.Each;

import com.emp.management.model.Attendance;
import com.emp.management.model.Employee;
import com.emp.management.model.EmployeeDesignation;
import com.emp.management.repository.AttendanceRepository;
import com.emp.management.repository.EmployeeDesignationRepository;
import com.emp.management.repository.EmployeeRepository;

@Controller
public class AdminDashboardController {

	@Autowired
	private EmployeeRepository empRepo;

	@Autowired
	private EmployeeDesignationRepository empDesigRepo;

	@Autowired
	private AttendanceRepository attendanceRepo;

	@GetMapping("/adminLogin")
	public String adminLogin() {
		return "admin_view";
	}

	@GetMapping("/showEmployeeForm")
	public String showEmployeeForm(Model model) {

		// create model attribute to bind form data
		Employee employee = new Employee();
		model.addAttribute("employee", employee);
		List<EmployeeDesignation> listDesignation = empDesigRepo.findAll();

		model.addAttribute("listDesignation", listDesignation);
		return "employee_form";
	}

	@PostMapping("/saveEmployee")
	public String saveEmployee(@ModelAttribute("employee") Employee employee, RedirectAttributes rd) {
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		
/*		Employee emp = empRepo.checkByUserName(employee.getUserName());
		if (empRepo.findByEmail(employee.getEmail()) != null) {
			System.out.println("faillllllll");
			rd.addFlashAttribute("fail", "Email already exists");
			return "redirect:/showEmployeeForm";
		} else if (emp == null) {
			System.out.println("username exists");
			rd.addFlashAttribute("fail", "username already exists");
			return "redirect:/showEmployeeForm";
		} else {		*/
			System.out.println(employee.getFirstName());
			String str1 = employee.getFirstName();
			char[] ch = str1.toCharArray();
			for(int i = 0; i <= 3; i++ ) {
				System.out.println(ch[i]);
			}

			System.out.println(employee.getDob());
			String empdob = employee.getDob();
			String[] x = empdob.split("-");
			System.out.println(x[0]);
			System.out.println(x[1]);
			
			System.out.println(str1+x[0]+x[1]);
			
			String str2 = str1.concat(x[0]).concat(x[2]);
			System.out.println(str2);
			
			employee.setEmpId(str2);
			
			String encryptedPwd = bcrypt.encode(employee.getPassword());
			employee.setPassword(encryptedPwd);
			empRepo.save(employee);
			rd.addFlashAttribute("success", "employee registered successfully");
			return "redirect:/adminLogin";
//		}
	}

	@GetMapping("/viewEmployees")
	public String viewEmployees(Model model) {
		model.addAttribute("listEmployees", empRepo.findAll());
		return "view_employees";
	}

	@GetMapping("/deleteEmployee/{id}")
	public String deleteEmployee(@PathVariable(value = "id") int id, RedirectAttributes rd) {
		this.empRepo.deleteById((long) id);
		rd.addFlashAttribute("delete", "employee deleted successfully");
		return "redirect:/viewEmployees";
	}

	@GetMapping("/updateEmployee/{id}")
	public String updateEmployee(@PathVariable(value = "id") int id, Model model) {
		// get employee from database
		model.addAttribute("employee", empRepo.getById((long) id));
		List<EmployeeDesignation> listDesignation = empDesigRepo.findAll();
		model.addAttribute("listDesignation", listDesignation);
		return "employee_form";
	}

	// employee designation
	@GetMapping("/showEmployeeDesignationForm")
	public String showEmployeeDesignationForm(Model model) {

		// create model attribute to bind form data
		EmployeeDesignation empDesig = new EmployeeDesignation();
		model.addAttribute("empDesig", empDesig);
		return "employee_designation_form";
	}

	// saving to database
	@PostMapping("/saveEmployeeDesignation")
	public String saveEmployee(@ModelAttribute("empDesig") EmployeeDesignation empDesig, RedirectAttributes rd) {
		empDesigRepo.save(empDesig);
		rd.addFlashAttribute("success", "Successfully added designation");
		return "redirect:/adminLogin";

	}

	// getting designation
	@GetMapping("/viewDesignations")
	public String viewDesignations(Model model) {
		model.addAttribute("listDesignation", empDesigRepo.findAll());
		return "view_designations";
	}

	@GetMapping("/deleteDesignation/{id}")
	public String deleteDesignation(@PathVariable(value = "id") int id, RedirectAttributes rd) {
		this.empDesigRepo.deleteById(id);
		rd.addFlashAttribute("delete", "designation deleted successfully");
		return "redirect:/viewDesignations";
	}

	@GetMapping("/viewEmployeesAttendance")
	public String viewEmployeesAttendance(Model model) {
		model.addAttribute("listEmployees", empRepo.findAll());
		return "view_employees_attendance";
	}

	@GetMapping("/viewAttendance/{id}")
	public String viewAttendance(@PathVariable(value = "id") int id, Model model) {

		List<Attendance> attendanceList = attendanceRepo.findByEmployeeId(id);
		model.addAttribute("attendanceList", attendanceList);
		for (Attendance employeeAttendance : attendanceList) {
			System.err.println(employeeAttendance.getStatus() + "<<<<<<<<<<<<");
		}

		/*
		 * List<Attendance> attendanceList = attendanceRepo.findAll(); for (Attendance
		 * attendance : attendanceList) {
		 * System.out.println(attendance.getTaskAssigned()); }
		 * model.addAttribute("attendanceList", attendanceList);
		 */
		return "view_attendance";

	}

}

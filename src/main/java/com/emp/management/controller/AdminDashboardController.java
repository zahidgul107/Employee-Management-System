package com.emp.management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emp.management.model.Admin;
import com.emp.management.model.Attendance;
import com.emp.management.model.Employee;
import com.emp.management.model.EmployeeDesignation;
import com.emp.management.model.EmployeeLeave;
import com.emp.management.repository.AdminRepository;
import com.emp.management.repository.AttendanceRepository;
import com.emp.management.repository.EmployeeDesignationRepository;
import com.emp.management.repository.EmployeeLeaveRepository;
import com.emp.management.repository.EmployeeRepository;

@Controller
public class AdminDashboardController {

	@Autowired
	private EmployeeRepository empRepo;

	@Autowired
	private EmployeeDesignationRepository empDesigRepo;

	@Autowired
	private AttendanceRepository attendanceRepo;

	@Autowired
	private AdminRepository adminRepo;

	@Autowired
	private EmployeeLeaveRepository empLeaveRepo;

	@GetMapping("/adminLoginForm")
	public String employeeLogin(Model model) {
		Admin admin = new Admin();
		model.addAttribute("admin", admin);

		return "admin_login";
	}

	@PostMapping("/adminLogin")
	public String adminLogin(@ModelAttribute("admin") Admin admin, RedirectAttributes rd) {
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		List<Admin> adminList = adminRepo.findAll();

		for (Admin ad : adminList) {

			if (bcrypt.matches(admin.getPassword(), ad.getPassword()) && ad.getUserName().equals(admin.getUserName())) {
				rd.addFlashAttribute("success", "Login In successfully");
				return "redirect:/adminView";
			}
		}
		rd.addFlashAttribute("fail", "username or password incorrect");
		return "redirect:/adminLoginForm";

	}

	@GetMapping("/adminView")
	public String employeeView(Model model) {
		model.addAttribute("count", empLeaveRepo.findByStatus("created").size());
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

		Employee emp = empRepo.checkByUserName(employee.getUserName());

		if (empRepo.findByEmail(employee.getEmail()) != null) {
			System.out.println("faillllllll");
			rd.addFlashAttribute("fail", "Email already exists");
			return "redirect:/showEmployeeForm";
		} else if (empRepo.checkByUserName(employee.getUserName()) != null) {
			System.out.println("username exists");
			rd.addFlashAttribute("fail", "username already exists");
			return "redirect:/showEmployeeForm";
		} else {
			String str1 = employee.getFirstName();
			String str2 = str1.substring(0,3);
			String empdob = employee.getDob();
			String[] x = empdob.split("-");
			String str3 = str2.concat(x[0]).concat(x[2]);
			employee.setEmpId(str3);		
			String encryptedPwd = bcrypt.encode(employee.getPassword());
			employee.setPassword(encryptedPwd);
			empRepo.save(employee);
			rd.addFlashAttribute("success", "employee registered successfully");
		}
		return "redirect:/adminView";
	}

	@GetMapping("/showEmployeeUpdateForm/{id}")
	public String updateEmployee(@PathVariable(value = "id") int id, Model model) {
		// get employee from database
		model.addAttribute("emp", empRepo.getById((long) id));
		List<EmployeeDesignation> listDesignation = empDesigRepo.findAll();
		model.addAttribute("listDesignation", listDesignation);
		return "update_employee_form";
	}

	@PostMapping("/saveUpdatedEmployee")
	public String saveUpdatedEmployee(@ModelAttribute("emp") Employee emp, RedirectAttributes rd) {
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

		if (empRepo.findByEmailAndId(emp.getEmail(), emp.getId()) != null) {
			System.out.println("faillllllll");
			rd.addFlashAttribute("delete", "Email already exists");
			return "redirect:/showEmployeeUpdateForm/" + emp.getId();
		} else {
			String encryptedPwd = bcrypt.encode(emp.getPassword());
			emp.setPassword(encryptedPwd);
			empRepo.save(emp);
			rd.addFlashAttribute("success", "employee updated successfully");
		}
		return "redirect:/adminView";
	}
	
	@GetMapping("/viewEmployees/{page}")
	public String viewPaginatedEmployees(@PathVariable(value = "page")int page,Model model) {
		Pageable pageable = PageRequest.of(page-1, 10);
		Page<Employee> list = empRepo.findAll(pageable);
		model.addAttribute("listEmployees", list.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", list.getTotalPages());
		model.addAttribute("totalItems", list.getTotalElements());
		return "view_employees";
	}

	@GetMapping("/viewEmployees")
	public String viewEmployees(Model model) {
		
		int page = 1;
		Pageable pageable = PageRequest.of(page-1, 10);
		Page<Employee> list = empRepo.findAll(pageable);
		model.addAttribute("listEmployees", list.getContent());
		model.addAttribute("totalPages", list.getTotalPages());
		model.addAttribute("totalItems", list.getTotalElements());
		model.addAttribute("currentPage", page);
		return "view_employees";
	}

	@GetMapping("/deleteEmployee/{id}")
	public String deleteEmployee(@PathVariable(value = "id") int id, RedirectAttributes rd) {
		this.empRepo.deleteById((long) id);
		rd.addFlashAttribute("delete", "employee deleted successfully");
		return "redirect:/viewEmployees";
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
		if (empDesigRepo.findByDesignation(empDesig.getDesignation()) != null) {
			System.out.println("failllllllllllllllll");
			rd.addFlashAttribute("fail", "Designation already exists");
			return "redirect:/showEmployeeDesignationForm";
		} else {
		empDesigRepo.save(empDesig);
		rd.addFlashAttribute("success", "Successfully added designation");
		return "redirect:/adminView";
		}
	}

	// getting designation
	@GetMapping("/viewDesignations")
	public String viewDesignations(Model model) {
		model.addAttribute("listDesignation", empDesigRepo.findAll());
		return "view_designations";
	}

	@GetMapping("/deleteDesignation/{id}")
	public String deleteDesignation(@PathVariable(value = "id") long id, RedirectAttributes rd) {
		this.empDesigRepo.deleteById(id);
		rd.addFlashAttribute("delete", "designation deleted successfully");
		return "redirect:/viewDesignations";
	}

	@GetMapping("/viewEmployeesAttendance")
	public String viewEmployeesAttendance(Model model) {
		model.addAttribute("listEmployees", empRepo.findAll());
		return "view_employees_attendance";
	}

/*	@GetMapping("/viewAttendance/{id}")
	public String viewAttendance(@PathVariable(value = "id") long id, Model model) {
		
		int page = 1;
		Pageable pageable = PageRequest.of(page-1, 5);
		Page<Attendance> attendanceList = attendanceRepo.findByEmployeeId(id,pageable);
		model.addAttribute("attendanceList", attendanceList.getContent());
		model.addAttribute("currentPages", page);
		model.addAttribute("totalPages", attendanceList.getTotalPages());
		model.addAttribute("totalItems", attendanceList.getTotalElements());
		return "view_attendance";
	}		*/
	
	@GetMapping("/viewAttendance/{id}/{page}")
	public String viewPaginatedAttendance(@PathVariable(value = "id") long id,@PathVariable(value = "page")int page,Model model) {
		
		Pageable pageable = PageRequest.of(page-1, 5);
		Page<Attendance> attendanceList = attendanceRepo.findByEmployeeId(id, pageable);
		List<Attendance> list = attendanceList.getContent();
		for (Attendance attendance : list) {
			model.addAttribute("empId", attendance.getEmployee().getId());
		}
		model.addAttribute("attendanceList", list);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", attendanceList.getTotalPages());
		model.addAttribute("totalItems", attendanceList.getTotalElements());
		return "view_attendance";
	}

	@GetMapping("/viewEmployeesLeave")
	public String viewEmployeesLeave(Model model) {
		List<EmployeeLeave> listLeave = empLeaveRepo.findByStatus("created");
		for (EmployeeLeave Leave : listLeave) {
			System.err.println(Leave.getEmployee().getFirstName());
			model.addAttribute("Leave", Leave);
			
		}
		
		model.addAttribute("count", empLeaveRepo.findByStatus("created").size());
		return "view_employees_leave";
	}

	@GetMapping("/viewLeave/{id}")
	public String viewLeave(@PathVariable(value = "id") int id, Model model) {

		List<EmployeeLeave> listLeave = empLeaveRepo.findByStatus("created");
		for (EmployeeLeave employeeLeave : listLeave) {
		//	System.err.println(employeeLeave.getEmployee().getId());
		//	System.err.println(employeeLeave.getReason());
			System.err.println(employeeLeave.getFromDate()+ " : "+employeeLeave.getToDate());
			model.addAttribute("employeeLeave", employeeLeave);
		}
		model.addAttribute("listLeave", listLeave);
		for (EmployeeLeave employeeLeave : listLeave) {
		//	System.err.println(employeeLeave.getReason() + "<<<<<<<<<<<<");
		}

		EmployeeLeave leaves = new EmployeeLeave();
		model.addAttribute("leaves", leaves);
		return "view_leave";

	}

	@PostMapping("/adminLeaveStatus")
	public String leaveStatus(@ModelAttribute("leaves") EmployeeLeave leaves) {
		empLeaveRepo.save(leaves);
		return "redirect:/viewEmployeesLeave";
	}

}

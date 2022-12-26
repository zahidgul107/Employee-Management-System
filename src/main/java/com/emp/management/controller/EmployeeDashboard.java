package com.emp.management.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emp.management.model.Attendance;
import com.emp.management.model.Employee;
import com.emp.management.model.EmployeeDesignation;
import com.emp.management.model.EmployeeLeave;
import com.emp.management.repository.AttendanceRepository;
import com.emp.management.repository.EmployeeLeaveRepository;
import com.emp.management.repository.EmployeeRepository;

import antlr.Token;

@Controller
public class EmployeeDashboard {

	@Autowired
	private AttendanceRepository attendanceRepo;

	@Autowired
	private EmployeeRepository employeeRepo;

	@Autowired
	private EmployeeLeaveRepository empLeaveRepo;

	@GetMapping("/employeeLogin")
	public String employeeLogin(Model model) {
		Employee employee = new Employee();
		model.addAttribute("employee", employee);

		return "login";
	}

	@PostMapping("/login")
	public String signIn(@ModelAttribute("employee") Employee employee, RedirectAttributes rd,Model model) {
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		List<Employee> employeeList = employeeRepo.findAll();
		for (Employee emp : employeeList) {
			if (bcrypt.matches(employee.getPassword(), emp.getPassword())
					&& emp.getUserName().equals(employee.getUserName())) {
				emp.setToken(randomString());
				System.err.println(emp.getToken());
				String token = emp.getToken();
				rd.addAttribute("token", token);
				employeeRepo.save(emp);
				rd.addFlashAttribute("success", "Login In successfully");
				return "redirect:/employeeView";
			}
		}
		rd.addFlashAttribute("fail", "username or password incorrect");
		return "redirect:/employeeLogin";
	}

	@GetMapping("/employeeView")
	public String employeeView(@ModelAttribute("token")String token,Model model) {
	//	System.err.println(token);
	//	model.addAttribute("emp", employeeRepo.findByTokenIsNotNull());
		List<Employee> empList = employeeRepo.findByToken(token);
	//	System.err.println(emp.getId());
		for (Employee emp : empList) {
	//		System.err.println(emp.getId());
			model.addAttribute("id", emp.getId());
		}
		model.addAttribute("token", token);
		return "employee_view";
	}

	@GetMapping("/showAttendanceForm")
	public String showEmployeeForm(@RequestParam String token,Model model) {
	//	System.err.println(token);
		model.addAttribute("token", token);
		String timeIn = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
		String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		// create model attribute to bind form data
		Attendance attendance = new Attendance();
		attendance.setDate(date);
		attendance.setTimeIn(timeIn);
		model.addAttribute("attendance", attendance);
		List<Employee> listEmployees = employeeRepo.findByToken(token);
		model.addAttribute("listEmployees", listEmployees);
		return "attendance_form";
	}

	@PostMapping("/saveAttendance")
	public String saveAttendance(@RequestParam String token,@ModelAttribute("attendance") Attendance attendance, RedirectAttributes rd) {
	//	System.err.println(token);
		if (attendanceRepo.findByDate(attendance.getDate()) != null) {
			rd.addAttribute("token", token);
			rd.addFlashAttribute("fail", "Attendance already done");
			return "redirect:/showAttendanceForm";
		} else {
			attendanceRepo.save(attendance);
			rd.addFlashAttribute("success", "Attendance submitted successfull");
			rd.addAttribute("token", token);
			return "redirect:/employeeView";
		}
	}

	@GetMapping("/showUpdateAttendanceForm/{id}")
	public String showUpdateAttendanceForm(@RequestParam String token,@PathVariable(value = "id") long id, Model model) {
		System.err.println(token);
		model.addAttribute("token", token);
		String timeOut = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
		List<Attendance> timeInAttendance = attendanceRepo.findByEmployeeIdAndTimeOutIsNull(id);
		model.addAttribute("timeInAttendance", timeInAttendance);
		for (Attendance attendance : timeInAttendance) {
			System.err.println(attendance.getTimeIn());
		}
		Attendance attendance = new Attendance();
		attendance.setTimeOut(timeOut);
		model.addAttribute("attendance", attendance);
	//	model.addAttribute("listEmployees", employeeRepo.findByTokenIsNotNull());

		return "update_attendance_form";
	}

	@PostMapping("/updateAttendance")
	public String saveUpdatedAttendance(@ModelAttribute("attendance") Attendance attendance, RedirectAttributes rd) {
		Attendance att = attendanceRepo.findById(attendance.getId()).get();
		if (att.getTimeOut() != null) {
			rd.addFlashAttribute("success", "You have already taken attendance");
			return "redirect:/employeeView";
		} else {
			System.err.println("hereeeeeeeeeeeeeeeeeeeeee");
			attendanceRepo.save(attendance);
			rd.addFlashAttribute("success", "Attendance updated successfully");
			return "redirect:/employeeView";
		}

	}

	@GetMapping("/showLeaveForm")
	public String showLeaveForm(Model model) {

		// create model attribute to bind form data
		EmployeeLeave leave = new EmployeeLeave();
		model.addAttribute("leave", leave);
		List<Employee> listEmployees = employeeRepo.findByTokenIsNotNull();
		model.addAttribute("listEmployees", listEmployees);
		return "leave_form";
	}

	@PostMapping("/approveLeave")
	public String acceptLeave(@ModelAttribute("leave") EmployeeLeave leave, RedirectAttributes rd) {
		System.err.println(leave.getFromDate());
		if (empLeaveRepo.findByFromDate(leave.getFromDate()) != null) {
			rd.addFlashAttribute("fail", "Your have already submitted leave from this date");
			return "redirect:/showLeaveForm";
		} else if (empLeaveRepo.findByToDate(leave.getToDate()) != null) {
			rd.addFlashAttribute("fail", "you have already submitted leave to this date");
			return "redirect:/showLeaveForm";
		}
		
		else {
			leave.setStatus("created");
			empLeaveRepo.save(leave);
			rd.addFlashAttribute("success", "leave submitted successfull");
			return "redirect:/employeeView";
		}
	}

	@GetMapping("/showEmployeeLeaveStatus")
	public String showEmployeeLeaves() {
		return "employee_leave_view";
	}

	@GetMapping("/showLeaveStatus/{status}/{page}")
	public String showEmployeeLeavedStatus(@PathVariable(value = "status") String status,@PathVariable(value = "page")int page, Model model) {

		ArrayList<Employee> list = new ArrayList<>();
		List<Employee> listEmployees = employeeRepo.findByTokenIsNotNull();
		for (Employee employee : listEmployees) {
		//	System.out.println(employee.getId());
			list.add(employee);
		}	
		Pageable pageable = PageRequest.of(page-1, 5);
		Page<EmployeeLeave> listLeave = empLeaveRepo.findByEmployeeAndStatus(list.get(0), status, pageable);
		model.addAttribute("listLeave", listLeave.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", listLeave.getTotalPages());
		model.addAttribute("totalItems", listLeave.getTotalElements());
		model.addAttribute("status", status);
		return "employee_leave_status";
	}

	public String randomString() {
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		// create random string builder
		StringBuilder sb = new StringBuilder();

		// create an object of Random class
		Random random = new Random();

		// specify length of random string
		int length = 7;

		for (int i = 0; i < length; i++) {

			// generate random index number
			int index = random.nextInt(alphabet.length());

			// get character specified by index
			// from the string
			char randomChar = alphabet.charAt(index);

			// append the character to string builder
			sb.append(randomChar);
		}

		String randomString = sb.toString();
	//	System.out.println(randomString);

		return randomString;

	}

}

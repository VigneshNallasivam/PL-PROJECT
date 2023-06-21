package com.intelizign.pl.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.MethodNotAllowedException;

import com.fasterxml.jackson.annotation.JsonView;
import com.intelizign.pl.model.ERole;
import com.intelizign.pl.model.EmployeeModel;
import com.intelizign.pl.model.Role;
import com.intelizign.pl.repositories.EmployeeRepository;
import com.intelizign.pl.repositories.RoleRepository;
import com.intelizign.pl.request.AddEmployeeRequest;
import com.intelizign.pl.response.MessageResponse;
import com.intelizign.pl.response.ResponseHandler;
import com.intelizign.pl.service.EmailService;
import com.intelizign.pl.service.EmployeeService;
import com.intelizign.pl.utils.CustomFields;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private EmployeeRepository employeeRepo;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	EmailService emailService;

	@Autowired
	RoleRepository roleRepo;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	Environment env;

	Logger Logger = LoggerFactory.getLogger(EmployeeController.class);

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		return errors;
	}

	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler(value = MethodNotAllowedException.class)
	public ResponseEntity<Object> handleMethodNotAllowedExceptionException(MethodNotAllowedException ex) {
		return ResponseHandler.generateResponse(ex.getMessage(), false, HttpStatus.METHOD_NOT_ALLOWED, null);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@PostMapping("/addEmployee")
	public ResponseEntity<Object> registerEmployee(@Valid @RequestBody AddEmployeeRequest signUpRequest)
			throws ConstraintViolationException {
		try {
			if (Boolean.TRUE.equals(employeeRepo.existsByUsername(signUpRequest.getUsername()))) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
			}

			if (Boolean.TRUE.equals(employeeRepo.existsByEmail(signUpRequest.getEmail()))) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
			}

			char[] password = employeeService.generatePassword(8);
			String final_password = new String(password);

			// Create new Employee
			EmployeeModel employee = new EmployeeModel(signUpRequest.getEmp_name(), signUpRequest.getEmp_code(),
					signUpRequest.getUsername(), signUpRequest.getEmail(), encoder.encode(final_password),
					signUpRequest.getDesignation(), signUpRequest.getAddress(), signUpRequest.getLocation(),
					signUpRequest.getBranch(), signUpRequest.getCountry(), signUpRequest.getDepartment(),
					LocalDateTime.now(ZoneId.of(env.getProperty("spring.app.timezone"))),
					signUpRequest.getMobile_number(), null);

			List<String> strRoles = signUpRequest.getRoles();

			List<Role> roles = new ArrayList<>();

			if (strRoles == null) {
				Logger.error("Employee Should have minimum one Role");
				return ResponseEntity.badRequest().body(new MessageResponse("Employee Should have minimum one Role"));

			} else {

				for (int i = 0; i < strRoles.size(); i++) {
					switch (strRoles.get(i)) {
					case "admin":
						Role adminRole = roleRepo.findByRoleName(ERole.ADMIN)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(adminRole);

						break;

					case "practicehead":
						Role practiceheadRole = roleRepo.findByRoleName(ERole.PRACTICE_HEAD)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(practiceheadRole);

						break;

					case "manager":
						Role managerRole = roleRepo.findByRoleName(ERole.PROJECT_MANAGER)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(managerRole);

						break;

					default:
						Logger.error("Role is not found. Enter valid role");
						return ResponseHandler.generateResponse("Invalid Role: " + strRoles.get(i), false,
								HttpStatus.OK, null);
					}
				}
			}
			employee.setRoles(roles);
			employeeRepo.save(employee);
			emailService.sendmail(signUpRequest.getEmail(), employee, final_password);
			return ResponseHandler.generateResponse(
					"Employee Registered Successfully! and Mail is sent to the Employee", true, HttpStatus.OK, null);
		} catch (Exception ex) {

			if (ex.getMessage().contains("constraint")) {
				return ResponseHandler.generateResponse("Employee Code already exists ", false, HttpStatus.OK, null);
			}
			return ResponseHandler.generateResponse("Employee was not registered " + ex.getMessage(), false,
					HttpStatus.OK, null);
		}
	}

	// Get Particular Employee
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	@JsonView(CustomFields.MyResponseViews.class)
	@GetMapping("/{id}")
	public ResponseEntity<Object> getEmployeebyid(@PathVariable(value = "id") Long id) {
		try {
			return employeeRepo.findById(id).map(userData -> {
				return ResponseHandler.generateResponse("Employee Information Retrieved successfully", true,
						HttpStatus.OK, userData);
			}).orElseGet(() -> {
				Logger.error("User {} Doesn't exist", id);
				return ResponseHandler.generateResponse("Employee with " + id + " Doesn't exist", false, HttpStatus.OK,
						null);
			});
		} catch (Exception e) {
			Logger.error("Internal Server Error while get User : {}", e.getMessage());
			return ResponseHandler.generateResponse("Server Error, Please contact Admin", false, HttpStatus.OK, null);
		}
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	@PutMapping("/editEmployee")
	public ResponseEntity<Object> editEmployeeDetails(@Valid @RequestBody EmployeeModel editRequest) {
		try {
			if (employeeRepo.findById(editRequest.getId()).isPresent()) {
				EmployeeModel updatedata = employeeRepo.findById(editRequest.getId()).get();

				if (!updatedata.getEmail().equals(editRequest.getEmail())) {
					if (Boolean.TRUE.equals(employeeRepo.existsByEmail(updatedata.getEmail()))) {
						Logger.error("Email is already in use!");
						return ResponseHandler.generateResponse("Error: Email is already in use!", false, HttpStatus.OK,
								null);
					}
				}

				updatedata.setAddress(editRequest.getAddress());
				updatedata.setCountry(editRequest.getCountry());
				updatedata.setMobile_number(editRequest.getMobile_number());
				updatedata.setDesignation(editRequest.getDesignation());
				updatedata.setEmail(editRequest.getEmail());
				updatedata.setUsername(editRequest.getUsername());
				EmployeeModel savedEmployee = employeeRepo.save(updatedata);
				return ResponseHandler.generateResponse("Employee details were updated Successfully!", true,
						HttpStatus.OK, savedEmployee);

			} else {
				Logger.error("EmployeeID " + editRequest.getId() + " Doesn't exist to Update Information");
				return ResponseHandler.generateResponse("Employee details was not updated", false, HttpStatus.OK, null);
			}

		} catch (Exception ex) {
			return ResponseHandler.generateResponse("Employee details was not updated" + ex.getMessage(), false,
					HttpStatus.OK, null);
		}
	}

	// Soft Delete
	@DeleteMapping("/deleteEmployee/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> deleteUser(@PathVariable(value = "id") Long id) {
		try {
			return employeeRepo.findById(id).map(employee_data -> {

				employee_data.setActive(false);
				employeeRepo.save(employee_data);
				return ResponseHandler.generateResponse("User Deleted Successfully", true, HttpStatus.OK, null);

			}).orElseGet(() -> {
				Logger.error("Employee with id " + id + " doesn't exist");
				return ResponseHandler.generateResponse("Employee with" + id + " doesn't exist", false, HttpStatus.OK,
						null);
			});
		} catch (Exception ex) {
			Logger.error("Internal Server Error while removing user data: " + ex.getMessage());
			return ResponseHandler.generateResponse("Server Error, Please contact Admin", false, HttpStatus.OK, null);
		}

	}

	// Hard Delete
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> HardDeleteUser(@PathVariable(value = "id") Long id) {
		try {
			if (employeeRepo.findById(id).isPresent()) {
				employeeRepo.deleteById(id);
				return ResponseHandler.generateResponse("User Deleted Successfully", true, HttpStatus.OK, null);

			} else {
				Logger.error("Employee with id " + id + " doesn't exist");
				return ResponseHandler.generateResponse("Employee with" + id + " doesn't exist", false, HttpStatus.OK,
						null);
			}
		} catch (Exception ex) {
			Logger.error("Internal Server Error while removing user data: " + ex.getMessage());
			return ResponseHandler.generateResponse("Server Error, Please contact Admin", false, HttpStatus.OK, null);
		}

	}

	// Get All Employees
	@GetMapping("")
	@JsonView(CustomFields.MyResponseViews.class)
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	public ResponseEntity<Object> getAllEmployees(
			@PageableDefault(size = 10, page = 0, sort = "emp_name", direction = Direction.ASC) Pageable pageable,
			@RequestParam(required = false) String searchKeyword) {
		try {
			Page<EmployeeModel> employees = employeeRepo.findAllEmployeesByPagination(searchKeyword, pageable);
			return ResponseHandler.generateResponse("List of Employees retrieved succesfully", true, HttpStatus.OK,
					employees);
		} catch (Exception ex) {
			Logger.error("Internal Server Error while fetching all employee data: " + ex.getMessage());
			return ResponseHandler.generateResponse("Server Error, Please contact Admin", false, HttpStatus.OK, null);
		}
	}
}

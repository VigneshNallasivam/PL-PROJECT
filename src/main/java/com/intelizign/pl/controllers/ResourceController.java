package com.intelizign.pl.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.MethodNotAllowedException;

import com.fasterxml.jackson.annotation.JsonView;
import com.intelizign.pl.model.ResourceModel;
import com.intelizign.pl.model.ResourceRole;
import com.intelizign.pl.model.VerticalModel;
import com.intelizign.pl.repositories.ResourceRepository;
import com.intelizign.pl.repositories.ResourceRoleRepository;
import com.intelizign.pl.request.ResourceCreateRequest;
import com.intelizign.pl.response.MessageResponse;
import com.intelizign.pl.response.ResponseHandler;
import com.intelizign.pl.service.ResourceService;
import com.intelizign.pl.utils.CustomFields;

@RestController
@RequestMapping("/resource")
public class ResourceController {

	@Autowired
	private ResourceRepository resourceRepo;

	@Autowired
	private ResourceRoleRepository resRoleRepo;

	@Autowired
	private ResourceService resService;

	@Autowired
	private Environment env;

	Logger Logger = LoggerFactory.getLogger(ResourceController.class);

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

	//JSON CREATION
	@GetMapping("/json")
	public ResourceCreateRequest getJSON(ResourceCreateRequest resources)
	{
		return resources;
	}

	// Insert Resource roles
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	@PostMapping("/insertrole")
	public ResponseEntity<Object> insertAllRoles() {
		try {
			List<ResourceRole> roledataList = resRoleRepo.findAll();

			if (roledataList.isEmpty()) {

				ResourceRole employee = new ResourceRole("EMPLOYEE");
				ResourceRole projectLead = new ResourceRole("PROJECT_LEAD");
				ResourceRole projectManager = new ResourceRole("PROJECT_MANAGER");
				roledataList.add(employee);
				roledataList.add(projectLead);
				roledataList.add(projectManager);

				resRoleRepo.saveAll(roledataList);

				return ResponseHandler.generateResponse("Roles Added Successfully", true, HttpStatus.OK, null);

			} else {
				return ResponseHandler.generateResponse("Roles Already Added", true, HttpStatus.OK, null);
			}
		} catch (Exception e) {
			Logger.error("Internal Server Error while insertrole:{}.", e.getMessage());
			return ResponseHandler.generateResponse("Server Error, Please contact Admin", false, HttpStatus.OK, null);
		}
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	@GetMapping("/getRoles")
	public ResponseEntity<Object> getRoles() {
		try {
			List<ResourceRole> roledataList = resRoleRepo.findAll();
			if (roledataList.isEmpty()) {
				return ResponseHandler.generateResponse("Resource Roles is empty", false, HttpStatus.OK, null);
			} else
				return ResponseHandler.generateResponse("Resource Roles retrieved successfully", true, HttpStatus.OK,
						roledataList);
		} catch (Exception ex) {
			Logger.error("Internal Server Error while getting Roles : {}", ex.getMessage());
			return ResponseHandler.generateResponse("Server Error, Please contact Admin", false, HttpStatus.OK, null);
		}
	}

	// Get Particular Resource
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	@JsonView(CustomFields.MyResponseViews.class)
	@GetMapping("/{id}")
	public ResponseEntity<Object> getResourcebyid(@PathVariable(value = "id") Long id) {
		try {
			return resourceRepo.findById(id).map(userData -> {
				return ResponseHandler.generateResponse("Resource Information Retrieved successfully", true,
						HttpStatus.OK, userData);
			}).orElseGet(() -> {
				Logger.error("Resource {} Doesn't exist", id);
				return ResponseHandler.generateResponse("User " + id + " Doesn't exist", false, HttpStatus.OK, null);
			});
		} catch (Exception e) {
			Logger.error("Internal Server Error while getting Resource : {}", e.getMessage());
			return ResponseHandler.generateResponse("Server Error, Please contact Admin", false, HttpStatus.OK, null);
		}
	}

	// Get All Resources
	@GetMapping("")
	@JsonView(CustomFields.MyResponseViews.class)
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> getAllResources(
			@PageableDefault(size = 10, page = 0, sort = "emp_name", direction = Direction.ASC) Pageable pageable,
			@RequestParam(required = false) String searchKeyword) {
		try {
			Page<ResourceModel> resources = resourceRepo.findAllResourcesByPagination(searchKeyword, pageable);

			return ResponseHandler.generateResponse("Pages of Resources retrieved succesfully", true, HttpStatus.OK,
					resources);
		} catch (Exception ex) {
			Logger.error("Internal Server Error while fetching all resources data: " + ex.getMessage());
			return ResponseHandler.generateResponse("Server Error, Please contact Admin", false, HttpStatus.OK, null);
		}
	}

	// Add a single resource
	@PostMapping("/addResource")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> addResource(@Valid @RequestBody ResourceCreateRequest resourceRequest) {
		try {
			if (Boolean.TRUE.equals(resourceRepo.existsByUsername(resourceRequest.getUsername()))) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
			}

			if (Boolean.TRUE.equals(resourceRepo.existsByEmail(resourceRequest.getEmail()))) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
			}

			if (Boolean.TRUE.equals(resourceRepo.existsByEmpcode(resourceRequest.getEmpcode()))) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Employee Code is already in use!"));
			}

			ResourceModel resource = new ResourceModel(resourceRequest.getEmp_name(), resourceRequest.getEmpcode(),
					resourceRequest.getUsername(), resourceRequest.getEmail(), resourceRequest.getEmp_status(),
					resourceRequest.getGender(), resourceRequest.getDesignation(), resourceRequest.getAddress(),
					resourceRequest.getLocation(), resourceRequest.getBranch(), resourceRequest.getCountry(),
					resourceRequest.getRole_name(), resourceRequest.getDate_of_join(), resourceRequest.getDepartment(),
					resourceRequest.getMobile_number(), resourceRequest.getReport_to(), resourceRequest.getCtc());

			resourceRepo.save(resource);
			return ResponseHandler.generateResponse("Resource added Successfully!", true, HttpStatus.OK, null);

		} catch (Exception ex) {

			Logger.error("Internal Server Error while adding Resource : {}", ex.getMessage());
			return ResponseHandler.generateResponse("Resource was not created " + ex.getMessage(), false, HttpStatus.OK,
					null);
		}
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	@PostMapping(path = "/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<Object> addMultipleResources(@RequestParam MultipartFile file) {
		try {
			String excel_Type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
			List<String> tableData = List.of("Emp Name", "Emp Code", "Username", "Email", "Emp Status", "Role Name",
					"Gender", "Designation", "Date Of Join", "Address", "Location", "Branch", "Country", "Department",
					"Mobile Number", "Report To", "CTC");
			List<String> tableHeader = tableData.stream().sorted().toList();
			String SHEET = "Sheet1";

			if (file.getContentType().equals(excel_Type)) {

				List<String> RequestHeader = resService.getHeaderColumns(file, SHEET);
				List<String> sorted_requestHeader = RequestHeader.stream().sorted().toList();

				if (tableHeader.equals(sorted_requestHeader)) {
					resService.save(file);
				}

				else {
					Logger.error("Column names are not equal:");
					return ResponseHandler.generateResponse("Column names are not equal:", false, HttpStatus.OK, null);
				}

				return ResponseHandler.generateResponse("Multiple Resources added succesfully", true, HttpStatus.OK,
						null);
			} else
				return ResponseHandler.generateResponse("Check the file format", false, HttpStatus.OK, null);

		} catch (Exception ex) {
			Logger.error("Internal Server Error while adding Resource : {}", ex.getMessage());
			return ResponseHandler.generateResponse("Multiple Resources could not be added", false, HttpStatus.OK,
					null);
		}
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	@PutMapping("/editResource")
	public ResponseEntity<Object> editResource(@RequestBody ResourceModel updatedata) {
		try {
			if (resourceRepo.findById(updatedata.getId()).isPresent()) {
				ResourceModel resource = resourceRepo.findById(updatedata.getId()).get();

				if (!updatedata.getEmail().equals(resource.getEmail())) {
					if (Boolean.TRUE.equals(resourceRepo.existsByEmail(updatedata.getEmail()))) {
						Logger.error("Email is already in use!");
						return ResponseHandler.generateResponse("Error: Email is already in use!", false, HttpStatus.OK,
								null);
					}
				}

				resource.setAddress(updatedata.getAddress());
				resource.setEmp_status(updatedata.getEmp_status());
				resource.setCountry(updatedata.getCountry());
				resource.setMobile_number(updatedata.getMobile_number());
				resource.setDesignation(updatedata.getDesignation());
				resource.setEmail(updatedata.getEmail());
				resource.setUsername(updatedata.getUsername());
				if (updatedata.getCtc() != resource.getCtc()) {
					resource.setCtc(updatedata.getCtc());
					resource.setCtc_revised_date(LocalDateTime.now(ZoneId.of(env.getProperty("spring.app.timezone"))));
				}

				ResourceModel savedResource = resourceRepo.save(resource);
				return ResponseHandler.generateResponse("Employee details were updated Successfully!", true,
						HttpStatus.OK, savedResource);

			} else {
				Logger.error("EmployeeID " + updatedata.getId() + " Doesn't exist to Update Information");
				return ResponseHandler.generateResponse("Resource details was not updated", false, HttpStatus.OK, null);
			}

		} catch (Exception ex) {
			return ResponseHandler.generateResponse("Resource details was not updated" + ex.getMessage(), false,
					HttpStatus.OK, null);
		}
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	@GetMapping("/resourceReport")
	public ResponseEntity<Resource> glgroupingExport() {

		InputStreamResource file = new InputStreamResource(resService.ResourceExport());
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "resource_data.xlsx")
				.contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);

	}
}

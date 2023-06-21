package com.intelizign.pl.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.intelizign.pl.authentication.UserDetailsImpl;
import com.intelizign.pl.exception.TokenRefreshException;
import com.intelizign.pl.model.ERole;
import com.intelizign.pl.model.EmployeeModel;
import com.intelizign.pl.model.RefreshToken;
import com.intelizign.pl.model.Role;
import com.intelizign.pl.repositories.EmployeeRepository;
import com.intelizign.pl.repositories.RoleRepository;
import com.intelizign.pl.request.ChangePasswordRequest;
import com.intelizign.pl.request.LoginRequest;
import com.intelizign.pl.request.SignupRequest;
import com.intelizign.pl.response.JwtResponse;
import com.intelizign.pl.response.MessageResponse;
import com.intelizign.pl.response.ResponseHandler;
import com.intelizign.pl.response.TokenRefreshResponse;
import com.intelizign.pl.service.EmailService;
import com.intelizign.pl.service.RefreshTokenService;
import com.intelizign.pl.service.TokenGeneratorService;
import com.intelizign.pl.utils.JwtUtils;

@RestController
@EnableAsync
@RequestMapping("/auth")
public class AuthenticationController 
{

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private EmployeeRepository employeeRepo;

	@Autowired
	RoleRepository roleRepo;

	@Autowired
	TokenGeneratorService tokenService;

	@Autowired
	EmailService emailService;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	private Environment env;

	private String token = null;

	Logger Logger = LoggerFactory.getLogger(AuthenticationController.class);

	AuthenticationController(EmployeeRepository employeeRepo, RoleRepository roleRepo) {
		this.employeeRepo = employeeRepo;
		this.roleRepo = roleRepo;
	}

	//SIGN-UP
	//REGISTER
	@PostMapping("/signup")
	public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		try {
			if (Boolean.TRUE.equals(employeeRepo.existsByUsername(signUpRequest.getUsername()))) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
			}

			if (Boolean.TRUE.equals(employeeRepo.existsByEmail(signUpRequest.getEmail()))) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
			}

			if (Boolean.TRUE.equals(employeeRepo.existsByEmpcode(signUpRequest.getEmpcode()))) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Employee Code is already in use!"));
			}
			// Create new Employee
			EmployeeModel employee = new EmployeeModel(signUpRequest.getEmp_name(), signUpRequest.getEmpcode(),
					signUpRequest.getUsername(), signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()),
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
						employee.setRole_name(adminRole.getRole_name().toString());
						break;

					case "practicehead":
						Role practiceheadRole = roleRepo.findByRoleName(ERole.PRACTICE_HEAD)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(practiceheadRole);
						employee.setRole_name(practiceheadRole.getRole_name().toString());
						break;

					case "manager":
						Role managerRole = roleRepo.findByRoleName(ERole.PROJECT_MANAGER)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(managerRole);
						employee.setRole_name(managerRole.getRole_name().toString());
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
			return ResponseHandler.generateResponse("Employee Registered Successfully!", true, HttpStatus.OK, null);

		} catch (Exception ex) {
			Logger.error("Could not Register Employee " + ex.getMessage());
			return ResponseHandler.generateResponse("Employee was not registered " + ex.getMessage(), false,
					HttpStatus.OK, null);
		}
	}
	
    //SIGN-IN
	//LOG-IN
	@PostMapping("/signin")
	public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
			HttpServletResponse response)
	{
		try
		{
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);

			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

			List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
					.collect(Collectors.toList());

			String jwt = jwtUtils.generateJwtToken(userDetails);

			RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

			ResponseCookie tokencookie = ResponseCookie.from("token", jwt).httpOnly(false).secure(true)
					.domain(env.getProperty("pl.cookies.allow.domain")).path("/").maxAge(7 * 24 * 60 * (long) 60)
					.build();

			ResponseCookie refreshtokencookie = ResponseCookie.from("refreshtoken", newRefreshToken.getToken())
					.httpOnly(false).secure(true).domain(env.getProperty("pl.cookies.allow.domain")).path("/")
					.maxAge(7 * 24 * 60 * (long) 60).build();

			response.addHeader("Set-Cookie", tokencookie.toString());
			response.addHeader("Set-Cookie", refreshtokencookie.toString());

			return ResponseHandler.generateResponse("Login Successfully", true, HttpStatus.OK,
					new JwtResponse(userDetails.getId(), userDetails.getEmp_name(), userDetails.getEmpcode(),
							userDetails.getUsername(), userDetails.getEmail(), userDetails.getDesignation(),
							userDetails.getAddress(), userDetails.getDepartment(), userDetails.getMobile_number(),
							roles));

		} catch (Exception ex) {
			Logger.error("Could not Login Employee " + ex.getMessage());
			return ResponseHandler.generateResponse("Employee was not logged in", false, HttpStatus.OK, null);
		}
	}

	//REFRESH-TOKEN GENERATION
	@PostMapping("/refreshtoken")
	public ResponseEntity<Object> refreshtoken(HttpServletRequest request, HttpServletResponse response) {
		try {

			String refreshToken = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("refreshtoken"))
					.findFirst().map(Cookie::getValue).orElse(null);

			return refreshTokenService.findByToken(refreshToken).map(refreshTokenService::verifyExpiration)
					.map(RefreshToken::getEmployee).map(user -> {
						String newToken = jwtUtils.generateTokenFromUsername(user.getUsername());

						ResponseCookie tokencookie = ResponseCookie.from("token", newToken).httpOnly(false).secure(true)
								.domain(env.getProperty("pl.cookies.allow.domain")).path("/")
								.maxAge(7 * 24 * 60 * (long) 60).build();

						ResponseCookie refreshtokencookie = ResponseCookie.from("refreshtoken", refreshToken)
								.httpOnly(false).secure(true).domain(env.getProperty("pl.cookies.allow.domain"))
								.path("/").maxAge(7 * 24 * 60 * (long) 60).build();

						// add cookie to response
						response.addHeader("Set-Cookie", tokencookie.toString());
						response.addHeader("Set-Cookie", refreshtokencookie.toString());

						return ResponseHandler.generateResponse("Token Refresh Successfully", true, HttpStatus.OK,
								new TokenRefreshResponse(token, refreshToken));
					}).orElseThrow(() -> new TokenRefreshException(refreshToken, "Refresh token is not in database!"));

		} catch (Exception e) {
			Logger.error("Internal Server Error while refreshtoken: {}.", e.getMessage());
			return ResponseHandler.generateResponse("Server Error, Please contact Admin", false, HttpStatus.OK, null);
		}
	}

	
	//LOG-OUT 
	//SIGN-OUT
	@GetMapping("/logout")
	public ResponseEntity<Object> logoutUser(HttpServletResponse response, HttpServletRequest request) {
		try {
			if (request.getCookies() != null) {
				token = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("refreshtoken")).findFirst()
						.map(Cookie::getValue).orElse(null);

				refreshTokenService.deletetoken(token);
			}
			ResponseCookie tokencookie = ResponseCookie.from("token", null).httpOnly(false).secure(true)
					.domain(env.getProperty("pl.cookies.allow.domain")).path("/").maxAge(0).build();

			ResponseCookie refreshtokencookie = ResponseCookie.from("refreshtoken", null).httpOnly(false).secure(true)
					.domain(env.getProperty("pl.cookies.allow.domain")).path("/").maxAge(0).build();

			response.addHeader("Set-Cookie", tokencookie.toString());
			response.addHeader("Set-Cookie", refreshtokencookie.toString());

			return ResponseHandler.generateResponse("Logout successful!", true, HttpStatus.OK, null);

		} catch (Exception ex) {

			Logger.error("While logout : {}.", ex.getMessage());
			ResponseCookie tokencookie = ResponseCookie.from("token", null).httpOnly(false).secure(true)
					.domain(env.getProperty("pl.cookies.allow.domain")).path("/").maxAge(0).build();

			ResponseCookie refreshtokencookie = ResponseCookie.from("refreshtoken", null).httpOnly(false).secure(true)
					.domain(env.getProperty("pl.cookies.allow.domain")).path("/").maxAge(0).build();
			response.addHeader("Set-Cookie", tokencookie.toString());
			response.addHeader("Set-Cookie", refreshtokencookie.toString());

			return ResponseHandler.generateResponse("Logout not successful!", true, HttpStatus.OK, null);
		}
	}

	//SENDING FORGROT PASSWORD MESSAGE THROUGH EMAIL
	@GetMapping("/forgotpassword")
	public ResponseEntity<Object> forgotPassword(@RequestParam("email") String email) {

		Optional<EmployeeModel> employeeOptional = Optional.ofNullable(employeeRepo.findByEmail(email));

		if (!employeeOptional.isPresent()) {
			return ResponseHandler.generateResponse("Invalid Email ID!", false, HttpStatus.OK, null);
		}

		EmployeeModel currentemployee = employeeOptional.get();
		String response = tokenService.forgotPassword(currentemployee);

		try {
			Map<String, Object> model = new HashMap<>();
			model.put("providerName", "Intelizign");
			model.put("recieverName", currentemployee.getUsername());
			model.put("passwordlink", env.getProperty("pl.frontend.app.domain") + "reset-password/" + response);
			emailService.sendForgetMail(email, model);

			return ResponseHandler.generateResponse("Reset Password link sent to registered email", true, HttpStatus.OK,
					null);

		} catch (Exception e) {
			Logger.error("Internal Server Error while forgetpassword:{}.", e.getMessage());
			return ResponseHandler.generateResponse("Server Error, Please contact Admin", false, HttpStatus.OK, null);
		}

	}

	//RESETTING PASSWORD USING TOKEN
	@GetMapping("/reset-password/{token}")
	public ResponseEntity<Object> resetPassword(@PathVariable String token, @RequestParam String password) {

		try {
			return tokenService.resetPassword(token, password);
		} catch (Exception e) {

			Logger.error("Internal Server Error while reset_password:{}.", e.getMessage());
			return ResponseHandler.generateResponse("Server Error, Please contact Admin", false, HttpStatus.OK, null);
		}

	}

	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	@PutMapping("/changepassword")
	public ResponseEntity<Object> formdataUpadate(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
			Authentication authentication) {
		try {
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			PasswordEncoder encoder = new BCryptPasswordEncoder();

			EmployeeModel employee = employeeRepo.findByEmail(userDetails.getEmail());
			String encodedPassword = employee.getPassword();

			if (encoder.matches(changePasswordRequest.getOldPassword(), encodedPassword)) {
				employee.setPassword(encoder.encode(changePasswordRequest.getNewPassword()));
				employeeRepo.save(employee);
				return ResponseHandler.generateResponse("Password Changed Successfully", true, HttpStatus.OK, null);
			}
			return ResponseHandler.generateResponse("Old Password Does not Match with Existing Password", true,
					HttpStatus.OK, null);

		} catch (Exception e) {

			Logger.error("Internal Server Error:" + e.getMessage());
			return ResponseHandler.generateResponse("Internal Server Error, Please contact Admin", false, HttpStatus.OK,
					null);
		}

	}

	//ROLE-INSERTION
	@PostMapping("/insertrole")
	public ResponseEntity<Object> insertAll() {
		try {
			List<Role> roledataList = roleRepo.findAll();

			if (roledataList.isEmpty()) {

				Role admin = new Role(1, ERole.ADMIN);
				Role practicehead = new Role(2, ERole.PRACTICE_HEAD);
				Role manager = new Role(3, ERole.PROJECT_MANAGER);
				roledataList.add(admin);
				roledataList.add(practicehead);
				roledataList.add(manager);

				roleRepo.saveAll(roledataList);

				return ResponseHandler.generateResponse("Role Added Successfully", true, HttpStatus.OK, null);

			} else {
				return ResponseHandler.generateResponse("Role Already Added", true, HttpStatus.OK, null);
			}
		} catch (Exception e) {
			Logger.error("Internal Server Error while insertrole:{}.", e.getMessage());
			return ResponseHandler.generateResponse("Server Error, Please contact Admin", false, HttpStatus.OK, null);
		}
	}

	//GETTING ALL AVAILABLE ROLES 
	@GetMapping("/getroles")
	public ResponseEntity<Object> getRoles() {
		try {

			List<Role> roles = roleRepo.findAll();
			return ResponseHandler.generateResponse("Role Data Retrieved Successfully", true, HttpStatus.OK, roles);

		} catch (Exception e) {
			Logger.error("Internal Server Error while getting role data : {}", e.getMessage());
			return ResponseHandler.generateResponse("Server Error, Please contact Admin", false, HttpStatus.OK, null);
		}
	}
}

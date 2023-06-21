package com.intelizign.pl.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.intelizign.pl.model.EmployeeModel;
import com.intelizign.pl.repositories.EmployeeRepository;
import com.intelizign.pl.response.ResponseHandler;

@Service
public class TokenGeneratorService {

	static final long EXPIRE_TOKEN_AFTER_MINUTES = 30;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	private EmployeeRepository employeeRepo;

	@Autowired
	private Environment env;

	public String forgotPassword(EmployeeModel employee) {

		employee.setResettoken(generateToken());
		employee.setToken_creation_date(LocalDateTime.now(ZoneId.of(env.getProperty("spring.app.timezone"))));

		employeeRepo.save(employee);

		return employee.getResettoken();
	}

	private String generateToken() {
		StringBuilder token = new StringBuilder();
		return token.append(UUID.randomUUID().toString()).append(UUID.randomUUID().toString()).toString();
	}

	/*
	 * Check whether token is expired
	 */
	private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {

		LocalDateTime now = LocalDateTime.now(ZoneId.of(env.getProperty("spring.app.timezone")));
		Duration diff = Duration.between(tokenCreationDate, now);

		return diff.toMinutes() >= EXPIRE_TOKEN_AFTER_MINUTES;
	}

	/*
	 * To reset password
	 */
	public ResponseEntity<Object> resetPassword(String token, String password) {

		Optional<EmployeeModel> empOptional = Optional.ofNullable(employeeRepo.findByResettoken(token));

		if (!empOptional.isPresent()) {
			return ResponseHandler.generateResponse("Invalid token", false, HttpStatus.BAD_REQUEST, null);
		}

//		If entered same password
//		if (empOptional.get().getPassword().equals(password)) {
//			return ResponseHandler.generateResponse("Try with a different password", false, HttpStatus.BAD_REQUEST, null);
//		}
		
		LocalDateTime tokenCreationDate = empOptional.get().getToken_creation_date();

		if (isTokenExpired(tokenCreationDate)) {
			return ResponseHandler.generateResponse("Token is expired.", false, HttpStatus.BAD_REQUEST, null);
		}

		EmployeeModel employee = empOptional.get();

		employee.setPassword(encoder.encode(password));
		employee.setResettoken(null);
		employee.setToken_creation_date(null);

		employeeRepo.save(employee);
		return ResponseHandler.generateResponse("Your password successfully updated", true, HttpStatus.OK, null);
	}

}

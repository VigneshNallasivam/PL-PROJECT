package com.intelizign.pl.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.intelizign.pl.exception.TokenRefreshException;
import com.intelizign.pl.model.RefreshToken;
import com.intelizign.pl.repositories.EmployeeRepository;
import com.intelizign.pl.repositories.RefreshTokenRepository;

@Service
public class RefreshTokenService {

	@Value("${intelizign.pl.jwtRefreshExpirationMs}")
	private Long refreshTokenDurationMs;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	EmployeeRepository employeeRepository;

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	/*
	 * Refresh Token creation with userId
	 */
	public RefreshToken createRefreshToken(Long userId) {
		RefreshToken refreshToken = new RefreshToken();

		refreshToken.setEmployee(employeeRepository.findById(userId).get());
		refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
		refreshToken.setToken(UUID.randomUUID().toString());

		refreshToken = refreshTokenRepository.save(refreshToken);
		return refreshToken;
	}

	/*
	 * Verify the expiration time of Refresh Token
	 */
	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			refreshTokenRepository.delete(token);
			throw new TokenRefreshException(token.getToken(),
					"Refresh token was expired. Please make a new signin request");
		}

		return token;
	}

	@Transactional
	public void deletetoken(String token) {
		System.out.println(token);
		refreshTokenRepository.deleteByToken(token);
	}

	@Transactional
	public int deleteByUserId(Long userId) {
		return refreshTokenRepository.deleteByEmployee(employeeRepository.findById(userId).get());
	}
}

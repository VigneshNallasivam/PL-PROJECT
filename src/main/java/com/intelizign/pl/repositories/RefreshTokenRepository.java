package com.intelizign.pl.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.intelizign.pl.model.EmployeeModel;
import com.intelizign.pl.model.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

	Optional<RefreshToken> findByToken(String token);

	void deleteByToken(String token);

	@Modifying
	int deleteByEmployee(EmployeeModel employeeModel);
	
}

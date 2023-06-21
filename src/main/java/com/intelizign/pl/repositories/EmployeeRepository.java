package com.intelizign.pl.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.intelizign.pl.model.EmployeeModel;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeModel, Long>{

	@Query("SELECT u FROM EmployeeModel u WHERE u.active = true AND u.email=?1")
	Optional<EmployeeModel> findUserByEmail(String email);
	
	@Query("SELECT u FROM EmployeeModel u WHERE u.active = true AND u.username=?1")
	Optional<EmployeeModel> findByUsername(String username);
	
	@Query("SELECT e FROM EmployeeModel e WHERE " 
			+ "LOWER(CONCAT(e.designation, e.emp_name, e.empcode, e.department, e.country)) LIKE %?1%  OR "
			+ "UPPER(CONCAT(e.designation, e.emp_name, e.empcode, e.department, e.country)) LIKE %?1%  AND "
			+ "e.active = true")
	Page<EmployeeModel> findAllEmployeesByPagination(String searchKeyword, Pageable pageable); 
	
	EmployeeModel findByResettoken(String resettoken);
	
	Boolean existsByUsername(String username);
	
	Boolean existsByEmail(String email);
	
	EmployeeModel findByEmail(String email);

	Boolean existsByEmpcode(String emp_code);
}

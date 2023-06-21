package com.intelizign.pl.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.intelizign.pl.model.ResourceModel;

@Repository
public interface ResourceRepository extends JpaRepository<ResourceModel, Long>{

	@Query("SELECT e FROM ResourceModel e WHERE " 
			+ "LOWER(CONCAT(e.designation, e.emp_name, e.empcode, e.department, e.country, e.gender, e.resource_role)) LIKE %?1%  OR "
			+ "UPPER(CONCAT(e.designation, e.emp_name, e.empcode, e.department, e.country, e.gender, e.resource_role)) LIKE %?1%  AND "
			+ "e.active = true")
	Page<ResourceModel> findAllResourcesByPagination(String searchKeyword, Pageable pageable); 
	
	Boolean existsByUsername(String username);
	
	Boolean existsByEmail(String email);
	
	Boolean existsByEmpcode(String empcode);

	List<ResourceModel> findAllByOrderById();
}

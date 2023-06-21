package com.intelizign.pl.repositories;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.intelizign.pl.model.ProjectModel;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectModel, Long>
{
	@Query("SELECT p FROM ProjectModel p WHERE "
			+ "LOWER(CONCAT(p.projectname,p.bit_type,p.manager_name,p.project_status)) LIKE %?1%  OR "
    		+ "UPPER(CONCAT(p.projectname,p.bit_type,p.manager_name,p.project_status)) LIKE %?1% AND "
    		+ "p.active = 'true'")
	Page<ProjectModel> findAllByPagination(Pageable pageable, String searchKeyword);
	
	Boolean existsByProjectname(String projectname);
	
	@Query("SELECT  p FROM ProjectModel p WHERE p.active = ?1")
	List<ProjectModel> findByActive(boolean active);
	
	@Query("SELECT  p FROM ProjectModel p WHERE p.active = ?1 AND p.end_date = ?2")
	List<ProjectModel> findByActiveAndEndDate(boolean active,LocalDate end_date);
	
	@Query("SELECT  p FROM ProjectModel p WHERE p.active = false")
	List<ProjectModel> findByFalse(boolean active);
}

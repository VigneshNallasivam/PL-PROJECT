package com.intelizign.pl.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.intelizign.pl.model.VerticalModel;

@Repository
public interface VerticalRepository extends JpaRepository<VerticalModel, Long>
{
	@Query("SELECT v FROM VerticalModel v WHERE "
			+ "LOWER(CONCAT(v.verticalname)) LIKE %?1% OR "
			+ "UPPER(CONCAT(v.verticalname)) LIKE %?1% AND "
			+ "v.active = 'true'")
	Page<VerticalModel> findAllByPagination(Pageable pageable, String searchKeyword);
	Boolean existsByVerticalname(String verticalname);
}

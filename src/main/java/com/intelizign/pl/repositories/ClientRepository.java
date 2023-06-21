package com.intelizign.pl.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.intelizign.pl.model.ClientModel;

@Repository
public interface ClientRepository extends JpaRepository<ClientModel, Long>
{
	@Query("SELECT c FROM ClientModel c WHERE "
    		+ "LOWER(CONCAT(c.clientname,c.mobile_number,c.country,c.address,c.email,c.active)) LIKE %?1%  OR "
    		+ "UPPER(CONCAT(c.clientname,c.mobile_number,c.country,c.address,c.email,c.active)) LIKE %?1% AND "
    		+ "c.active = 'true'")
	Page<ClientModel> findAllByPagination(Pageable pageable,String searchKeyword);
	
    Boolean existsByClientname(String clientname);
	
	Boolean existsByEmail(String email);

}

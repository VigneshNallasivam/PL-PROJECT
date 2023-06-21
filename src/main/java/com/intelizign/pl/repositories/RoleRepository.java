package com.intelizign.pl.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.intelizign.pl.model.ERole;
import com.intelizign.pl.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>{

	@Query("SELECT u FROM Role u WHERE u.role_name=?1")
	Optional<Role> findByRoleName(ERole role_name);
}

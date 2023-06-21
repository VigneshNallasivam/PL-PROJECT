package com.intelizign.pl.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.intelizign.pl.model.ResourceRole;

@Repository
public interface ResourceRoleRepository extends JpaRepository<ResourceRole, String>{

}

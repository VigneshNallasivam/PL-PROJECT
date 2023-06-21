package com.intelizign.pl.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.intelizign.pl.model.ResourceAllocation;

@Repository
public interface ResourceAllocationRepository extends JpaRepository<ResourceAllocation, Long>{

}

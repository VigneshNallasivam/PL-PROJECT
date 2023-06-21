package com.intelizign.pl.audit;

import org.hibernate.envers.RevisionEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.envers.DefaultRevisionEntity;

import javax.persistence.Entity;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@RevisionEntity(AuditListener.class)
public class AuditRevisionInfo  extends DefaultRevisionEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
}

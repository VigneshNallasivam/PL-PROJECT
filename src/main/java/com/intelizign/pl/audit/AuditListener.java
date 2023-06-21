package com.intelizign.pl.audit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditListener implements RevisionListener {

	public final Logger LOGGER = LogManager.getLogger(AuditListener.class);

	public void newRevision(Object revisionEntity) {
		
		try {
			AuditRevisionInfo exampleRevEntity = (AuditRevisionInfo) revisionEntity;
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			exampleRevEntity.setUsername(auth.getName());
		} catch (Exception e) {
			LOGGER.error("Audit GetName() Function Error: " + e.getMessage());
		}
	}
}

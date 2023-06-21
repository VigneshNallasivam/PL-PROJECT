package com.intelizign.pl.repositories;

import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.intelizign.pl.model.FileUploadModel;


public interface FileUploadRepository extends JpaRepository<FileUploadModel, Long>
{
	@Query("SELECT u FROM FileUploadModel u WHERE u.id = ?1")
	Optional<FileUploadModel> findById(Long id);

	@Modifying
	@Transactional
	void deleteAllByMapped(boolean b);
}

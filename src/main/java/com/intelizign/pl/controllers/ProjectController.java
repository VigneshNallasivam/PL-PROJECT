package com.intelizign.pl.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.intelizign.pl.authentication.UserDetailsImpl;
import com.intelizign.pl.model.ClientModel;
import com.intelizign.pl.model.FileUploadModel;
import com.intelizign.pl.model.ProjectModel;
import com.intelizign.pl.model.VerticalModel;
import com.intelizign.pl.repositories.ClientRepository;
import com.intelizign.pl.repositories.FileUploadRepository;
import com.intelizign.pl.repositories.ProjectRepository;
import com.intelizign.pl.repositories.VerticalRepository;
import com.intelizign.pl.request.SupportingFiles;
import com.intelizign.pl.response.ResponseHandler;


@RestController
@RequestMapping("/project")
public class ProjectController 
{
	Logger logger = LoggerFactory.getLogger(ProjectController.class);


	@Autowired
	private FileUploadRepository fileUploadRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private VerticalRepository verticalRepository;

	@Autowired
	private Environment environment;


    //JSON CREATION
	@GetMapping("/json")
	public ProjectModel getJSON(ProjectModel projectModel)
	{
		return projectModel;
	}

	//ADDING PROJECT DETAILS
	@PostMapping("/{clientid}/{verticalid}")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> createData(@Valid @RequestBody ProjectModel projectModel, @PathVariable Long clientid, @PathVariable Long verticalid,Authentication authentication)
	{
		try
		{
			UserDetailsImpl user = (UserDetailsImpl)authentication.getPrincipal();

			if (Boolean.TRUE.equals(projectRepository.existsByProjectname(projectModel.getProjectname()))) 
			{
				return ResponseHandler.generateResponse("Project-Name is already taken!",false,HttpStatus.OK,null);
			}
			
			//SETTING CLIENT DATA IN PROJECT
			Optional<ClientModel> clientData = clientRepository.findById(clientid);
			if(clientData.isPresent()) 
			{
				projectModel.setClient(clientData.get());
			}
			else
			{
				return ResponseHandler.generateResponse("Client Id Not Found",false,HttpStatus.OK,null);
			}
			
			//SETTING VERTICAL DATA IN PROJECT
			Optional<VerticalModel> verticalData = verticalRepository.findById(verticalid);
			if(verticalData.isPresent()) 
			{
				projectModel.setVertical(verticalData.get());				

			}
			else
			{
				return ResponseHandler.generateResponse("Vertical Id Not Found",false,HttpStatus.OK,null);
			}
			projectModel.setStart_date(projectModel.getStart_date());
			projectModel.setEnd_date(projectModel.getEnd_date());
			projectModel.setCreated_by(user.getUsername());
			projectModel.setCreated_on(LocalDateTime.now(ZoneId.of(environment.getProperty("spring.app.timezone"))));
			List<SupportingFiles> currentFile = projectModel.getSupportingFiles();
			
			if(!currentFile.isEmpty()) 
			{
				for(SupportingFiles fileData : currentFile)
				{
					if (Boolean.FALSE.equals(fileData.getMapped()))
					{

						Optional<FileUploadModel> fileuploadmodel = fileUploadRepository.findById(fileData.getId());
						if (fileuploadmodel.isPresent()) 
						{
							fileuploadmodel.get().setMapped(true);
							fileUploadRepository.save(fileuploadmodel.get());
						}
					}
				}

			}
			projectModel.setSupportingFiles(currentFile);
			ProjectModel projectModels = projectRepository.save(projectModel);
			return ResponseHandler.generateResponse("Project Details Added Successfully..!!",true,HttpStatus.OK, projectModels);
		}
		catch (Exception ex) 
		{
			logger.error("Error while Creating Project Details", ex.getMessage());
			return ResponseHandler.generateResponse("Internal Server Error, Please contact Admin",false,HttpStatus.OK,null);
		}
	}

	//EDITING PROJECT DETAILS
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> updateData(@Valid @PathVariable Long id,@RequestBody ProjectModel projectModel,Authentication authentication)
	{
		try
		{
			UserDetailsImpl user = (UserDetailsImpl)authentication.getPrincipal();	
		    if (Boolean.TRUE.equals(clientRepository.existsByClientname(projectModel.getProjectname()))) 
			{
				return ResponseHandler.generateResponse("Project-Name is already taken",false,HttpStatus.OK,null);
			}

			Optional<ProjectModel> projectModels = projectRepository.findById(id);
			if(projectModels.isPresent())
			{
				projectModels.get().setProjectname(projectModel.getProjectname());
				projectModels.get().setStart_date(projectModel.getStart_date());
				projectModels.get().setEnd_date(projectModel.getEnd_date());
				projectModels.get().setBudget(projectModel.getBudget());
				projectModels.get().setBit_type(projectModel.getBit_type());
				projectModels.get().setWorking_hours(projectModel.getWorking_hours());
				projectModels.get().setManager_name(projectModel.getManager_name());
				projectModels.get().setProject_status(projectModel.getProject_status());
				projectModels.get().setResource_id(projectModel.getResource_id());
				projectModels.get().setUpdated_on(LocalDateTime.now(ZoneId.of(environment.getProperty("spring.app.timezone"))));
				projectModels.get().setUpdated_by(user.getUsername());
				projectModels.get().setActive(projectModel.getActive());
				projectRepository.save(projectModels.get());
				return ResponseHandler.generateResponse("Project Data Updated Successfully",true,HttpStatus.OK,projectModels);
			}
			else
			{
				return ResponseHandler.generateResponse("Project Id Not Found",false,HttpStatus.OK,null);
			}

		}
		catch (Exception ex) 
		{
			logger.error("Error while Updating Project Details", ex.getMessage());
			return ResponseHandler.generateResponse("Internal Server Error, Please contact Admin",false,HttpStatus.OK,null);
		}

	}

	//FETCHING PROJECT DETAIL BY ID
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> getDataById(@PathVariable Long id)
	{
		try
		{
			Optional<ProjectModel> projectModels = projectRepository.findById(id);
			if(projectModels.isPresent())
			{
				return ResponseHandler.generateResponse("Project Data Retrived Successfully",true,HttpStatus.OK,projectModels);	
			}
			else
			{
				return ResponseHandler.generateResponse("Project Id Not found",false,HttpStatus.OK,null);	
			}

		}
		catch (Exception ex)
		{
			logger.error("Error while getting Project Details", ex.getMessage());
			return ResponseHandler.generateResponse("Internal Server Error, Please contact Admin",false,HttpStatus.OK,null);
		}
	}

	//FETCHING ALL PROJECT DETAILS
	@GetMapping("")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> getAllData(@PageableDefault(size = 10, page = 0, sort = "id", direction = Direction.ASC) Pageable pageable,
			@RequestParam(required = false) String searchKeyword)
	{
		try
		{
			Page<ProjectModel> clientList = projectRepository.findAllByPagination(pageable,searchKeyword);
			return ResponseHandler.generateResponse("All Project Data Retrived Successfully",true,HttpStatus.FOUND,clientList);
		}
		catch (Exception ex)
		{
			logger.error("Error while getting Project Details", ex.getMessage());
			return ResponseHandler.generateResponse("Internal Server Error, Please contact Admin",false,HttpStatus.OK,null);
		}
	}


	//HARD-DELETE
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> deleteByIdHard(@PathVariable Long id)
	{
		try
		{
			if(projectRepository.findById(id).isPresent())
			{
				projectRepository.deleteById(id);
				return ResponseHandler.generateResponse("Project Data Deleted Successfully",true,HttpStatus.OK,null);
			}
			else
			{
				return ResponseHandler.generateResponse("Project Id Not Found",false,HttpStatus.OK,null);
			}
		}
		catch(Exception ex)
		{
			logger.error("Error while Deleting Project Details", ex.getMessage());
			return ResponseHandler.generateResponse("Internal Server Error, Please contact Admin",false,HttpStatus.OK,null);
		}
	}

	//SOFT-DELETE
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> deleteByIdSoft(@PathVariable Long id)
	{
		try
		{
			Optional<ProjectModel> projectModel = projectRepository.findById(id);
			if(projectModel.isPresent()) 
			{
				projectModel.get().setActive(false);
				projectRepository.save(projectModel.get());
				return ResponseHandler.generateResponse("Project Data Deleted Successfully",true,HttpStatus.OK,null);
			}
			else
			{
				return ResponseHandler.generateResponse("Project ID Not Found",true,HttpStatus.OK,null);
			}

		}
		catch(Exception ex)
		{
			logger.error("Error while Deleting Project Details", ex.getMessage());
			return ResponseHandler.generateResponse("Internal Server Error, Please contact Admin",false,HttpStatus.OK,null);
		}
	}
}
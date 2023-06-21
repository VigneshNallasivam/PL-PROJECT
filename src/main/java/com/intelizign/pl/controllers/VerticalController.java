package com.intelizign.pl.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.MethodNotAllowedException;
import com.intelizign.pl.authentication.UserDetailsImpl;
import com.intelizign.pl.model.VerticalModel;
import com.intelizign.pl.repositories.VerticalRepository;
import com.intelizign.pl.response.ResponseHandler;

@RestController
@RequestMapping("/vertical")
public class VerticalController 
{
	Logger logger = LoggerFactory.getLogger(VerticalController.class);

	@Autowired
	private VerticalRepository verticalRepository;

	@Autowired
	Environment environment;

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) 
	{

		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
		.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
		return errors;
	}

	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler(value = MethodNotAllowedException.class)
	public ResponseEntity<Object> handleMethodNotAllowedExceptionException(MethodNotAllowedException ex) 
	{
		return ResponseHandler.generateResponse(ex.getMessage(), false, HttpStatus.METHOD_NOT_ALLOWED, null);
	}


	@ResponseBody
	@ExceptionHandler
	ResponseEntity<?> handleConflict(DataIntegrityViolationException ex) 
	{
		return ResponseHandler.generateResponse(ex.getRootCause().getMessage(), false, HttpStatus.CONFLICT, null);
	}


	//JSON CREATION
	@GetMapping("/json")
	public VerticalModel getJSON(VerticalModel verticalModel)
	{
		return verticalModel;
	}

	//ADDING VERTICAL DETAILS
	@PostMapping("")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> createData(@Valid @RequestBody VerticalModel verticalModel,Authentication authentication)
	{
		try
		{
			UserDetailsImpl user = (UserDetailsImpl)authentication.getPrincipal();
			if (Boolean.TRUE.equals(verticalRepository.existsByVerticalname(verticalModel.getVerticalname())))
			{
				return ResponseHandler.generateResponse("Vertical-Name is already taken!",false,HttpStatus.OK,null);
			}
			verticalModel.setCreated_by(user.getUsername());
			verticalModel.setCreated_on(LocalDateTime.now(ZoneId.of(environment.getProperty("spring.app.timezone"))));
			VerticalModel verticalModels = verticalRepository.save(verticalModel);
			return ResponseHandler.generateResponse("Vertical Data Added Successfully",true,HttpStatus.OK,verticalModels);
		}

		catch (Exception ex) 
		{
			logger.error("Error while Creating Vertical Details", ex.getMessage());
			return ResponseHandler.generateResponse("Internal Server Error, Please contact Admin",false,HttpStatus.OK,null);
		}
	}

	//EDITING VERTICAL DETAILS
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> updateData(@Valid @PathVariable Long id,@RequestBody VerticalModel verticalModel,Authentication authentication)
	{
		try
		{
			UserDetailsImpl user = (UserDetailsImpl)authentication.getPrincipal();
			Optional<VerticalModel> verticalModels = verticalRepository.findById(id);
			if(verticalModels.isPresent())
			{
				if (Boolean.TRUE.equals(verticalRepository.existsByVerticalname(verticalModel.getVerticalname())))
				{
					return ResponseHandler.generateResponse("Vertical-Name is already taken!",false,HttpStatus.OK,null);
				}
				verticalModels.get().setVerticalname(verticalModel.getVerticalname());
				verticalModels.get().setActive(verticalModel.getActive());
				verticalModels.get().setUpdated_on(LocalDateTime.now(ZoneId.of(environment.getProperty("spring.app.timezone"))));
				verticalModels.get().setUpdated_by(user.getUsername());
				verticalRepository.save(verticalModels.get());
				return ResponseHandler.generateResponse("Vertical Data Updated Successfully",true,HttpStatus.OK,verticalModels);
			}
			else
			{
				return ResponseHandler.generateResponse("Vertical Id Not Found",false,HttpStatus.OK,null);
			}

		}
		catch (Exception ex) 
		{
			logger.error("Error while Updating Vertical Details", ex.getMessage());
			return ResponseHandler.generateResponse("Internal Server Error, Please contact Admin",false,HttpStatus.OK,null);
		}

	}

	//FETCHING VERTICAL DETAIL BY ID
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> getDataById(@PathVariable Long id)
	{
		try
		{
			Optional<VerticalModel> verticalModels = verticalRepository.findById(id);
			if(verticalModels.isPresent())
			{
				return ResponseHandler.generateResponse("Vertical Data Retrived Successfully",true,HttpStatus.OK,verticalModels);	
			}
			else
			{
				return ResponseHandler.generateResponse("Vertical Id Not found",false,HttpStatus.OK,null);	
			}

		}
		catch (Exception ex)
		{
			logger.error("Error while getting Vertical Details", ex.getMessage());
			return ResponseHandler.generateResponse("Internal Server Error, Please contact Admin",false,HttpStatus.OK,null);
		}
	}

	//FETCHING ALL VERTICAL DETAILS
	@GetMapping("")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> getAllData(@PageableDefault(size = 10, page = 0, sort = "id", direction = Direction.DESC) Pageable pageable,
			@RequestParam(required = false) String searchKeyword)
	{
		try
		{
			Page<VerticalModel> verticalList = verticalRepository.findAllByPagination(pageable,searchKeyword);
			return ResponseHandler.generateResponse("Vertical Data Readed Successfully",true,HttpStatus.FOUND,verticalList);
		}
		catch (Exception ex)
		{
			logger.error("Error while getting Vertical Details", ex.getMessage());
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
			if(verticalRepository.findById(id).isPresent())
			{
				verticalRepository.deleteById(id);
				return ResponseHandler.generateResponse("Vertical Data Deleted Successfully",true,HttpStatus.OK,null);
			}
			else
			{
				return ResponseHandler.generateResponse("Vertical Id not found",false,HttpStatus.OK,null);
			}
		}
		catch(Exception ex)
		{
			logger.error("Error while Deleting Vertical Details", ex.getMessage());
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
			Optional<VerticalModel> verticalData = verticalRepository.findById(id);
			if(verticalData.isPresent()) 
			{
				verticalData.get().setActive(false);
				verticalRepository.save(verticalData.get());
				return ResponseHandler.generateResponse("Vertical Data Deleted Successfully",true,HttpStatus.OK,null);
			}
			else
			{
				return ResponseHandler.generateResponse("ID Not Found",true,HttpStatus.OK,null);
			}

		}
		catch(Exception ex)
		{
			logger.error("Error while Deleting Vertical Details", ex.getMessage());
			return ResponseHandler.generateResponse("Internal Server Error, Please contact Admin",false,HttpStatus.OK,null);
		}
	}

}

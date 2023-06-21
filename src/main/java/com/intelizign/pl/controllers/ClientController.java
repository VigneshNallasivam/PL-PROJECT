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
import com.intelizign.pl.model.ClientModel;
import com.intelizign.pl.repositories.ClientRepository;
import com.intelizign.pl.response.ResponseHandler;

@RestController
@RequestMapping("/client")
public class ClientController 
{
	Logger logger = LoggerFactory.getLogger(ClientController.class);

	@Autowired
	private ClientRepository clientRepository;
	
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
	public ClientModel getJSON(ClientModel clientModel)
	{
		return clientModel;
	}

	//ADDING CLIENT DETAILS
	@PostMapping("")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> createData(@Valid @RequestBody ClientModel clientModel,Authentication authentication)
	{
		try
		{
			UserDetailsImpl user = (UserDetailsImpl)authentication.getPrincipal();
			if (Boolean.TRUE.equals(clientRepository.existsByClientname(clientModel.getClientname()))) 
			{
				return ResponseHandler.generateResponse("Client-Name is already taken",false,HttpStatus.OK,null);
			}

			else if (Boolean.TRUE.equals(clientRepository.existsByEmail(clientModel.getEmail()))) 
			{
				return ResponseHandler.generateResponse("Email is already in use",false,HttpStatus.OK,null);
			}
			clientModel.setCreated_by(user.getUsername());
			clientModel.setCreated_on(LocalDateTime.now(ZoneId.of(environment.getProperty("spring.app.timezone"))));
			ClientModel clientModels = clientRepository.save(clientModel);
			return ResponseHandler.generateResponse("Client Details Added Successfully",true,HttpStatus.OK, clientModels);
		}

		catch (Exception ex) 
		{
			logger.error("Error while Creating Client Details", ex.getMessage());
			return ResponseHandler.generateResponse("Internal Server Error, Please contact Admin",false,HttpStatus.OK,null);
		}
	}

	//EDITING CLIENT DETAILS
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> updateData(@Valid @PathVariable Long id,@RequestBody ClientModel clientModel,Authentication authentication)
	{
		try
		{
		    UserDetailsImpl user = (UserDetailsImpl)authentication.getPrincipal();	
		    if (Boolean.TRUE.equals(clientRepository.existsByClientname(clientModel.getClientname()))) 
			{
				return ResponseHandler.generateResponse("Client-Name is already taken",false,HttpStatus.OK,null);
			}

			else if (Boolean.TRUE.equals(clientRepository.existsByEmail(clientModel.getEmail()))) 
			{
				return ResponseHandler.generateResponse("Email is already in use",false,HttpStatus.OK,null);
			}
			Optional<ClientModel> clientModels = clientRepository.findById(id);
			if(clientModels.isPresent())
			{
				clientModels.get().setClientname(clientModel.getClientname());
				clientModels.get().setCountry(clientModel.getCountry());
				clientModels.get().setAddress(clientModel.getAddress());
				clientModels.get().setEmail(clientModel.getEmail());
				clientModels.get().setMobile_number(clientModel.getMobile_number());
				clientModels.get().setUpdated_on(LocalDateTime.now(ZoneId.of(environment.getProperty("spring.app.timezone"))));
				clientModels.get().setUpdated_by(user.getUsername());
				clientRepository.save(clientModels.get());
				return ResponseHandler.generateResponse("Client Data Updated Successfully",true,HttpStatus.OK,clientModels);
			}
			else
			{
				return ResponseHandler.generateResponse("Client Id Not Found",false,HttpStatus.OK,null);
			}
			
		}
		catch (Exception ex) 
		{
			logger.error("Error while Updating Client Details", ex.getMessage());
			return ResponseHandler.generateResponse("Internal Server Error, Please contact Admin",false,HttpStatus.OK,null);
		}

	}
	
	//FETCHING CLIENT DETAIL BY ID
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> getDataById(@PathVariable Long id)
	{
		try
		{
			Optional<ClientModel> clientModels = clientRepository.findById(id);
			if(clientModels.isPresent())
			{
				return ResponseHandler.generateResponse("Client Data Retrived Successfully",true,HttpStatus.OK,clientModels);	
			}
			else
			{
				return ResponseHandler.generateResponse("Client Id Not found",false,HttpStatus.OK,null);	
			}
			
		}
		catch (Exception ex)
		{
			logger.error("Error while getting Client Details", ex.getMessage());
			return ResponseHandler.generateResponse("Internal Server Error, Please contact Admin",false,HttpStatus.OK,null);
		}
	}

	//FETCHING ALL CLIENT DETAILS
	@GetMapping("")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> getAllData(@PageableDefault(size = 10, page = 0, sort = "id", direction = Direction.ASC) Pageable pageable,
			@RequestParam(required = false) String searchKeyword)
	{
		try
		{
			Page<ClientModel> clientList = clientRepository.findAllByPagination(pageable,searchKeyword);
			return ResponseHandler.generateResponse("Client Data Retrived Successfully",true,HttpStatus.FOUND,clientList);
		}
		catch (Exception ex)
		{
			logger.error("Error while getting Client Details", ex.getMessage());
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
			if(clientRepository.findById(id).isPresent())
			{
				clientRepository.deleteById(id);
				return ResponseHandler.generateResponse("Client Data Deleted Successfully",true,HttpStatus.OK,null);
			}
			else
			{
				return ResponseHandler.generateResponse("Client ID not found",false,HttpStatus.OK,null);
			}
		}
		catch(Exception ex)
		{
			logger.error("Error while Deleting Client Details", ex.getMessage());
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
			Optional<ClientModel> client = clientRepository.findById(id);
			if(client.isPresent()) 
			{
				client.get().setActive(false);
				clientRepository.save(client.get());
				return ResponseHandler.generateResponse("Client Data Deleted Successfully",true,HttpStatus.OK,null);
			}
			else
			{
				return ResponseHandler.generateResponse("Client ID Not Found",true,HttpStatus.OK,null);
			}
			
		}
		catch(Exception ex)
		{
			logger.error("Error while Deleting Client Details", ex.getMessage());
			return ResponseHandler.generateResponse("Internal Server Error, Please contact Admin",false,HttpStatus.OK,null);
		}
	}
}


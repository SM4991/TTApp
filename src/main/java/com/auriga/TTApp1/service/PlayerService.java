package com.auriga.TTApp1.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import com.auriga.TTApp1.constants.GenderEnum;
import com.auriga.TTApp1.dto.PlayerDto;
import com.auriga.TTApp1.exception.ResourceBadRequestException;
import com.auriga.TTApp1.exception.ResourceNotFoundException;
import com.auriga.TTApp1.exception.UserAlreadyExistsException;
import com.auriga.TTApp1.model.Role;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.repository.RoleRepository;
import com.auriga.TTApp1.repository.UserRepository;
import com.auriga.TTApp1.service.PaginationService;
import com.auriga.TTApp1.util.FileUtil;

@Service
public class PlayerService {
	private static Validator validator;
	
	@Autowired
	private UserRepository repo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private PaginationService paginationService;

	@Autowired
	private CUserDetailsService cUserDetailsService;

	@Autowired
	private FileImportService fileImportService;
	
	public PlayerService() {
		super();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
	}

	public List<User> listAll() {
		return repo.findAllPlayer(getPlayerRole());
	}

	public PaginationService getAllItems(Integer page, Integer pageSize, String sortBy) {
		Pageable paging = paginationService.pagingData(page, pageSize, sortBy);

		Page<User> pagedResult = repo.findAllPlayer(paging, getPlayerRole());

		return paginationService.paginatedItems(pagedResult, page, pageSize, sortBy);
	}

	@Transactional
	public void save(PlayerDto playerForm) {
		// Let's check if user already registered with us
		if (cUserDetailsService.checkIfUserExist(playerForm.getEmail())) {
			throw new UserAlreadyExistsException("User already exists for this email");
		}
		Role role = getPlayerRole();

		User player = new User();
		BeanUtils.copyProperties(playerForm, player);

		player.setIsLoginActive(false);
		player.setRole(role);
		repo.save(player);
	}

	public User get(Long id) {
		return repo.findPlayerById(id, getPlayerRole()).orElseThrow(() -> new ResourceNotFoundException("Player"));
	}

	public User getById(Long id) {
		return repo.getPlayerById(id, getPlayerRole());
	}

	public void delete(User player) {
		repo.delete(player);
	}

	public List importPlayers(MultipartFile file) throws IOException {
    	String[] headers = {"name", "email", "gender", "age"};
    	List<Map<String, String>> records = fileImportService.csvToRecords(headers, file.getInputStream());
    	return records;
    }
	
	@Transactional
	public List saveBulkPlayers(List<Map<String, String>> records) {
		List response = new ArrayList();
		
		for(Map<String, String> record : records) {
			Map playerResponse = new HashMap();
			List all_errors = new ArrayList();
			
			PlayerDto player = new PlayerDto();
    		
    		if(!record.get("name").isEmpty()) {
    			player.setName(record.get("name"));
    		}
    		if(!record.get("email").isEmpty()) {
    			player.setEmail(record.get("email"));
    		}
    		if(!record.get("gender").isEmpty()) {
    			try{
    				player.setGender(GenderEnum.valueOf(record.get("gender")));
    			} catch(IllegalArgumentException ex) {
    				// all_errors.add("Gender is invalid");
    			}
    		}
    		if(!record.get("age").isEmpty()) {
    			try {
    				player.setAge(Integer.parseInt(record.get("age")));
    			} catch (NumberFormatException ex) {
    				// all_errors.add("Age is invalid");
    			} 			
    		}
			
			Set<ConstraintViolation<PlayerDto>> violations = validator.validate(player);
			if(violations.isEmpty()) {
				try {
					save(player);
					playerResponse.put("record", record);
					playerResponse.put("status", true);
					playerResponse.put("message", "Player added successfully.");
				} catch (UserAlreadyExistsException ex) {
					playerResponse.put("record", record);
					playerResponse.put("status", false);
					playerResponse.put("message", "Email already exists.");
				}
			} else {
				for(ConstraintViolation violation: violations) {
					if(violation.getPropertyPath().toString().equals("age")) {
						all_errors.add("Age is invalid");
					} else if(violation.getPropertyPath().toString().equals("gender")) {
						all_errors.add("Gender is invalid");
					} else {
						all_errors.add(violation.getMessage());
					}
				}
				playerResponse.put("record", record);
				playerResponse.put("status", false);
				playerResponse.put("message", String.join(", ", all_errors));
			}
			response.add(playerResponse);
		}
		return response;
	}

	public Role getPlayerRole() {
		Role role = roleRepo.findByName("PLAYER");
		if (role == null)
			throw new ResourceBadRequestException("Player role is missing, Please contact the administrator.");
		return role;
	}
}

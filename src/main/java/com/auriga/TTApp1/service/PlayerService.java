package com.auriga.TTApp1.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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

	private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

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

	public List<Map<String, String>> importPlayers(MultipartFile file) throws IOException {
		String[] headers = { "name", "email", "gender", "age" };
		List<Map<String, String>> records = fileImportService.csvToRecords(headers, file.getInputStream());
		return records;
	}

	@Transactional
	public List saveBulkPlayers(List<Map<String, String>> records) {
		List response = new ArrayList();

		for (Map<String, String> record : records) {
			Map playerResponse = new HashMap();
			List all_errors = new ArrayList();

			PlayerDto player = new PlayerDto();

			if (!record.get("name").isEmpty()) {
				player.setName(record.get("name"));
			}
			if (!record.get("email").isEmpty()) {
				player.setEmail(record.get("email"));
			}
			if (!record.get("gender").isEmpty()) {
				try {
					player.setGender(GenderEnum.valueOf(record.get("gender")));
				} catch (IllegalArgumentException ex) {
					// all_errors.add("Gender is invalid");
				}
			}
			if (!record.get("age").isEmpty()) {
				try {
					player.setAge(Integer.parseInt(record.get("age")));
				} catch (NumberFormatException ex) {
					// all_errors.add("Age is invalid");
				}
			}

			Set<ConstraintViolation<PlayerDto>> violations = validator.validate(player);
			if (violations.isEmpty()) {
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
				for (ConstraintViolation violation : violations) {
					if (violation.getPropertyPath().toString().equals("age")) {
						all_errors.add("Age is invalid");
					} else if (violation.getPropertyPath().toString().equals("gender")) {
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

	public List<User> saveAllBulkPlayers(List<Map<String, String>> records) {
		Role role = getPlayerRole();
		List response = new ArrayList();
		List<User> players = new ArrayList();

		for (Map<String, String> record : records) {
			Map playerResponse = new HashMap();
			List all_errors = new ArrayList();

			User player = new User();

			if (!record.get("name").isEmpty()) {
				player.setName(record.get("name"));
			}
			if (!record.get("email").isEmpty()) {
				player.setEmail(record.get("email"));
			}
			if (!record.get("gender").isEmpty()) {
				try {
					player.setGender(GenderEnum.valueOf(record.get("gender")));
				} catch (IllegalArgumentException ex) {
					// all_errors.add("Gender is invalid");
				}
			}
			if (!record.get("age").isEmpty()) {
				try {
					player.setAge(Integer.parseInt(record.get("age")));
				} catch (NumberFormatException ex) {
					// all_errors.add("Age is invalid");
				}
			}

			player.setRole(role);
			players.add(player);
		}
		repo.saveAll(players);
		return players;
	}

	@Async
	public CompletableFuture<List<User>> savePlayers(MultipartFile file) throws IOException {
		logger.info("Completable future");
		long start = System.currentTimeMillis();

		List<Map<String, String>> records = importPlayers(file);
		
		logger.info("saving list of players of size {} " + Thread.currentThread().getName(), records.size());
		List<User> players = saveAllBulkPlayers(records);

		long end = System.currentTimeMillis();
		logger.info("Total Time: {}", (end - start));
		return CompletableFuture.completedFuture(players);
	}

	public List savePlayers1(MultipartFile file) throws Exception {
		logger.info("Non Completable future");
		long start = System.currentTimeMillis();
		List<Map<String, String>> records = importPlayers(file);
		logger.info("saving list of players of size {}", records.size());
		List players = saveAllBulkPlayers(records);
		long end = System.currentTimeMillis();
		logger.info("Total Time: {}", (end - start));
		return players;
	}

	@Async
	public CompletableFuture<List<User>> findAllPlayers() {
		logger.info("Get list of users by " + Thread.currentThread().getName());
		List<User> users = repo.findAll();
		return CompletableFuture.completedFuture(users);
	}

	public Role getPlayerRole() {
		Role role = roleRepo.findByName("PLAYER");
		if (role == null)
			throw new ResourceBadRequestException("Player role is missing, Please contact the administrator.");
		return role;
	}
}

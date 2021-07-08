package com.auriga.TTApp1.controller.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.auriga.TTApp1.dto.TournamentDrawDto;
import com.auriga.TTApp1.dto.TournamentDrawDto2;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentImage;
import com.auriga.TTApp1.service.FileUploadService;
import com.auriga.TTApp1.service.PaginationService;
import com.auriga.TTApp1.service.TournamentService;

@RestController
public class TournamentRestController {
	@Autowired
	private TournamentService service;

	@Autowired
	private FileUploadService fileUploadService;

	@Value("${tournaments.image.upload.path}")
	String uploadPath;

	@Value("${spring.list.page.size}")
	Integer defaultPageSize;

	@RequestMapping(value = {"/admin/api/tournaments", "/api/tournaments"}, method = RequestMethod.GET)
	public ResponseEntity<Object> getTournaments(@RequestParam(defaultValue = "1") Integer status, @RequestParam(defaultValue = "1") Integer page) {
		PaginationService paginatedItems = service.getAllItems(status, page, defaultPageSize, "name");
		return new ResponseEntity<>(paginatedItems, HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/api/tournaments", method = RequestMethod.POST)
	public ResponseEntity<Object> createTournament(@Valid @RequestBody Tournament tournament,
			BindingResult errorResult) {
		if (errorResult.hasErrors()) {
			List<FieldError> errors = errorResult.getFieldErrors();
			HashMap<String, String> errorMsgs = new HashMap<String, String>();
			for (FieldError e : errors) {
				if (!errorMsgs.containsKey(e.getField())) {
					errorMsgs.put(e.getField(), e.getDefaultMessage());
				}
			}
			return new ResponseEntity<>(errorMsgs, HttpStatus.BAD_REQUEST);
		} else {
			service.save(tournament);
			return new ResponseEntity<>("Tournament is added successfully", HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/admin/api/tournaments/draw", method = RequestMethod.POST)
	public ResponseEntity<Object> createDraw(@Valid @RequestBody TournamentDrawDto dto, BindingResult errorResult) {
		if(errorResult.hasErrors()) {
			List<FieldError> errors = errorResult.getFieldErrors();
			HashMap<String, String> errorMsgs = new HashMap<String, String>();
	        for (FieldError e : errors){
	        	if(!errorMsgs.containsKey(e.getField())) {
	        		errorMsgs.put(e.getField(), e.getDefaultMessage());
	        	}
	        }
			return new ResponseEntity<>(errorMsgs, HttpStatus.BAD_REQUEST);
		} else {
			service.createDrawRounds(dto);
//			return new ResponseEntity<>("Tournament is added successfully", HttpStatus.OK);
		}
		return new ResponseEntity<>("testing", HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/admin/api/tournaments/upload", method = RequestMethod.POST)
	public ResponseEntity<Object> uploadTournamentImage(@ModelAttribute TournamentImage tournamentImage) {
		String image = null;
		try {
			MultipartFile file = tournamentImage.getFile();
			image = fileUploadService.saveUploadedFile(tournamentImage.getFile(), uploadPath);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(image, HttpStatus.OK);
	}
}

package com.auriga.TTApp1.controller.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.auriga.TTApp1.dto.PlayerDto;
import com.auriga.TTApp1.dto.UserImageDto;
import com.auriga.TTApp1.exception.ResourceBadRequestException;
import com.auriga.TTApp1.exception.ResourceNotFoundException;
import com.auriga.TTApp1.exception.UserAlreadyExistsException;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.service.FileImportService;
import com.auriga.TTApp1.service.FileUploadService;
import com.auriga.TTApp1.service.PaginationService;
import com.auriga.TTApp1.service.PlayerService;
import com.auriga.TTApp1.util.FileUtil;
import org.springframework.http.MediaType;

import javassist.NotFoundException;

@RestController
public class PlayerRestController {
	
	@Autowired
    private PlayerService service;
	
	@Autowired
    private FileUploadService fileUploadService;
	
	@Value("${spring.list.page.size}")
    Integer defaultPageSize;

	@GetMapping("/admin/api/players")
	public ModelAndView getPlayers(@RequestParam(defaultValue = "1") Integer page) {
		PaginationService paginatedItems = service.getAllItems(page, defaultPageSize, "name");
		
		ModelAndView model = new ModelAndView("/admin/players/playerList");
		model.addObject("paginatedItems", paginatedItems);
		
		return model;
	}

	
	@PostMapping("/admin/api/players")
	public ResponseEntity<Object> createPlayer(@Valid @RequestBody PlayerDto playerForm, BindingResult errorResult) {
		HashMap<String, String> errorMsgs = new HashMap<String, String>();
		if(errorResult.hasErrors()) {
			List<FieldError> errors = errorResult.getFieldErrors();
			
	        for (FieldError e : errors){
	        	if(!errorMsgs.containsKey(e.getField())) {
	        		errorMsgs.put(e.getField(), e.getDefaultMessage());
	        	}
	        }
	        return new ResponseEntity<>(errorMsgs, HttpStatus.BAD_REQUEST);
		} else {
			try {
				service.save(playerForm);
				return new ResponseEntity<>("Player added successfully", HttpStatus.OK);
	        } catch (UserAlreadyExistsException e){
	        	errorMsgs.put("email", "An account already exists for this email.");
	            return new ResponseEntity<>(errorMsgs, HttpStatus.BAD_REQUEST);
	        }
		}
	}
	
//	@PostMapping("/admin/api/player/{id}/edit")
//	public ResponseEntity<Object> editPlayer(@PathVariable("id") Long id, @Valid @RequestBody PlayerDto playerForm, BindingResult errorResult) {
//		HashMap<String, String> errorMsgs = new HashMap<String, String>();
//		if(errorResult.hasErrors()) {
//			List<FieldError> errors = errorResult.getFieldErrors();
//			
//	        for (FieldError e : errors){
//	        	if(!errorMsgs.containsKey(e.getField())) {
//	        		errorMsgs.put(e.getField(), e.getDefaultMessage());
//	        	}
//	        }
//	        return new ResponseEntity<>(errorMsgs, HttpStatus.BAD_REQUEST);
//		} else {
//			try {
//				service.save(playerForm);
//				return new ResponseEntity<>("Player added successfully", HttpStatus.OK);
//	        } catch (UserAlreadyExistsException e){
//	        	errorMsgs.put("email", "An account already exists for this email.");
//	            return new ResponseEntity<>(errorMsgs, HttpStatus.BAD_REQUEST);
//	        }
//		}
//	}
	
	@GetMapping("/admin/api/players/{id}")
	public ResponseEntity<Object> viewPlayer(@PathVariable("id") Long id){
		User player = service.get(id);
		
		return new ResponseEntity<>(player, HttpStatus.OK);
	}
	
	@PostMapping("/admin/api/players/import")
	public ModelAndView importPlayers(MultipartFile file) throws Exception{
		if(!FileUtil.hasCSVFormat(file)) throw new ResourceBadRequestException("Uploaded file should be in csv or excel format");
		
		List importResponse = service.saveBulkPlayers(service.importPlayers(file));
		
		ModelAndView model = new ModelAndView("/admin/players/importResponse");
		model.addObject("importResponse", importResponse);
		
		return model;
	}
	
	@PostMapping("/admin/api/players/importAsync")
	public @ResponseBody ResponseEntity importAsyncPlayers(@RequestParam("files") MultipartFile[] files) throws IOException{
		for(MultipartFile file: files) {
			CompletableFuture<List<User>> players = service.savePlayers(file);
			System.out.println(players);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@GetMapping("/admin/api/players/asyncList")
	public CompletableFuture<ResponseEntity> findAllPlayers(){
		CompletableFuture<List<User>> cars1=service.findAllPlayers();
        CompletableFuture<List<User>> cars2=service.findAllPlayers();
        CompletableFuture<List<User>> cars3=service.findAllPlayers();

        CompletableFuture.allOf(cars1, cars2, cars3).join();
		return service.findAllPlayers().thenApply(ResponseEntity::ok);
	}
	
	@PostMapping("/admin/api/players/upload")
	public ResponseEntity<Object> uploadImage(@ModelAttribute UserImageDto playerImage) {
        String image = null;
        try {
        	image = fileUploadService.saveUploadedFile(playerImage.getFile(), playerImage.getFilesUploadPath());
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
 
        return new ResponseEntity<>(image, HttpStatus.OK);
	}
}

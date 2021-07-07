package com.auriga.TTApp1.controller.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.auriga.TTApp1.model.Player;
import com.auriga.TTApp1.model.PlayerImage;
import com.auriga.TTApp1.repository.PlayerRepository;
import com.auriga.TTApp1.service.FileUploadService;
import com.auriga.TTApp1.service.PaginationService;
import com.auriga.TTApp1.service.PlayerService;

import javassist.NotFoundException;

@RestController
public class PlayerRestController {
	
	@Autowired
    private PlayerService service;
	
	@Autowired
    private FileUploadService fileUploadService;
	
	@Value("${players.image.upload.path}")
    String uploadPath;
	
	@Value("${spring.list.page.size}")
    Integer defaultPageSize;

	@RequestMapping(value = "/admin/api/players", method = RequestMethod.GET)
	public ResponseEntity<Object> getPlayers(@RequestParam(defaultValue = "1") Integer page) {
		PaginationService paginatedItems = service.getAllItems(page, defaultPageSize, "name");
		return new ResponseEntity<>(paginatedItems, HttpStatus.OK);
	}

	
	@RequestMapping(value = "/admin/api/players", method = RequestMethod.POST)
	public ResponseEntity<Object> createPlayer(@Valid @RequestBody Player player, BindingResult errorResult) {
		if(errorResult.hasErrors()) {
			List<FieldError> errors = errorResult.getFieldErrors();
			HashMap<String, String> errorMsgs = new HashMap<String, String>();
	        for (FieldError e : errors){
	        	if(!errorMsgs.containsKey(e.getField())) {
	        		errorMsgs.put(e.getField(), e.getField() + " " + e.getDefaultMessage());
	        	}
	        }
			return new ResponseEntity<>(errorMsgs, HttpStatus.BAD_REQUEST);
		} else {
			service.save(player);
			return new ResponseEntity<>("Product is created successfully", HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/admin/api/players/upload", method = RequestMethod.POST)
	public ResponseEntity<Object> uploadImage(@ModelAttribute PlayerImage playerImage) {
        String image = null;
        try {
        	MultipartFile file = playerImage.getFile();
        	image = fileUploadService.saveUploadedFile(playerImage.getFile(), uploadPath);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
 
        return new ResponseEntity<>(image, HttpStatus.OK);
	}
}

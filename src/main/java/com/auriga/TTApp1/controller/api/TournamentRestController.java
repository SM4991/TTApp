package com.auriga.TTApp1.controller.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.auriga.TTApp1.dto.TournamentDrawDto;
import com.auriga.TTApp1.dto.TournamentDto;
import com.auriga.TTApp1.dto.TournamentImageDto;
import com.auriga.TTApp1.exception.ResourceNotFoundException;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentMatch;
import com.auriga.TTApp1.model.TournamentRound;
import com.auriga.TTApp1.model.TournamentType;
import com.auriga.TTApp1.repository.TournamentMatchRepository;
import com.auriga.TTApp1.repository.TournamentRoundRepository;
import com.auriga.TTApp1.service.FileUploadService;
import com.auriga.TTApp1.service.TournamentTypeService;
import com.auriga.TTApp1.util.FileUtil;
import com.auriga.TTApp1.service.PaginationService;
import com.auriga.TTApp1.service.TournamentService;

@RestController
public class TournamentRestController {
	@Autowired
	private TournamentService service;
	
	@Autowired
	private TournamentTypeService tournamentTypeService;
	
	@Autowired
	private TournamentRoundRepository roundRepo;
	
	@Autowired
	private TournamentMatchRepository matchRepo;

	@Autowired
	private FileUploadService fileUploadService;

	@Value("${spring.list.page.size}")
	Integer defaultPageSize;

	@GetMapping(value = {"/admin/api/tournaments", "/api/tournaments"})
	public ModelAndView getTournaments(@RequestParam(defaultValue = "1") Integer status, @RequestParam(defaultValue = "1") Integer page) {
		PaginationService paginatedItems = service.getAllItems(status, page, defaultPageSize, "name");
		
		ModelAndView model = new ModelAndView("/admin/tournaments/tournamentList");
		model.addObject("paginatedItems", paginatedItems);
		
		return model;
	}

	@PostMapping(value = "/admin/api/tournaments")
	public ResponseEntity<Object> createTournament(@Valid @RequestBody TournamentDto tournamentDto,
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
			service.save(tournamentDto);
			return new ResponseEntity<>("Tournament is added successfully", HttpStatus.OK);
		}
	}

	@PostMapping(value = "/admin/api/tournaments/draw")
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
			return new ResponseEntity<>("Tournament Draw is generated successfully", HttpStatus.OK);
		}
	}
	
	@GetMapping(value = {"/admin/api/tournaments/{ttId}/fixture", "/api/tournaments/{ttId}/fixture"})
	public ModelAndView viewFixture(@PathVariable Long ttId) {
		TournamentType tournamentType = tournamentTypeService.get(ttId);
		Tournament tournament = tournamentType.getTournament();
		
		List<TournamentRound> rounds = roundRepo.findByTournamentType(tournamentType);
		Map<Long, List<TournamentMatch>> matchList = new HashMap();
		Map<Long, TournamentRound> roundList = new HashMap();
		
		rounds.forEach(round -> {
			List<TournamentMatch> r_matches = matchRepo.findAllByTournamentRound(round);
			
			List<TournamentMatch> nmatches = new ArrayList();
			
			Integer count = 1;
			Integer counter = 1;
			for(TournamentMatch match : r_matches) {
				String cls = "";
				if(counter%2 == 1) {
					cls = "match-block-1";
					counter++;
				} else {
					cls = "match-block-2";
					counter = 1;
				}
				if(count%2 > 0 && count == r_matches.size()) {
					cls += " match-block-common";
				}
				match.setFixtureClass(cls);
				nmatches.add(match);
				
				count++;
			}
			
			matchList.put(round.getId(), nmatches);
			roundList.put(round.getId(), round);
		});
		
		Map<Long, List<TournamentMatch>> sortedMatchList = new TreeMap<Long, List<TournamentMatch>>(matchList);
		
		ModelAndView model = new ModelAndView("/admin/tournaments/fixtureBracket");
		model.addObject("tournament", tournament);
		model.addObject("tournamentType", tournamentType);
		model.addObject("rounds", roundList);
		model.addObject("matches", sortedMatchList);
		model.addObject("userDefaultImage", FileUtil.getUserDefaultImage());
		
		return model;
	}
	
	@GetMapping(value = {"/admin/api/tournaments/{ttId}/matches", "/api/tournaments/{ttId}/matches"})
	public ModelAndView viewMatches(@PathVariable Long ttId) {
		TournamentType tournamentType = tournamentTypeService.get(ttId);
		Tournament tournament = tournamentType.getTournament();
		
		List<TournamentMatch> matches = matchRepo.findAllActiveTournamentMatch(tournamentType);
		
		ModelAndView model = new ModelAndView("/admin/tournaments/matchList");
		model.addObject("matches", matches);
		model.addObject("userDefaultImage", FileUtil.getUserDefaultImage());
		
		return model;
	}

	@PostMapping(value = "/admin/api/tournaments/upload")
	public ResponseEntity<Object> uploadTournamentImage(@ModelAttribute TournamentImageDto tournamentImage) {
		String image = null;
		try {
			image = fileUploadService.saveUploadedFile(tournamentImage.getFile(), tournamentImage.getFilesUploadPath());
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(image, HttpStatus.OK);
	}
}

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.auriga.TTApp1.dto.TournamentDrawDto;
import com.auriga.TTApp1.dto.TournamentDto;
import com.auriga.TTApp1.dto.TournamentImageDto;
import com.auriga.TTApp1.exception.ResourceNotFoundException;
import com.auriga.TTApp1.model.MatchType;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentMatch;
import com.auriga.TTApp1.model.TournamentRound;
import com.auriga.TTApp1.repository.TournamentMatchRepository;
import com.auriga.TTApp1.repository.TournamentRoundRepository;
import com.auriga.TTApp1.service.FileUploadService;
import com.auriga.TTApp1.service.MatchTypeService;
import com.auriga.TTApp1.service.PaginationService;
import com.auriga.TTApp1.service.TournamentService;

@RestController
public class TournamentRestController {
	@Autowired
	private TournamentService service;
	
	@Autowired
	private MatchTypeService matchTypeService;
	
	@Autowired
	private TournamentRoundRepository roundRepo;
	
	@Autowired
	private TournamentMatchRepository matchRepo;

	@Autowired
	private FileUploadService fileUploadService;

	@Value("${tournaments.image.upload.path}")
	String uploadPath;

	@Value("${spring.list.page.size}")
	Integer defaultPageSize;

	@RequestMapping(value = {"/admin/api/tournaments", "/api/tournaments"}, method = RequestMethod.GET)
	public ModelAndView getTournaments(@RequestParam(defaultValue = "1") Integer status, @RequestParam(defaultValue = "1") Integer page) {
		PaginationService paginatedItems = service.getAllItems(status, page, defaultPageSize, "name");
		
		ModelAndView model = new ModelAndView("/admin/tournaments/partialList");
		model.addObject("paginatedItems", paginatedItems);
		
		return model;
		
//		return new ResponseEntity<>(paginatedItems, HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/api/tournaments", method = RequestMethod.POST)
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
			return new ResponseEntity<>("Tournament Draw is generated successfully", HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/admin/api/tournaments/{id}/{mtId}/fixture", method = RequestMethod.GET)
	public ModelAndView viewFixture(@PathVariable("id") Long id, @PathVariable Long mtId) {
		Tournament tournament = service.get(id).orElseThrow(() -> new ResourceNotFoundException("Tournament"));
		MatchType matchType = matchTypeService.get(mtId).orElseThrow(() -> new ResourceNotFoundException("Match Type"));
		
		List<TournamentRound> rounds = roundRepo.findByTournamentAndMatchType(tournament, matchType);
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
		model.addObject("rounds", roundList);
		model.addObject("matches", sortedMatchList);
		
		return model;
	}
	
	@RequestMapping(value = {"/admin/api/tournaments/{id}/{mtId}/matches", "/api/tournaments/{id}/{mtId}/matches"}, method = RequestMethod.GET)
	public ResponseEntity<Object> viewMatches(@PathVariable("id") Long id, @PathVariable Long mtId) {
		Tournament tournament = service.get(id).orElseThrow(() -> new ResourceNotFoundException("Tournament"));
		MatchType matchType = matchTypeService.get(mtId).orElseThrow(() -> new ResourceNotFoundException("Match Type"));
		
		List<TournamentMatch> matches = matchRepo.findAllActiveTournamentMatch(tournament, matchType);
		
		ModelAndView model = new ModelAndView("/admin/tournaments/matches");
		model.addObject("matches", matches);
		return new ResponseEntity<>(matches, HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/api/tournaments/upload", method = RequestMethod.POST)
	public ResponseEntity<Object> uploadTournamentImage(@ModelAttribute TournamentImageDto tournamentImage) {
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

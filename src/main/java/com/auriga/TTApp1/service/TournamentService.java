package com.auriga.TTApp1.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auriga.TTApp1.constants.MatchStatusEnum;
import com.auriga.TTApp1.constants.TournamentTypeEnum;
import com.auriga.TTApp1.constants.RoundTypeEnum;
import com.auriga.TTApp1.dto.TournamentDrawDto;
import com.auriga.TTApp1.dto.TournamentDto;
import com.auriga.TTApp1.exception.ResourceAlreadyExistsException;
import com.auriga.TTApp1.exception.ResourceNotFoundException;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentMatch;
import com.auriga.TTApp1.model.TournamentRound;
import com.auriga.TTApp1.model.TournamentType;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.repository.TournamentMatchRepository;
import com.auriga.TTApp1.repository.TournamentTypeRepository;
import com.auriga.TTApp1.repository.TournamentRepository;
import com.auriga.TTApp1.repository.TournamentRoundRepository;

@Service
public class TournamentService {
	@Autowired
	private TournamentRepository repo;
	
	@Autowired
	private TournamentTypeRepository tournamentTypeRepo;
	
	@Autowired
	private TournamentRoundRepository roundRepo;
	
	@Autowired
	private TournamentMatchRepository matchRepo;

	@Autowired
	private PlayerService playerService;
	
	@Autowired
	private TournamentTypeService tournamentTypeService;
	
	@Autowired
	private TournamentMatchSetService matchSetService;

	@Autowired
	private PaginationService paginationService;

	public List<Tournament> listAll() {
		return repo.findAll();
	}

	public PaginationService getAllItems(Integer status, Integer page, Integer pageSize, String sortBy) {
		Pageable paging = paginationService.pagingData(page, pageSize, sortBy);

		Page<Tournament> pagedResult;
		if(status == 2) {
			pagedResult = repo.findAllUpcoming(paging);
		} else if(status == 3) {
			pagedResult = repo.findAllPrevious(paging);
		} else {
			pagedResult = repo.findAllOngoing(paging);
		}

		return paginationService.paginatedItems(pagedResult, page, pageSize, sortBy);
	}

	@Transactional
	public void save(TournamentDto tournamentDto) {
		Tournament tournament = new Tournament(); 
    	BeanUtils.copyProperties(tournamentDto, tournament);
		
		repo.save(tournament);
		
		List<TournamentTypeEnum> tournamentTypes = new ArrayList();
		tournamentDto.getTypes().forEach(item -> {
			TournamentType tournamentType = new TournamentType();
			tournamentType.setType(item);
			tournamentType.setTournament(tournament);
			tournamentTypeRepo.save(tournamentType);
		});
	}

	public Tournament get(Long id) {
		return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tournament"));
	}

	public void delete(Tournament tournament) {
		repo.delete(tournament);
	}
	
	public void createDrawRounds(TournamentDrawDto dto) {
		TournamentType tournamentType = tournamentTypeService.getByTournamentId(dto.getTournamentTypeId(), dto.getTournamentId());
		Tournament tournament = tournamentType.getTournament();
		if(roundRepo.countByTournamentType(tournamentType) > 0) {
			throw new ResourceAlreadyExistsException("Draw");
		}
		
		List<User> players = new ArrayList<User>();
		dto.getPlayerIds().forEach(id -> {
			User player = playerService.getById(id);
			if (player != null) {
				players.add(player);
			}
		});
		
		Integer participantCount = players.size();
		
		Map<Integer, Integer> roundMatchCount = getRoundMatchCount(participantCount);
		
		Integer totalRound = roundMatchCount.size();
		
		AtomicReference<TournamentRound> firstRound = new AtomicReference<>();
		/* Save Round Entries */
		roundMatchCount.forEach((order, matchCount) -> {
			TournamentRound round = new TournamentRound();
			round.setTournamentType(tournamentType);
			round.setOrder(order);
			round.setName(getRoundName(order, totalRound));
			round.setType(getRoundType(order, totalRound));
			
			roundRepo.save(round);
			
			createRoundMatches(round, players, matchCount);
			
			if(order == 1) {
				firstRound.set(round);
			}
		});
		
		/* Find match with bye given for first round, and move them to next round */
		List<TournamentMatch> matches = matchRepo.findAllByTournamentRoundAndByeGiven(firstRound.get(), true);
		if(matches.size() > 0) {
			matches.forEach(match -> {
				matchSetService.setNextRoundPlayer(tournament, tournamentType, firstRound.get(), match, match.getPlayer1());
			});
		}
	}
	
	public void createRoundMatches(TournamentRound round, List<User> players, Integer matchCount) {
		if(round.getOrder() == 1) {
			createPlayerMatches(round, players, matchCount);
		} else {
			createNonPlayerMatches(round, matchCount);
		}
	}
	
	public void createPlayerMatches(TournamentRound round, List<User> players, Integer matchCount) {
		Integer participantCount = players.size();
		
		/* Randomize players */
		Boolean randomize = true;
		if (randomize) {
			Long seed = System.nanoTime();
			Collections.shuffle(players, new Random(seed));
		}
		
		/* Add seeds to players */
		Integer i = 0; 
		for(User player: players) {
			i++;
			player.setSeed(i);
		}
		
		/* Get player1 and player2 for round and save match entries */
		List<Integer> range = IntStream.rangeClosed(1, matchCount).boxed().collect(Collectors.toList());
		Long seed = System.nanoTime();
		Collections.shuffle(range, new Random(seed));
		Integer counter = 1;
		for(Integer index : range) {
			Integer index2 = (participantCount-1)-index;
			
			TournamentMatch tournamentMatch = new TournamentMatch();
			
			User player1 = players.get(index);
			User player2 = players.get(index2);
			
			tournamentMatch.setTournamentRound(round);
			tournamentMatch.setName("Match "+counter);
			tournamentMatch.setOrder(counter);
			tournamentMatch.setPlayer1(player1);
			
			//give bye & save related data, else save other data
			if(player1.getId() == player2.getId()) {
				tournamentMatch.setPlayer2(null);
				tournamentMatch.setByeGiven(true);
				tournamentMatch.setWinner(player1);
				tournamentMatch.setStatus(MatchStatusEnum.COMPLETE);
			} else {
				tournamentMatch.setPlayer2(player2);
				tournamentMatch.setStatus(MatchStatusEnum.PENDING);
			}
			matchRepo.save(tournamentMatch);
			
			counter++;
		}
	}
	
	public void createNonPlayerMatches(TournamentRound round, Integer matchCount) {
		/* Save inactive match entries  */
		IntStream.rangeClosed(1, matchCount).forEach(index -> {
			TournamentMatch tournamentMatch = new TournamentMatch();
			tournamentMatch.setTournamentRound(round);
			tournamentMatch.setName("Match "+index);
			tournamentMatch.setOrder(index);
			matchRepo.save(tournamentMatch);
		});
	}
	
	public Map<Integer, Integer> getRoundMatchCount(Integer participantCount) {
		Integer matchCount = (int) Math.ceil(participantCount/2);
		matchCount = matchCount*2 < participantCount ? (matchCount+1) : matchCount;
		
		Map<Integer, Integer> list = new HashMap<Integer, Integer>();
		Integer round = 1;
		while(matchCount > 0) {
			list.put(round, matchCount);
			
			if(matchCount > 1) {
				matchCount = (matchCount/2)+(matchCount%2);
				round++;
			} else {
				break;
			}
		}
		return list;
	}
	
	public RoundTypeEnum getRoundType(Integer order, Integer roundCount) {
		RoundTypeEnum type = null;
		if(order <= (roundCount-3)) {
			type = RoundTypeEnum.PRE;
		} else if (order == roundCount) {
			type = RoundTypeEnum.FINAL;
		} else if (order == (roundCount-1)) {
			type = RoundTypeEnum.SEMIFINAL;
		} else if (order == (roundCount-2)) {
			type = RoundTypeEnum.QUATERFINAL;
		} 
		
		return type;
	}
	
	public String getRoundName(Integer order, Integer roundCount) {
		String name = "";
		if(order <= (roundCount-3)) {
			name = "Round "+order;
		} else if (order == roundCount) {
			name = "Final";
		} else if (order == (roundCount-1)) {
			name = "Semi-Final";
		} else if (order == (roundCount-2)) {
			name = "Quater Final";
		} 
		
		return name;
	}
	
	
}

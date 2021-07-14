package com.auriga.TTApp1.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.auriga.TTApp1.constants.MatchStatusEnum;
import com.auriga.TTApp1.constants.RoundTypeEnum;
import com.auriga.TTApp1.dto.TournamentDrawDto;
import com.auriga.TTApp1.exception.ResourceAlreadyExistsException;
import com.auriga.TTApp1.exception.ResourceNotFoundException;
import com.auriga.TTApp1.model.MatchType;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentMatch;
import com.auriga.TTApp1.model.TournamentRound;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.repository.MatchTypeRepository;
import com.auriga.TTApp1.repository.TournamentMatchRepository;
import com.auriga.TTApp1.repository.TournamentRepository;
import com.auriga.TTApp1.repository.TournamentRoundRepository;

@Service
public class TournamentService {
	@Autowired
	private TournamentRepository repo;
	
	@Autowired
	private TournamentRoundRepository roundRepo;
	
	@Autowired
	private TournamentMatchRepository matchRepo;

	@Autowired
	private MatchTypeRepository matchTypeRepo;

	@Autowired
	private PlayerService playerService;
	
	@Autowired
	private MatchTypeService matchTypeService;

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

	public void save(Tournament tournament) {
		List<MatchType> matchTypes = new ArrayList();
		tournament.getMatchTypeIds().forEach(item -> {
			Long id = Long.valueOf(item);
			matchTypes.add(matchTypeRepo.getById(id));
		});
		
		tournament.setMatchTypes(matchTypes);
		repo.save(tournament);
	}

	public Optional<Tournament> get(Long id) {
		return repo.findById(id);
	}

	public void delete(Tournament tournament) {
		repo.delete(tournament);
	}
	
	public void createDrawRounds(TournamentDrawDto dto) {
		Tournament tournament = get(dto.getTournamentId()).orElseThrow(() -> new ResourceNotFoundException("Tournament"));
		MatchType matchType = matchTypeService.get(dto.getMatchTypeId()).orElseThrow(() -> new ResourceNotFoundException("Match Type"));
		if(roundRepo.countByTournamentAndMatchType(tournament, matchType) > 0) {
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
		
		/* Save Round Entries */
		roundMatchCount.forEach((order, matchCount) -> {
			
			TournamentRound round = new TournamentRound();
			round.setTournament(tournament);
			round.setMatchType(matchType);
			round.setOrder(order);
			round.setName(getRoundName(order, totalRound));
			round.setType(getRoundType(order, totalRound));
			
			roundRepo.save(round);
			
			createRoundMatches(round, players, matchCount);
		});
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
		IntStream.range(0, matchCount).forEach(index -> {
			Integer index2 = (participantCount-1)-index;
			
			TournamentMatch tournamentMatch = new TournamentMatch();
			
			User player1 = players.get(index);
			User player2 = players.get(index2);
			
			//give bye
			if(player1.getId() == player2.getId()) {
				player2 = null;
			}
			
			tournamentMatch.setTournamentRound(round);
			tournamentMatch.setName("Match "+(index+1));
			tournamentMatch.setOrder(index+1);
			tournamentMatch.setPlayer1(player1);
			tournamentMatch.setPlayer2(player2);
			tournamentMatch.setStatus(MatchStatusEnum.PENDING);
			matchRepo.save(tournamentMatch);
		});
	}
	
	public void createNonPlayerMatches(TournamentRound round, Integer matchCount) {
		/* Save match entries  */
		IntStream.range(0, matchCount).forEach(index -> {
			TournamentMatch tournamentMatch = new TournamentMatch();
			tournamentMatch.setTournamentRound(round);
			tournamentMatch.setName("Match"+(index+1));
			tournamentMatch.setOrder(index+1);
			tournamentMatch.setStatus(MatchStatusEnum.INACTIVE);
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

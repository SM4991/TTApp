package com.auriga.TTApp1.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auriga.TTApp1.constants.MatchSetStatusEnum;
import com.auriga.TTApp1.constants.MatchStatusEnum;
import com.auriga.TTApp1.constants.RoundTypeEnum;
import com.auriga.TTApp1.dto.PlayerDto;
import com.auriga.TTApp1.exception.ResourceBadRequestException;
import com.auriga.TTApp1.exception.UserAlreadyExistsException;
import com.auriga.TTApp1.model.Role;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentMatch;
import com.auriga.TTApp1.model.TournamentMatchSet;
import com.auriga.TTApp1.model.TournamentRound;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.repository.RoleRepository;
import com.auriga.TTApp1.repository.TournamentMatchRepository;
import com.auriga.TTApp1.repository.TournamentMatchSetRepository;
import com.auriga.TTApp1.repository.TournamentRepository;
import com.auriga.TTApp1.repository.TournamentRoundRepository;
import com.auriga.TTApp1.repository.UserRepository;

@Service
public class TournamentMatchSetService {
	@Autowired
    private TournamentMatchSetRepository repo;
	
	@Autowired
    private TournamentMatchRepository matchRepo;
	
	@Autowired
    private TournamentRoundRepository roundRepo;
	
	@Autowired
	private TournamentRepository tournamentRepo;
     
    public List<TournamentMatchSet> listAllByMatch(TournamentMatch match) {
    	return repo.findAllByTournamentMatch(match);
    }
	
    @Transactional
	public void startSet(TournamentMatch match, Integer setNumber) {
		/* Check if set number is valid (only 1-3 allowed) */
    	if (setNumber < 1 && setNumber > 3) throw new ResourceBadRequestException("Invalid set");
		
    	/* check if match is inactive */
		if(match.getStatus() == MatchStatusEnum.INACTIVE) throw new ResourceBadRequestException("Match is in inactive state.");

		Tournament tournament = match.getTournament();
		
		/* Check if tournament match can be started or not */
		if(!tournament.getCanTournamentStart()) throw new ResourceBadRequestException("Match cannot be started before its start date.");
		
		/* Check if match already finished and has winner or not */
		if(match.getWinner() != null) throw new ResourceBadRequestException("Match is already complete.");
		
        TournamentMatchSet set = new TournamentMatchSet();
        set.setTournamentMatch(match);
        set.setSetNumber(setNumber);
        set.setName("Set " + setNumber);
        set.setPlayer1Score(0);
        set.setPlayer2Score(0);
        set.setStatus(MatchSetStatusEnum.ONGOING);
        
        repo.save(set);
        
        /* Update Match Status as Live */
        match.setStatus(MatchStatusEnum.LIVE);
        
        matchRepo.save(match);
	}
	
	@Transactional
	public Map<String, Integer> updateScore(TournamentMatchSet set, Integer playerNumber, Boolean state) {
		/* Check if player number is valid (only 1 or 2 allowed) */
		if(playerNumber < 1 && playerNumber > 2) throw new ResourceBadRequestException("Invalid request");
		
		/* Check if set is already completed */
		if(set.getStatus() == MatchSetStatusEnum.COMPLETE) throw new ResourceBadRequestException("Set is already finished.");
		
		TournamentMatch match = set.getTournamentMatch();
		TournamentRound round = match.getTournamentRound();
		Tournament tournament = round.getTournament();
		Integer max_score = tournament.getMaxScore();
		
		/* Check if tournament match can be started or not */
		if(!tournament.getCanTournamentStart()) throw new ResourceBadRequestException("Match cannot be started before its start date.");
		
		Integer score;
		Integer status = 1;
		if(playerNumber == 1) {
			score = set.getPlayer1Score();
			score = state == true ? score+1 : score-1;
			score = score < 0 ? 0 : score;
			set.setPlayer1Score(score);
		} else {
			score = set.getPlayer2Score();
			score = state == true ? score+1 : score-1;
			score = score < 0 ? 0 : score;
			set.setPlayer2Score(score);
		}
		
		/* If a player reached max score, mark him as winner */
		Boolean setWinner = false; 
		if(max_score <= set.getPlayer1Score() || max_score <= set.getPlayer2Score()) {
			setWinner = true;
			set.setStatus(MatchSetStatusEnum.COMPLETE);
			status = 2;
			if(set.getPlayer1Score() > set.getPlayer2Score()) {
				set.setWinner(match.getPlayer1());
			} else {
				set.setWinner(match.getPlayer2());
			}
		}
		
		repo.save(set);
		
		/* Set match winner */
		if(setWinner) {
			setMatchWinner(tournament, round, match, set);
		}
		
		Map<String, Integer> result = new HashMap<>(); 
		result.put("score", score);
		result.put("status", status);
		
		return result;
	}
	
	@Transactional
	public void setMatchWinner(Tournament tournament, TournamentRound round, TournamentMatch match, TournamentMatchSet set) {
		/* If match set is 2/3 and any of the player's score has exceeded max score, set him/her as winner */
		if(set.getSetNumber() > 1) {
			User winner = getMatchWinner(match);
			if(winner != null) {
				match.setStatus(MatchStatusEnum.COMPLETE);
				match.setWinner(winner);
				matchRepo.save(match);
				
				/* Set match winner as next round player */
				setNextRoundPlayer(tournament, round, match, winner);
			}
		}
	}
	
	@Transactional
	public void setNextRoundPlayer(Tournament tournament, TournamentRound round, TournamentMatch match, User winner) {
		/* If round is not final, set current match winner as next round's match player, else set tournament match */
		if(round.getType() != RoundTypeEnum.FINAL) {
			TournamentMatch nextMatch = matchRepo.findByTournamentRoundAndOrder(round, (match.getOrder()+1));
			TournamentRound nextRound = roundRepo.findByTournamentAndOrder(tournament, round.getOrder()+1);
			System.out.println("nextRound: "+nextRound);
			
			/* if next round is not empty, set current match winner as next round's match player */
			if(nextRound != null) {
				Integer order = (match.getOrder()/2)+(match.getOrder()%2);
				TournamentMatch nextRoundMatch = matchRepo.findByTournamentRoundAndOrder(nextRound, order);
				System.out.println("nextRoundMatch: "+nextRoundMatch);
				
				/* if next round's match is not empty, set current match winner as its player */
				if(nextRoundMatch != null) {
					/* If next match for current match does not exists & next round is not final, give the next round match bye & mark the player as winner */
					System.out.println("nextMatch: "+nextMatch);
					if(nextMatch == null && nextRound.getType() != RoundTypeEnum.FINAL) {
						System.out.println("Give bye");
						
						nextRoundMatch.setPlayer1(winner);
						nextRoundMatch.setWinner(winner);
						nextRoundMatch.setByeGiven(true);
						nextRoundMatch.setStatus(MatchStatusEnum.COMPLETE);
						
						/* Set match winner as next round player*/
						setNextRoundPlayer(tournament, nextRound, nextRoundMatch, winner);
					} else {
						System.out.println("Set Player");
						if(nextRoundMatch.getPlayer1() == null) {
							nextRoundMatch.setPlayer1(winner);
						} else if(nextRoundMatch.getPlayer2() == null) {
							nextRoundMatch.setPlayer2(winner);
							nextRoundMatch.setStatus(MatchStatusEnum.PENDING);
						}
						
						matchRepo.save(nextRoundMatch);
					}
				}
			}
		} else {
			tournament.setWinner(winner);
			tournamentRepo.save(tournament);
		}
	}
	
	public User getMatchWinner(TournamentMatch match) {
		User winner = repo.findTournamentMatchWinner(match);
		
		return winner;
	}
     
    public Optional<TournamentMatchSet> get(Long id) {
        return repo.findById(id);
    }
}

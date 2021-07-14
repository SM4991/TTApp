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
	
	public void startSet(TournamentMatch match, Integer setNumber) {
		if (setNumber < 1 && setNumber > 3) throw new ResourceBadRequestException("Invalid set");
		
        TournamentMatchSet set = new TournamentMatchSet();
        set.setTournamentMatch(match);
        set.setSetNumber(setNumber);
        set.setName("Set " + setNumber);
        set.setPlayer1Score(0);
        set.setPlayer2Score(0);
        set.setStatus(MatchSetStatusEnum.ONGOING);
        
        repo.save(set);
	}
	
	public Map<String, Integer> updateScore(TournamentMatchSet set, Integer playerNumber, Boolean state) {
		if(playerNumber < 1 && playerNumber > 2) throw new ResourceBadRequestException("Invalid request");
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
		TournamentMatch match = set.getTournamentMatch();
		TournamentRound round = match.getTournamentRound();
		Tournament tournament = round.getTournament();
		Integer max_score = tournament.getMaxScore();
		
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
	
	public void setNextRoundPlayer(Tournament tournament, TournamentRound round, TournamentMatch match, User winner) {
		if(round.getType() != RoundTypeEnum.FINAL) {
			TournamentRound nextRound = roundRepo.findByOrder(round.getOrder()+1);
			if(nextRound != null) {
				Integer order = (match.getOrder()/2)+(match.getOrder()%2);
				TournamentMatch nextRoundMatch = matchRepo.findByTournamentRoundAndOrder(nextRound, order);
				if(nextRoundMatch != null) {
					if(nextRoundMatch.getPlayer1() == null) {
						nextRoundMatch.setPlayer1(winner);
					} else if(nextRoundMatch.getPlayer2() == null) {
						nextRoundMatch.setPlayer2(winner);
						nextRoundMatch.setStatus(MatchStatusEnum.PENDING);
					}
					
					matchRepo.save(nextRoundMatch);
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

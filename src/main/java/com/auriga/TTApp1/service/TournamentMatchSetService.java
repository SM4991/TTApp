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
import com.auriga.TTApp1.dto.PlayerDto;
import com.auriga.TTApp1.exception.ResourceBadRequestException;
import com.auriga.TTApp1.exception.UserAlreadyExistsException;
import com.auriga.TTApp1.model.Role;
import com.auriga.TTApp1.model.TournamentMatch;
import com.auriga.TTApp1.model.TournamentMatchSet;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.repository.RoleRepository;
import com.auriga.TTApp1.repository.TournamentMatchSetRepository;
import com.auriga.TTApp1.repository.UserRepository;

@Service
public class TournamentMatchSetService {
	@Autowired
    private TournamentMatchSetRepository repo;
     
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
		Integer max_score = match.getTournamentRound().getTournament().getMaxScore();
		
		/* If a player reached max score, mark him as winner */
		if(max_score == score) {
			set.setStatus(MatchSetStatusEnum.COMPLETE);
			status = 2;
			if(set.getPlayer1Score() > set.getPlayer2Score()) {
				set.setWinner(match.getPlayer1());
			} else {
				set.setWinner(match.getPlayer2());
			}
		}
		
		repo.save(set);
		
		if(set.getSetNumber() > 1) {
			setMatchWinner(match);
		}
		
		Map<String, Integer> result = new HashMap<>(); 
		result.put("score", score);
		result.put("status", status);
		
		return result;
	}
	
	public void setMatchWinner(TournamentMatch match) {
//		User winner = 
	}
     
    public Optional<TournamentMatchSet> get(Long id) {
        return repo.findById(id);
    }
}

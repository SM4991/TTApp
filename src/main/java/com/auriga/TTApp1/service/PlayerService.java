package com.auriga.TTApp1.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.auriga.TTApp1.dto.PlayerDto;
import com.auriga.TTApp1.exception.UserAlreadyExistsException;
import com.auriga.TTApp1.model.Role;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.repository.RoleRepository;
import com.auriga.TTApp1.repository.UserRepository;
import com.auriga.TTApp1.service.PaginationService;

@Service
public class PlayerService{
	@Autowired
    private UserRepository repo;
	
	@Autowired
    private RoleRepository roleRepo;
	
	@Autowired
    private PaginationService paginationService;
	
	@Autowired 
	private CUserDetailsService cUserDetailsService;
	
    public List<User> listAll() {
    	return repo.findAllPlayer(getPlayerRole());
    }
    
    public PaginationService getAllItems(Integer page, Integer pageSize, String sortBy)
    {
    	Pageable paging = paginationService.pagingData(page, pageSize, sortBy);
 
        Page<User> pagedResult = repo.findAllPlayer(paging, getPlayerRole());
         
        return paginationService.paginatedItems(pagedResult, page, pageSize, sortBy);
    }
     
    public void save(PlayerDto playerForm) {
    	//Let's check if user already registered with us
        if(cUserDetailsService.checkIfUserExist(playerForm.getEmail())){
            throw new UserAlreadyExistsException("User already exists for this email");
        }	
    	Role role = getPlayerRole();
    	
    	User player = new User(); 
    	BeanUtils.copyProperties(playerForm, player);
    	
    	player.setIsLoginActive(false);
    	player.setRole(role);
        repo.save(player);
    }
     
    public Optional<User> get(Long id) {
        return repo.findPlayerById(id, getPlayerRole());
    }
    
    public User getById(Long id) {
        return repo.getPlayerById(id, getPlayerRole());
    }
     
    public void delete(User player) {
        repo.delete(player);
    }
    
    public Role getPlayerRole() {
    	return roleRepo.findByName("PLAYER");
    }
}

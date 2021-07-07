package com.auriga.TTApp1.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PaginationService {
	public List<Object> items;
	public Integer currentPage;
	public Integer totalPages;
	public Integer totalItems;
	
	public String sortField;
	
	public PaginationService paginatedItems(Page pagedResult, Integer currentPage, Integer pageSize, String sortBy) {
		this.items = pagedResult.getContent();
		this.currentPage = currentPage;
		this.totalPages = pagedResult.getTotalPages();
		this.totalItems = (int) pagedResult.getTotalElements();
		this.sortField = sortBy;
		
		return this;
	}
	
	public Pageable pagingData(Integer page, Integer pageSize, String sortBy) {
		Integer currentPage = page > 1 ? page-1 : 0; // PageRequest accept 0 as 1st page 

    	Pageable paging = (Pageable) PageRequest.of(currentPage, pageSize, Sort.by(sortBy));
    	
    	return paging;
	}
	
}

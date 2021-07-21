package com.auriga.TTApp1.filter;

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler(value = { 
			IllegalArgumentException.class, 
			IllegalStateException.class,
			SQLIntegrityConstraintViolationException.class
	})
    public ResponseEntity<Object> handleRunTimeExceptions(RuntimeException ex, WebRequest request) {
		/* Log the exception message */
		logger.error(ex.getMessage());
		
		String bodyOfResponse = "Internal server error occured, Please contact the administrator.";
        return handleExceptionInternal(ex, bodyOfResponse, 
          new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}

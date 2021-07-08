package com.auriga.TTApp1.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ResourceAlreadyExistsException extends RuntimeException {
	public ResourceAlreadyExistsException() {
        super();
    }

    public ResourceAlreadyExistsException(String message) {
        super(getExceptionMessage(message));
    }

    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(getExceptionMessage(message), cause);
    }
    
    public static String getExceptionMessage(String message) {
    	return message + " alredy exists.";
    }
}

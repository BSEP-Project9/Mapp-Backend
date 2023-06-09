package com.example.Mapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Object> handleREgistrationException(RegistrationException ex) {
        // Create a custom error response for your exception
        ErrorResponse errorResponse = new ErrorResponse("Custom exception occurred", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}

package com.example.Mapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UserBlockedException extends RuntimeException{
    public UserBlockedException() {
        super("User is blocked!");
    }
}

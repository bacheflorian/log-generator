package com.ad1.loggenerator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FilePathNotFoundException extends RuntimeException{
    public String message;

    public FilePathNotFoundException(String message) {
        super(message);
    }

}

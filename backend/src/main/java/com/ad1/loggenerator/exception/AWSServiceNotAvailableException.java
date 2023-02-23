package com.ad1.loggenerator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Handles AWS S3 Service not available exception
 */
@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class AWSServiceNotAvailableException extends RuntimeException{
    public String message;

    public AWSServiceNotAvailableException(String message) {
        super(message);
    }
}

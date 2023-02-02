package com.ad1.loggenerator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * Class to handle global exceptions
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles specified exception when file path is not found
     * @param exception
     * @param webRequest
     * @return error message details
     */
    @ExceptionHandler(FilePathNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleFileNotFoundException(FilePathNotFoundException exception,
                                                                    WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "SPECIFIED FILE PATH CANNOT BE FOUND");
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

}

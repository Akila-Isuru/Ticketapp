package com.ticketapp.booking.advicer;

import com.ticketapp.booking.exception.DuplicateException;
import com.ticketapp.booking.exception.NotFoundException;
import com.ticketapp.booking.utill.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardResponse> handleNotFoundException(NotFoundException e){
        return  new ResponseEntity<>(
                new StandardResponse(404,"Error",e.getMessage()), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<StandardResponse> handleDuplicateException(DuplicateException e){
        return new ResponseEntity<>(
                new StandardResponse(409,"Error",e.getMessage()), HttpStatus.CONFLICT
        );
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse> handleException(Exception e){
        return new ResponseEntity<>(
                new StandardResponse(500,"Error",e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}

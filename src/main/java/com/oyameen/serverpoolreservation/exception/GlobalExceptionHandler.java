package com.oyameen.serverpoolreservation.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //TODO: handle other different exception and it's status code, error ... etc.

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ServerPoolReservationException> handleExceptions(Exception exception) {
        ServerPoolReservationException serverPoolReservationException
                = new ServerPoolReservationException(System.currentTimeMillis(),
                400,
                "Bad Request",
                exception.getMessage());
        return ResponseEntity.status(400).body(serverPoolReservationException);
    }
}

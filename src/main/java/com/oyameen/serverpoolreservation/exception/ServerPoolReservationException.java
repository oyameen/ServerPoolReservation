package com.oyameen.serverpoolreservation.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerPoolReservationException{
    private long timeStamp;
    private int status;
    private String error;
    private String message;


}

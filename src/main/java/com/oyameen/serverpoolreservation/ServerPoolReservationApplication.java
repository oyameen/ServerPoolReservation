package com.oyameen.serverpoolreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.config.EnableStateMachine;

@SpringBootApplication
@EnableStateMachine
public class ServerPoolReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerPoolReservationApplication.class, args);
    }

}

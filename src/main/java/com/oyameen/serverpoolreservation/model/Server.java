package com.oyameen.serverpoolreservation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



import java.util.List;

@Document
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class Server {
    @Id
    private Long id;
    private int capacity;
    private ServerStatus serverStatus;
    private int numberOfUser;
    private List<String> users;
    private long version;
}

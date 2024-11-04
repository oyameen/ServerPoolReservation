package com.oyameen.serverpoolreservation.repositories;

import com.oyameen.serverpoolreservation.model.Server;
import com.oyameen.serverpoolreservation.model.ServerStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerPoolRepository extends MongoRepository<Server, Long> {
    default void activate(Server s) {
        System.out.println("====> Start activating server with id = " + s.getId());
        Server originServer = findById(s.getId()).get();

        if (s.getVersion() != originServer.getVersion()) {
            System.err.println("Server with id = [ " + s.getId() + " ] not activated, Incorrect version.");
        } else {
            s.setVersion(s.getVersion() + 1);
            s.setServerStatus(ServerStatus.ACTIVE);
            s.setCapacity(originServer.getCapacity());
            s.setNumberOfUser(originServer.getNumberOfUser());
            s.setUsers(originServer.getUsers());
            save(s);
            System.out.println("Server with id = [ " + s.getId() + " ] was activated successfully.");
        }
    }

    List<Server> findAllByServerStatus(ServerStatus serverStatus);
}

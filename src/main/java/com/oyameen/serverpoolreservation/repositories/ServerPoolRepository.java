package com.oyameen.serverpoolreservation.repositories;

import com.oyameen.serverpoolreservation.model.Server;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerPoolRepository extends MongoRepository<Server, Long> {
    default void update(Server s) {
        Server originServer = findById(s.getId()).get();

        System.out.println("----> id is = " + s.getId() + "\t" + s.getVersion() + "\t" + originServer.getVersion());

        if (s.getVersion() != originServer.getVersion()) {
            System.err.println("Incorrect version.");
        } else {
            System.out.println("====> id is = " + s.getId() + "\t" + s.getVersion() + "\t" + originServer.getVersion());
            s.setVersion(s.getVersion() + 1);
            save(s);
        }
    }
}

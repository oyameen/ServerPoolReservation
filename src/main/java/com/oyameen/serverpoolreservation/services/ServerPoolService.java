package com.oyameen.serverpoolreservation.services;

import com.oyameen.serverpoolreservation.model.Server;
import com.oyameen.serverpoolreservation.model.ServerStatus;
import com.oyameen.serverpoolreservation.repositories.ServerPoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ServerPoolService {
    @Autowired
    ServerPoolRepository serverPoolRepository;

    private static final int maxServerCapacity = 100;

    private static final int timeToActivateServerInMills = 10000;

    public List<Server> getAllServers() {
        return serverPoolRepository.findAll();
    }

    public void allocateNewServer(int capacity, String userName) {
        List<Server> servers = serverPoolRepository.findAll();
        for (Server server : servers) {
            if (server.getCapacity() < maxServerCapacity) {

                if (server.getCapacity() + capacity <= maxServerCapacity) {
                    server.setCapacity(server.getCapacity() + capacity);
                    capacity = 0;
                } else {
                    int x = maxServerCapacity - server.getCapacity();
                    server.setCapacity(maxServerCapacity);
                    capacity = capacity - x;
                }
                server.getUsers().add(userName);
                server.setNumberOfUser(server.getNumberOfUser() + 1);
                serverPoolRepository.save(server);
                if (capacity == 0) {
                    return;
                }
            }
        }

        if (capacity > 0) {
            long newId = (System.currentTimeMillis() << 20) | (System.nanoTime() & 0xFFFFFL);
            List<String> usersOfServer = new ArrayList<>();
            Server newServer = new Server(newId, 0, ServerStatus.CREATING, 0, usersOfServer, 1);
            serverPoolRepository.save(newServer);
            System.out.println("Server with id = [ " + newServer.getId() + " ] was created successfully.");
            activateServer(newServer);
            allocateNewServer(capacity, userName);
        }
    }

    public void activateServer(Server server) {
        Thread thread = new Thread(() ->
        {
            try {
                Thread.sleep(timeToActivateServerInMills);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            serverPoolRepository.activate(server);
        });
        thread.start();
    }

    public Server getServer(long id) {
        return serverPoolRepository.findById(id).get();
    }

    public int getAllocatedSize() {

        AtomicInteger sum = new AtomicInteger();
        serverPoolRepository.findAll()
                .forEach(server ->
                        sum.addAndGet(server.getCapacity()));
        return sum.get();
    }

    public long getServerCount() {
        return serverPoolRepository.count();
    }
}

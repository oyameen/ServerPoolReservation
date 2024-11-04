package com.oyameen.serverpoolreservation.services;

import com.oyameen.serverpoolreservation.model.Server;
import com.oyameen.serverpoolreservation.model.ServerStatus;
import com.oyameen.serverpoolreservation.repositories.ServerPoolRepository;
import com.oyameen.serverpoolreservation.statemachine.ServerEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.oyameen.serverpoolreservation.constants.ServerPoolConstants.maxServerCapacity;
import static com.oyameen.serverpoolreservation.constants.ServerPoolConstants.timeToActivateServerInMills;

@Service
public class ServerPoolService {
    @Autowired
    ServerPoolRepository serverPoolRepository;

    @Autowired
    private StateMachineFactory<ServerStatus, ServerEvent> stateMachineFactory;

    public List<Server> getAllServers(ServerStatus serverStatus) {
        if (serverStatus != null) {
            return serverPoolRepository.findAllByServerStatus(serverStatus);
        }

        return serverPoolRepository.findAll();
    }

    public void allocateNewServerV1(int capacity, String userName) {
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
            activateServerV1(newServer);
            allocateNewServerV1(capacity, userName);
        }
    }

    public void activateServerV1(Server server) {
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


    public void allocateNewServerV2(int capacity, String userName) {
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
            stateMachineFactory.getStateMachine(String.valueOf(newServer.getId())).start();
            allocateNewServerV2(capacity, userName);
        }
    }

    public void activateServerV2(Server s) {
        Server server = serverPoolRepository.findById(s.getId()).get();
        server.setServerStatus(ServerStatus.ACTIVE);
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

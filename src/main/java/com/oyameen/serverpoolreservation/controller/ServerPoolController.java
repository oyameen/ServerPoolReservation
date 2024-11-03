package com.oyameen.serverpoolreservation.controller;

import com.oyameen.serverpoolreservation.model.Server;
import com.oyameen.serverpoolreservation.services.ServerPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ServerPoolController {

    @Autowired
    private ServerPoolService serverPoolService;
    private int sum;

    @GetMapping("/servers")
    public List<Server> getAllServers() {
        return serverPoolService.getAllServers();
    }

    @GetMapping("/servers/{id}")
    public Server getServer(@PathVariable(value = "id") long id) {
        return serverPoolService.getServer(id);
    }

    @GetMapping("/allocate/v1/{capacity}/{userName}")
    public String allocateNewServer(@PathVariable(value = "capacity") int capacity,
                                    @PathVariable(value = "userName") String userName) {
        serverPoolService.allocateNewServerV1(capacity, userName);
        return "Allocating new server was done.";
    }

    @GetMapping("/allocate/v2/{capacity}/{userName}")
    public String allocateNewServerV2(@PathVariable(value = "capacity") int capacity,
                                      @PathVariable(value = "userName") String userName) {
        serverPoolService.allocateNewServerV2(capacity, userName);
        return "Allocating new server was done.";
    }

    @GetMapping(value = "/allocatedSize")
    public int allocatedSize() {

        return serverPoolService.getAllocatedSize();

    }

    @GetMapping(value = "serverCount")
    public long serverCount() {
        return serverPoolService.getServerCount();
    }

}

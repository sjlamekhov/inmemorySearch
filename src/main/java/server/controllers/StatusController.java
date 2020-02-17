package server.controllers;

import networking.GossipServiceClient;
import networking.GossipServiceServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import platform.StatusService;

import java.util.Map;

@RestController
public class StatusController {

    @Autowired
    private StatusService statusService;

    @RequestMapping(value = "/status/serverStatus",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public GossipServiceServer.ServerStatus getServerStatus() {
        return statusService.getServerStatus();
    }

    @RequestMapping(value = "/status/clientsStatus",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Map<String, GossipServiceClient.ClientStatus> getClientStatus() {
        return statusService.getClientStatus();
    }

}

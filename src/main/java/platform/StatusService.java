package platform;

import networking.GossipServiceClient;
import networking.GossipServiceMultiClient;
import networking.GossipServiceServer;

import java.util.Map;

public class StatusService {

    private final GossipServiceServer gossipServiceServer;
    private final GossipServiceMultiClient gossipServiceMultiClient;

    public StatusService(GossipServiceServer gossipServiceServer, GossipServiceMultiClient gossipServiceMultiClient) {
        this.gossipServiceServer = gossipServiceServer;
        this.gossipServiceMultiClient = gossipServiceMultiClient;
    }

    public GossipServiceServer.ServerStatus getServerStatus() {
        return gossipServiceServer.getServerStatus();
    }

    public Map<String, GossipServiceClient.ClientStatus> getClientStatus() {
        return gossipServiceMultiClient.getClientStatus();
    }

}

package platform;

import networking.GossipServiceClient;
import networking.GossipServiceMultiClient;
import networking.GossipServiceServer;

import java.util.HashMap;
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

    public Map<String, Long> getMemoryStatus() {
        Runtime runtime = Runtime.getRuntime();
        Map<String, Long> result = new HashMap<>();
        result.put("maxMemory", runtime.maxMemory());
        result.put("freeMemory", runtime.freeMemory());
        result.put("totalMemory", runtime.totalMemory());
        return result;
    }

}

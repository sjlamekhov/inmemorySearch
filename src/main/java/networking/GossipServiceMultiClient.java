package networking;

import networking.GossipServiceClient;
import networking.protobuf.ChangeRequest;

import java.util.HashMap;
import java.util.Map;

public class GossipServiceMultiClient {

    private Map<String, GossipServiceClient> clients;

    public GossipServiceMultiClient() {
        clients = new HashMap<>();
    }

    public void registerClient(String address, GossipServiceClient gossipServiceClient) {
        if (clients.containsKey(address)) {
            throw new RuntimeException(String.format("gossipClient with address %s already registered", address));
        }
        clients.put(address, gossipServiceClient);
    }

    public void init() {
        clients.values().forEach(GossipServiceClient::init);
    }

    public void sendChange(ChangeRequest changeRequest) {
        clients.values().forEach(i -> i.sendChange(changeRequest));
    }

    public boolean isStarted() {
        for (GossipServiceClient gossipServiceClient : clients.values()) {
            if (!gossipServiceClient.isStarted()) {
                break;
            }
        }
        return true;
    }

    public void close() {
        clients.values().forEach(GossipServiceClient::close);
    }

}

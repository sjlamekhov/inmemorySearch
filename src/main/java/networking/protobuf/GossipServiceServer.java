package networking.protobuf;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.util.LinkedList;
import java.util.Queue;

public class GossipServiceServer {

    private Server server;
    private Queue<ChangeRequest> incomingQueue;

    public GossipServiceServer() {
        this.incomingQueue = new LinkedList<>();
    }

    public Queue<ChangeRequest> getIncomingQueue() {
        return incomingQueue;
    }

    public void init() {
        server = ServerBuilder
                .forPort(6565)
                .addService(new GossipServiceImpl(incomingQueue))
                .build();
        try {
            server.start();
            server.awaitTermination();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        server.shutdown();
    }

    public static void main(String[] args) throws Exception {
        GossipServiceServer gossipServiceServer = new GossipServiceServer();
        gossipServiceServer.init();
    }

}

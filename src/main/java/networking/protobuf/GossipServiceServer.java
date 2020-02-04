package networking.protobuf;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.util.LinkedList;
import java.util.Queue;

public class GossipServiceServer {

    private Queue<ChangeRequest> incomingQueue;

    public GossipServiceServer() {
        this.incomingQueue = new LinkedList<>();
    }

    public Queue<ChangeRequest> getIncomingQueue() {
        return incomingQueue;
    }

    public void init() throws Exception {
        Server server = ServerBuilder
                .forPort(6565)
                .addService(new GossipServiceImpl(incomingQueue))
                .build();

        server.start();
        server.awaitTermination();
    }

    public static void main(String[] args) throws Exception {
        GossipServiceServer gossipServiceServer = new GossipServiceServer();
        gossipServiceServer.init();
    }

}

package networking;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import networking.protobuf.ChangeRequest;
import networking.protobuf.GossipServiceImpl;

import java.util.LinkedList;
import java.util.Queue;

public class GossipServiceServer {

    private static final int DEFAULT_PORT = 6060;

    private Server server;
    private int port;
    private Queue<ChangeRequest> incomingQueue;

    public GossipServiceServer() {
        this(DEFAULT_PORT);
    }

    public GossipServiceServer(int port) {
        this.port = port;
        this.incomingQueue = new LinkedList<>();
    }

    public Queue<ChangeRequest> getIncomingQueue() {
        return incomingQueue;
    }

    public void init() {
        server = ServerBuilder
                .forPort(port)
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

//    public static void main(String[] args) throws Exception {
//        GossipServiceServer gossipServiceServer = new GossipServiceServer();
//        gossipServiceServer.init();
//    }

}

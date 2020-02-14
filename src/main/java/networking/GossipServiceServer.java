package networking;

import io.grpc.InternalChannelz;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import networking.protobuf.ChangeRequest;
import networking.protobuf.GossipServiceImpl;

import java.util.LinkedList;
import java.util.Queue;

public class GossipServiceServer {

    private static final int DEFAULT_PORT = 6060;
    private boolean isStarted;

    private Server server;
    private int port;
    private Queue<ChangeRequest> incomingQueue;

    public GossipServiceServer() {
        this(DEFAULT_PORT);
    }

    public GossipServiceServer(int port) {
        this.port = port;
        this.isStarted = false;
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
            isStarted = true;
            System.out.println("server init done");
            server.awaitTermination();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isStarted() {
        return isStarted;
    }

    public ServerStatus getServerStatus() {
        return new ServerStatus(
                port,
                isStarted,
                incomingQueue.size()
        );
    }

    public void close() {
        server.shutdown();
    }

    public class ServerStatus {
        private final int port;
        private final boolean isStarted;
        private final int queueSize;

        public ServerStatus(int port, boolean isStarted, int queueSize) {
            this.port = port;
            this.isStarted = isStarted;
            this.queueSize = queueSize;
        }

        public int getPort() {
            return port;
        }

        public boolean isStarted() {
            return isStarted;
        }

        public int getQueueSize() {
            return queueSize;
        }
    }

}

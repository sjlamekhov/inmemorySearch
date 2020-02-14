package networking;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import networking.protobuf.ChangeAck;
import networking.protobuf.ChangeRequest;
import networking.protobuf.GossipServiceGrpc;

public class GossipServiceClient {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 6060;
    private boolean isStarted;

    private String host;
    private int port;
    private GossipServiceGrpc.GossipServiceBlockingStub stub;
    private ManagedChannel managedChannel;
    private long numberOfSentMessages = 0;

    public GossipServiceClient() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public GossipServiceClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.isStarted = false;
    }

    public void init() {
        managedChannel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        stub = GossipServiceGrpc.newBlockingStub(managedChannel);
        isStarted = true;
        System.out.println("client init done");
    }

    public ChangeAck sendChange(ChangeRequest changeRequest) {
        numberOfSentMessages += 1;
        return stub.process(changeRequest);
    }

    public boolean isStarted() {
        return isStarted;
    }

    public ClientStatus getClientStatus() {
        return new ClientStatus(
                host,
                port,
                isStarted,
                numberOfSentMessages
        );
    }

    public void close() {
        managedChannel.shutdown();
    }

    public class ClientStatus {

        private final String host;
        private final int port;
        private final boolean isStarted;
        private final long numberOfSentMessages;

        public ClientStatus(String host, int port, boolean isStarted, long numberOfSentMessages) {
            this.host = host;
            this.port = port;
            this.isStarted = isStarted;
            this.numberOfSentMessages = numberOfSentMessages;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public boolean isStarted() {
            return isStarted;
        }

        public long getNumberOfSentMessages() {
            return numberOfSentMessages;
        }
    }

}

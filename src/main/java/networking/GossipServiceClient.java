package networking;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import networking.protobuf.ChangeAck;
import networking.protobuf.ChangeRequest;
import networking.protobuf.GossipServiceGrpc;

public class GossipServiceClient {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 6060;

    private String host;
    private int port;
    private GossipServiceGrpc.GossipServiceBlockingStub stub;
    private ManagedChannel managedChannel;

    public GossipServiceClient() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public GossipServiceClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void init() {
        managedChannel = ManagedChannelBuilder
                .forAddress(host, port).usePlaintext().build();
        stub = GossipServiceGrpc.newBlockingStub(managedChannel);
    }

    public ChangeAck sendChange(ChangeRequest changeRequest) {
        return stub.process(changeRequest);
    }

    public void close() {
        managedChannel.shutdown();
    }

//    public static void main(String[] args) {
//        GossipServiceClient gossipServiceClient = new GossipServiceClient();
//        gossipServiceClient.init();
//        gossipServiceClient.sendChange(ChangeRequest.newBuilder().setMessage("message").build());
//        gossipServiceClient.close();
//    }

}

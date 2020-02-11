package networking.protobuf;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GossipServiceClient {

    private GossipServiceGrpc.GossipServiceBlockingStub stub;
    private ManagedChannel managedChannel;

    public void init() {
        managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 6565).usePlaintext().build();
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

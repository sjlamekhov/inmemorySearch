package networking.protobuf;

import io.grpc.stub.StreamObserver;

public class GossipServiceImpl extends GossipServiceGrpc.GossipServiceImplBase {

    @Override
    public void process(ChangeRequest request, StreamObserver<ChangeAck> responseObserver) {
        System.out.println("received:\t" + request);

        ChangeAck response = ChangeAck.newBuilder()
                .setMessage("ACK_" + System.currentTimeMillis() + "_" + request.getTimestamp())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}

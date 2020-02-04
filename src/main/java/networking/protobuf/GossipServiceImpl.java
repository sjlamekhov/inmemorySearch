package networking.protobuf;

import io.grpc.stub.StreamObserver;

import java.util.Queue;

public class GossipServiceImpl extends GossipServiceGrpc.GossipServiceImplBase {

    private Queue<ChangeRequest> incomingQueue;

    public GossipServiceImpl(Queue<ChangeRequest> incomingQueue) {
        this.incomingQueue = incomingQueue;
    }

    public Queue<ChangeRequest> getIncomingQueue() {
        return incomingQueue;
    }

    @Override
    public void process(ChangeRequest request, StreamObserver<ChangeAck> responseObserver) {
        System.out.println("received:\t" + request);
        incomingQueue.add(request);
        ChangeAck response = ChangeAck.newBuilder()
                .setMessage("ACK_" + System.currentTimeMillis() + "_" + request.getTimestamp())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}

package networking.protobuf;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GossipServiceServer {

    public void init() throws Exception {
        Server server = ServerBuilder
                .forPort(6565)
                .addService(new GossipServiceImpl()).build();

        server.start();
        server.awaitTermination();
    }

    public static void main(String[] args) throws Exception {
        GossipServiceServer gossipServiceServer = new GossipServiceServer();
        gossipServiceServer.init();
    }

}

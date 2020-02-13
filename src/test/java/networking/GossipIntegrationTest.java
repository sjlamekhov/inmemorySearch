package networking;

import networking.protobuf.ChangeRequest;
import org.junit.Assert;
import org.junit.Test;
import platform.Platform;
import platform.PlatformFactory;
import utils.FileUtils;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class GossipIntegrationTest {

    @Test
    public void test() throws Exception {
        Platform platformA = PlatformFactory.buildPlatform(
                FileUtils.propertiesFromClasspath("networking/configA.properties")
        );
        Platform platformB = PlatformFactory.buildPlatform(
                FileUtils.propertiesFromClasspath("networking/configB.properties")
        );
        GossipServiceMultiClient gossipServiceClient = platformA.getGossipServiceMultiClient();
        GossipServiceServer gossipServiceServer = platformB.getGossipServiceServer();

        try {
            ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(2);
            threadPoolExecutor.submit(gossipServiceClient::init);
            threadPoolExecutor.submit(gossipServiceServer::init);

            waitFor(gossipServiceClient::isStarted);
            waitFor(gossipServiceServer::isStarted);

            gossipServiceClient.sendChange(ChangeRequest.newBuilder()
                    .setMessage("mockMessage")
                    .setTimestamp(String.valueOf(System.currentTimeMillis()))
                    .build());
            Queue<ChangeRequest> q = gossipServiceServer.getIncomingQueue();
            Assert.assertEquals(1, q.size());
            Assert.assertNotNull(q.poll());
        } finally {
            gossipServiceClient.close();
            gossipServiceServer.close();
        }
    }

    private void waitFor(Supplier<Boolean> toWaitFor) throws InterruptedException {
        waitFor(toWaitFor, 10000);
    }

    private void waitFor(Supplier<Boolean> toWaitFor, long maxWait) throws InterruptedException {
        long startOfWaiting = System.currentTimeMillis();
        while (System.currentTimeMillis() - startOfWaiting < maxWait) {
            if (toWaitFor.get()) {
                break;
            }
            Thread.sleep(1000);
        }
    }

}

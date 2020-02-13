package networking;

import networking.protobuf.ChangeRequest;
import objects.Document;
import objects.DocumentUri;
import org.junit.Assert;
import org.junit.Test;
import platform.Platform;
import platform.PlatformFactory;
import utils.FileUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static utils.QueueUtils.extractFromQueue;
import static utils.TestUtils.waitFor;

public class GossipIntegrationTest {

    @Test
    public void testWithMultiClient() throws Exception {
        MessageConverter messageConverter = new MessageConverterJson();
        Map<String, String> attributes = new HashMap<>();
        attributes.put("key", "value");
        Document documentToSend = new Document(
                new DocumentUri("testTenantId"),
                attributes
        );
        Message messageTosend = new Message(
                System.currentTimeMillis(),
                documentToSend,
                "localhost",
                Message.MessageType.CREATE
        );
        ChangeRequest changeRequestToSend = messageConverter.convertToChangeRequest(messageTosend);

        Platform platformA = PlatformFactory.buildPlatform(FileUtils.propertiesFromClasspath("networking/configA.properties"));
        Platform platformB = PlatformFactory.buildPlatform(FileUtils.propertiesFromClasspath("networking/configB.properties"));
        Platform platformC = PlatformFactory.buildPlatform(FileUtils.propertiesFromClasspath("networking/configC.properties"));
        GossipServiceMultiClient gossipServiceClient = platformA.getGossipServiceMultiClient();
        GossipServiceServer gossipServiceServerB = platformB.getGossipServiceServer();
        GossipServiceServer gossipServiceServerC = platformC.getGossipServiceServer();

        try {
            ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(3);
            threadPoolExecutor.submit(gossipServiceClient::init);
            threadPoolExecutor.submit(gossipServiceServerB::init);
            threadPoolExecutor.submit(gossipServiceServerC::init);

            waitFor(gossipServiceClient::isStarted);
            waitFor(gossipServiceServerB::isStarted);
            waitFor(gossipServiceServerC::isStarted);

            gossipServiceClient.sendChange(changeRequestToSend);
            //checking B
            List<ChangeRequest> extractedFromQueue = extractFromQueue(gossipServiceServerB.getIncomingQueue());
            Assert.assertEquals(1, extractedFromQueue.size());
            Assert.assertEquals(changeRequestToSend, extractedFromQueue.iterator().next());
            //checking C
            extractedFromQueue = extractFromQueue(gossipServiceServerC.getIncomingQueue());
            Assert.assertEquals(1, extractedFromQueue.size());
            Assert.assertEquals(changeRequestToSend, extractedFromQueue.iterator().next());
        } finally {
            gossipServiceClient.close();
            gossipServiceServerB.close();
            gossipServiceServerC.close();
        }
    }

}

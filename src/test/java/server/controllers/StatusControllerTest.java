package server.controllers;

import networking.GossipServiceClient;
import networking.GossipServiceServer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import utils.TestUtils;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {server.Application.class})
public class StatusControllerTest {

    @Autowired
    private StatusController statusController;

    @Test
    public void serverStatusTest() throws Exception {
        TestUtils.waitFor(statusController.getServerStatus()::isStarted);
        GossipServiceServer.ServerStatus serverStatus = statusController.getServerStatus();
        Assert.assertNotNull(serverStatus);
        Assert.assertEquals(7070, serverStatus.getPort());
        Assert.assertEquals(0, serverStatus.getQueueSize());
        Assert.assertTrue(serverStatus.isStarted());
    }

    @Test
    public void clientsStatusTest() {
        Map<String, GossipServiceClient.ClientStatus> response = statusController.getClientStatus();
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.size());
        Assert.assertTrue(response.containsKey("localhost:6060"));
        GossipServiceClient.ClientStatus clientStatus = response.get("localhost:6060");
        Assert.assertEquals("localhost", clientStatus.getHost());
        Assert.assertEquals(6060, clientStatus.getPort());
        Assert.assertEquals(0, clientStatus.getNumberOfSentMessages());
        Assert.assertTrue(clientStatus.isStarted());
    }

}
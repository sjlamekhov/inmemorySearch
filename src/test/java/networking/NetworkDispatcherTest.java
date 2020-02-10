package networking;

import objects.Document;
import objects.DocumentUri;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkDispatcherTest {

    private static final String tenantId = "testTenantId";

    @Test
    public void test() {
        List<Message> collectionForMessageSender = new ArrayList<>();
        List<Message> collectionForDownstream = new ArrayList<>();
        NetworkDispatcher networkDispatcher = new NetworkDispatcher(
                makeSupplierFromCollection(Arrays.asList(
                        new Message(
                                System.currentTimeMillis(),
                                new Document(new DocumentUri("fromOtherNodes1", tenantId)),
                                "localhost", Message.MessageType.CREATE),
                        new Message(
                                System.currentTimeMillis(),
                                new Document(new DocumentUri("fromOtherNodes2", tenantId)),
                                "localhost", Message.MessageType.DELETE)
                )),
                makeSupplierFromCollection(Arrays.asList(
                        new Message(
                                System.currentTimeMillis(),
                                new Document(new DocumentUri("fromThisNode1", tenantId)),
                                "localhost", Message.MessageType.CREATE),
                        new Message(
                                System.currentTimeMillis(),
                                new Document(new DocumentUri("fromThisNode2", tenantId)),
                                "localhost", Message.MessageType.DELETE)
                )),
                makeConsumerFromCollection(collectionForMessageSender),
                makeConsumerFromCollection(collectionForDownstream)
        );
        networkDispatcher.receiveAndSendMessages(true);

        Assert.assertEquals(2, collectionForMessageSender.size());
        Assert.assertTrue(collectionForMessageSender.stream().map(i -> i.getAbstractObject().getUri().getId()).allMatch(i -> i.startsWith("fromThisNode")));

        Assert.assertEquals(2, collectionForDownstream.size());
        Assert.assertTrue(collectionForDownstream.stream().map(i -> i.getAbstractObject().getUri().getId()).allMatch(i -> i.startsWith("fromOtherNodes")));
    }

    private static Consumer<Message> makeConsumerFromCollection(Collection<Message> output) {
        return output::add;
    }

    private static Supplier<Message> makeSupplierFromCollection(Collection<Message> input) {
        return new Supplier<Message>() {

            private LinkedList<Message> toSupply = new LinkedList<>(input);

            @Override
            public Message get() {
                return toSupply.isEmpty() ? null : toSupply.pollFirst();
            }
        };
    }

}
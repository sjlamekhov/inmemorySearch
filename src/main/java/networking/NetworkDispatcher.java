package networking;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkDispatcher {

    private final Queue<Message> receivedMessages;
    private final Queue<Message> toSend;

    private final Supplier<Message> messageProvider1;
    private final Supplier<Message> messageProvider2;
    private final Consumer<Message> messageSender;
    private final Consumer<Message> messageDownstreamConsumer;

    private final int maxQueueSize = 256;
    private long lastReceiveAndSendTimestamp = 0;
    private final long maxSendReceiveTimeout = 1000;
    private final int maxSendAndReceiveAtOneTime = 256;

    public NetworkDispatcher(
            Supplier<Message> messageProvider1,
            Supplier<Message> messageProvider2,
            Consumer<Message> messageSender,
            Consumer<Message> messageDownstreamConsumer) {
        this(messageProvider1, messageProvider2,
                messageSender,
                messageDownstreamConsumer,
                new LinkedList<>(),
                new LinkedList<>()
        );
    }

    public NetworkDispatcher(
            Supplier<Message> messagesFromOtherNodesProvider,
            Supplier<Message> messagesFromThisNodeServicesProvider,
            Consumer<Message> messageSender,
            Consumer<Message> messageDownstreamConsumer,
            Queue<Message> receivedMessages,
            Queue<Message> toSend) {
        this.messageProvider1 = messagesFromOtherNodesProvider;
        this.messageProvider2 = messagesFromThisNodeServicesProvider;
        this.messageSender = messageSender;
        this.messageDownstreamConsumer = messageDownstreamConsumer;

        this.receivedMessages = receivedMessages;
        this.toSend = toSend;
    }

    public synchronized void receiveAndSendMessages(boolean force) {
        lastReceiveAndSendTimestamp = System.currentTimeMillis();
        if (force || toSend.size() >= maxQueueSize || receivedMessages.size() >= maxQueueSize) {
            pollMessages(messageProvider1, receivedMessages::add, maxSendAndReceiveAtOneTime);
            pollMessages(messageProvider2, toSend::add, maxSendAndReceiveAtOneTime);
            pollMessages(toSend::poll, messageSender, maxSendAndReceiveAtOneTime);
            pollMessages(receivedMessages::poll, messageDownstreamConsumer, maxSendAndReceiveAtOneTime);
        }
    }

    private static void pollMessages(Supplier<Message> source, Consumer<Message> messageConsumer, int maxSendAndReceiveAtOneTime) {
        int polledCount = 0;
        Message message = source.get();
        while (null != message && polledCount < maxSendAndReceiveAtOneTime) {
            messageConsumer.accept(message);
            message = source.get();
            polledCount++;
        }
    }

}

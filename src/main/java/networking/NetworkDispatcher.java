package networking;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkDispatcher {

    private final Queue<Message> receivedMessages;
    private final Queue<Message> toSend;

    private final Supplier<Message> messagesFromOtherNodesProvider;
    private final Supplier<Message> messagesFromThisNodeServicesProvider;
    private final Consumer<Message> messageSender;
    private final Consumer<Message> messageDownstreamConsumer;

    private final int maxQueueSize = 256;
    private long lastReceiveAndSendTimestamp = 0;
    private final long maxSendReceiveTimeout = 1000;
    private final int maxSendAndReceiveAtOneTime = 256;

    public NetworkDispatcher(
            Supplier<Message> messagesFromOtherNodesProvider,
            Supplier<Message> messagesFromThisNodeServicesProvider,
            Consumer<Message> messageSender,
            Consumer<Message> messageDownstreamConsumer) {
        this(messagesFromOtherNodesProvider,
                messagesFromThisNodeServicesProvider,
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
        this.messagesFromOtherNodesProvider = messagesFromOtherNodesProvider;
        this.messagesFromThisNodeServicesProvider = messagesFromThisNodeServicesProvider;
        this.messageSender = messageSender;
        this.messageDownstreamConsumer = messageDownstreamConsumer;

        this.receivedMessages = receivedMessages;
        this.toSend = toSend;
    }

    public synchronized void receiveAndSendMessages(boolean force) {
        lastReceiveAndSendTimestamp = System.currentTimeMillis();
        boolean forcePoll = force || toSend.size() >= maxQueueSize || receivedMessages.size() >= maxQueueSize;
        pollMessages(messagesFromOtherNodesProvider, receivedMessages::add, maxSendAndReceiveAtOneTime, forcePoll);
        pollMessages(messagesFromThisNodeServicesProvider, toSend::add, maxSendAndReceiveAtOneTime, forcePoll);
        pollMessages(toSend::poll, messageSender, maxSendAndReceiveAtOneTime, forcePoll);
        pollMessages(receivedMessages::poll, messageDownstreamConsumer, maxSendAndReceiveAtOneTime, forcePoll);
    }

    private static void pollMessages(Supplier<Message> source,
                                     Consumer<Message> messageConsumer,
                                     int maxSendAndReceiveAtOneTime,
                                     boolean force) {
        int polledCount = 0;
        Message message = source.get();
        while (null != message && (force || polledCount < maxSendAndReceiveAtOneTime)) {
            messageConsumer.accept(message);
            message = source.get();
            polledCount++;
        }
    }

}

package networking;

import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkDispatcher {

    private final Queue<Message> receivedMessages;
    private final Queue<Message> toSend;

    private final Supplier<Message> messageProvider;
    private final Consumer<Message> messageSender;

    private final int maxQueueSize = 256;
    private final int sendAndReceiveAtOneTime = 128;
    private final Consumer<Message> messageDownstreamConsumer;
    private long lastReceiveAndSendTimestamp = 0;
    private final long maxSendReceiveTimeout = 1000;

    public NetworkDispatcher(
            Supplier<Message> messageProvider,
            Consumer<Message> messageSender,
            Consumer<Message> messageDownstreamConsumer,
            Queue<Message> receivedMessages,
            Queue<Message> toSend) {
        this.messageProvider = messageProvider;
        this.messageSender = messageSender;
        this.messageDownstreamConsumer = messageDownstreamConsumer;

        this.receivedMessages = receivedMessages;
        this.toSend = toSend;
    }

    public synchronized void addMessageToSend(Message message) {
        toSend.add(message);
        if (toSend.size() >= maxQueueSize
                || (System.currentTimeMillis() - lastReceiveAndSendTimestamp >= maxSendReceiveTimeout)) {
            receiveAndSendMessages(true);
        }
    }

    public synchronized void receiveAndSendMessages(boolean force) {
        lastReceiveAndSendTimestamp = System.currentTimeMillis();
        if (force || toSend.size() >= maxQueueSize || receivedMessages.size() >= maxQueueSize) {
            int maxSendAndReceiveAtOneTime = 0;
            Message message = toSend.poll();
            while (null != message && maxSendAndReceiveAtOneTime < maxSendAndReceiveAtOneTime) {
                messageSender.accept(message);
                message = toSend.poll();
                maxSendAndReceiveAtOneTime++;
            }

            maxSendAndReceiveAtOneTime = 0;
            message = receivedMessages.poll();
            while (null != message && maxSendAndReceiveAtOneTime < maxSendAndReceiveAtOneTime) {
                messageDownstreamConsumer.accept(message);
                message = receivedMessages.poll();
                maxSendAndReceiveAtOneTime++;
            }
        }
    }

}

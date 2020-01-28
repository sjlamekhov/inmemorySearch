package networking;

import java.util.Queue;

public class NetworkDispatcher {

    private final Queue<Message> receivedMessages;
    private final Queue<Message> toSend;
    private final int maxQueueSize = 256;
    private long lastReceiveAndSendTimestamp = 0;
    private final long maxSendReceiveTimeout = 1000;

    public NetworkDispatcher(Queue<Message> receivedMessages, Queue<Message> toSend) {
        this.receivedMessages = receivedMessages;
        this.toSend = toSend;
    }

    public synchronized void addMessageToSend(Message message) {
        toSend.add(message);
        if (toSend.size() >= maxQueueSize
                || (System.currentTimeMillis() - lastReceiveAndSendTimestamp >= maxSendReceiveTimeout)) {
            receiveAndSendMessages();
        }
    }

    public void receiveAndSendMessages() {
        //TODO implement
        lastReceiveAndSendTimestamp = System.currentTimeMillis();
    }

}

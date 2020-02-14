package networking;

import objects.AbstractObject;

public class Message<T extends AbstractObject> {

    public enum MessageType {
        CREATE, DELETE
    }

    private long timestamp;
    private T object;
    private String sentFrom;
    private MessageType messageType;

    public Message() {
    }

    public Message(long timestamp, T object, String sentFrom, MessageType messageType) {
        this.timestamp = timestamp;
        this.object = object;
        this.sentFrom = sentFrom;
        this.messageType = messageType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public T getObject() {
        return object;
    }

    public String getSentFrom() {
        return sentFrom;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (timestamp != message.timestamp) return false;
        if (object != null ? !object.equals(message.object) : message.object != null)
            return false;
        if (sentFrom != null ? !sentFrom.equals(message.sentFrom) : message.sentFrom != null) return false;
        return messageType == message.messageType;
    }

    @Override
    public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (object != null ? object.hashCode() : 0);
        result = 31 * result + (sentFrom != null ? sentFrom.hashCode() : 0);
        result = 31 * result + (messageType != null ? messageType.hashCode() : 0);
        return result;
    }
}

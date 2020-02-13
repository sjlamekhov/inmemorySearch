package networking;

import objects.AbstractObject;

public class Message {

    public enum MessageType {
        CREATE, DELETE
    }

    private long timestamp;
    private AbstractObject abstractObject;
    private String sentFrom;
    private MessageType messageType;

    public Message(long timestamp, AbstractObject abstractObject, String sentFrom, MessageType messageType) {
        this.timestamp = timestamp;
        this.abstractObject = abstractObject;
        this.sentFrom = sentFrom;
        this.messageType = messageType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public AbstractObject getAbstractObject() {
        return abstractObject;
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
        if (abstractObject != null ? !abstractObject.equals(message.abstractObject) : message.abstractObject != null)
            return false;
        if (sentFrom != null ? !sentFrom.equals(message.sentFrom) : message.sentFrom != null) return false;
        return messageType == message.messageType;
    }

    @Override
    public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (abstractObject != null ? abstractObject.hashCode() : 0);
        result = 31 * result + (sentFrom != null ? sentFrom.hashCode() : 0);
        result = 31 * result + (messageType != null ? messageType.hashCode() : 0);
        return result;
    }
}

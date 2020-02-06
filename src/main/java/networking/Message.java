package networking;

import objects.AbstractObject;

public class Message {

    public enum MessageType {
        CREATE, DELETE
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

    public Message(long timestamp, AbstractObject abstractObject, String sentFrom, MessageType messageType) {
        this.timestamp = timestamp;
        this.abstractObject = abstractObject;
        this.sentFrom = sentFrom;
        this.messageType = messageType;
    }

    private long timestamp;
    private AbstractObject abstractObject;
    private String sentFrom;
    private MessageType messageType;

}

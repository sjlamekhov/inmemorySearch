package networking;

import networking.protobuf.ChangeRequest;
import objects.Document;
import objects.DocumentUri;

//TODO: implement, for now it's just mocks for tests
public class MessageConverterJson implements MessageConverter {

    public Message convertToMessage(ChangeRequest changeRequest) {
        return new Message(
                Long.valueOf(changeRequest.getTimestamp()),
                new Document(new DocumentUri("mock")),
                "",
                Message.MessageType.CREATE
        );
    }

    public ChangeRequest convertToChangeRequest(Message message) {
        return ChangeRequest.newBuilder()
                .setMessage("mockMessage")
                .setTimestamp(String.valueOf(System.currentTimeMillis()))
                .build();
    }

}

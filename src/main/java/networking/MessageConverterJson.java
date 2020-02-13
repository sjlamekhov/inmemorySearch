package networking;

import com.fasterxml.jackson.databind.ObjectMapper;
import networking.protobuf.ChangeRequest;

import java.io.IOException;

public class MessageConverterJson implements MessageConverter {

    private ObjectMapper mapper = new ObjectMapper();

    public Message convertToMessage(ChangeRequest changeRequest) {
        String messageString = changeRequest.getMessage();
        try {
            return mapper.readValue(messageString, Message.class);
        } catch (IOException e) {
            return null;
        }
    }

    public ChangeRequest convertToChangeRequest(Message message) {
        String messageString = null;
        try {
            messageString = mapper.writeValueAsString(message);
        } catch (IOException e) {}
        return ChangeRequest.newBuilder()
                .setMessage(messageString)
                .setTimestamp(String.valueOf(System.currentTimeMillis()))
                .build();
    }

}

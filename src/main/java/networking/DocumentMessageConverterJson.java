package networking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.socket.oio.DefaultOioSocketChannelConfig;
import networking.protobuf.ChangeRequest;
import objects.Document;

import java.io.IOException;

public class DocumentMessageConverterJson implements MessageConverter {

    private ObjectMapper mapper = new ObjectMapper();

    public Message convertToMessage(ChangeRequest changeRequest) {
        if (null == changeRequest) {
            return null;
        }
        String messageString = changeRequest.getMessage();
        try {
            return mapper.readValue(messageString, new TypeReference<Message<Document>>() {});
        } catch (IOException e) {
            e.printStackTrace();
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

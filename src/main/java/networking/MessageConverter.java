package networking;

import networking.protobuf.ChangeRequest;

public interface MessageConverter {

    Message convertToMessage(ChangeRequest changeRequest);

    ChangeRequest convertToChangeRequest(Message message);

}

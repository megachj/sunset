package sunset.reactive.chatserver.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChatMessage {

    private String fromUserId;
    private String toUserId;

    private String content;

    public String deserializeToSentMessage() {
        return String.format("from: %s\n%s", fromUserId, content);
    }

    public static ChatMessage serializeFromReceivedMessage(String fromUserId, String receivedMessage) {
        String[] parsed = receivedMessage.split("\n", 2);

        return ChatMessage.builder()
            .fromUserId(fromUserId)
            .toUserId(parsed[0])
            .content(parsed[1])
            .build();
    }
}

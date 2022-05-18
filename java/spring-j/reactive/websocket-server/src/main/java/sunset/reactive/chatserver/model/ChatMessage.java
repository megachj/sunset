package sunset.reactive.chatserver.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChatMessage {

    private String fromUserId;
    private String toUserId;

    private String content;

    public static ChatMessage parsePayload(String fromUserId, String payload) {
        String[] parsed = payload.split("\n");

        return ChatMessage.builder()
            .fromUserId(fromUserId)
            .toUserId(parsed[0])
            .content(parsed[1])
            .build();
    }
}

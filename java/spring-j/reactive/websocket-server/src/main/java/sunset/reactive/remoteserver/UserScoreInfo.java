package sunset.reactive.remoteserver;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@ToString
@Builder
public class UserScoreInfo {

    private String userId;
    private String category;
    private Long score;
    private LocalDateTime scoreUpdatedAt;
}

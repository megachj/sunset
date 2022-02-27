package sunset.spring.web_api.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import sunset.spring.web_api.service.MyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("/api/v1")
@RequiredArgsConstructor
@Tag(name = "멤버 관련 API")
public class MyController {

    private final MyService myService;

    @GetMapping("/members/{id}/name")
    @Operation(summary = "멤버 아이디로 이름 조회")
    public ResponseEntity<String> getMemberName(@Parameter(description = "멤버 아이디") @PathVariable("id") int id) {
        return new ResponseEntity<>("TODO", HttpStatus.OK);
    }

    @GetMapping("/members/{id}/history")
    @Operation(summary = "멤버 이력 조회")
    public ResponseEntity<HistoryInfo> getHistory(@Parameter(description = "멤버 아이디") @PathVariable("id") int id) {
        HistoryInfo historyInfo = new HistoryInfo("더미", LocalDate.now(), LocalDateTime.now(), ZonedDateTime.now());
        return new ResponseEntity<>(historyInfo, HttpStatus.OK);
    }

    @PutMapping("/members/{id}/name")
    @Operation(summary = "멤버 이름 변경")
    public ResponseEntity<Void> updateMemberName(@Parameter(description = "멤버 아이디") @PathVariable("id") int id,
                                                 @RequestBody @Valid UpdateMemberNameRequest request) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @Data
    @Schema
    public static class UpdateMemberNameRequest {
        @Schema(title = "멤버 아이디", example = "1000")
        @NotNull
        private Integer id;

        @Schema(title = "변경할 이름")
        @NotEmpty
        private String changedName;
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @Value(staticConstructor = "of")
    public static class HistoryInfo {
        private final String name;
        private final LocalDate date;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private final LocalDateTime dateTime;
        private final ZonedDateTime zonedDateTime;
    }
}

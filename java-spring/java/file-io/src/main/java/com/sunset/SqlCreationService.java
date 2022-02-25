package com.sunset;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SqlCreationService {

    private static final String ACCOUNT_PREFIX =
        " INSERT INTO account (account_id, name, created_at, updated_at) VALUES \n";

    private static final String ACCOUNT_VALUE = " (%d, '%s', '%s', '%s');";

    private static ObjectMapper objectMapper = new ObjectMapper();

    public Path createDummySqlFile() {
        // resources(여기선 data 디렉토리도 포함) 에 있는 데이터 불러오기
        URL inputDataUrl = Main.class.getClassLoader().getResource("input/input.json");
        System.out.println("input data: " + inputDataUrl.getPath());

        // json 파일 읽기
        List<Account> accounts = readJsonFile(inputDataUrl);

        // sql 문 생성
        List<String> sqlList = createSqlList(accounts);

        // sql 파일 쓰기
        Path outputFile = writeFileOutput(findUserDir(), sqlList);
        System.out.println("output file: " + outputFile);

        return outputFile;
    }

    private List<Account> readJsonFile(URL jsonFileUrl) {
        try {
            return objectMapper.readValue(jsonFileUrl, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("json 파일 읽기 실패", e);
        }
    }

    private List<String> createSqlList(List<Account> accounts) {
        return accounts.stream()
            .map(account ->
                ACCOUNT_PREFIX + String.format(ACCOUNT_VALUE, account.getAccountId(), account.getName(),
                    "null",
                    "null")
            )
            .collect(Collectors.toList());
    }

    private String findUserDir() {
        String userDir = System.getProperty("user.dir"); // 'java -jar *.jar' 명령어를 실행한 경로(실행중인 jar 파일의 경로가 아님)
        String classPath = System.getProperty("java.class.path"); // class path
        System.out.println("userDir: " + userDir);
        System.out.println("classPath: " + classPath);

        return userDir;
    }

    private Path writeFileOutput(String userDir, List<String> sqlList) {
        try {
            String directory = userDir + "/output/";
            String fileName = "output.sql";

            Path directoryPath = Paths.get(directory);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
                System.out.println("Directory is created: " + directory);
            }

            return Files.write(Paths.get(directory, fileName), sqlList, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE);
        } catch (Exception e) {
            throw new RuntimeException("결과 파일 쓰기 실패", e);
        }
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @NoArgsConstructor
    @Data
    public static class Account {

        private long accountId;
        private String name;
    }
}

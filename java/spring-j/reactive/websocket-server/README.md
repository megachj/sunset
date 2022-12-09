# 내용
- spring reactive(spring-boot-starter-webflux) 로 웹소켓 일대일 채팅 서비스 구현
- spring reactive(spring-boot-starter-webflux) 로 웹소켓 API 서비스 구현

## 1. 웹소켓 일대일 채팅 서비스
경로: ws://localhost:8080/chat/open

웹소켓 메시지 포맷은 아래와 같다.
### 발신 메시지 포맷
```text
{to_user_id}
{content}
```

### 수신 메시지 포맷
```text
from: {user_id}
{content}
```

## 2. 웹소켓 API 서비스
경로: ws://localhost:8080/api/open

웹소켓 API 메시지 포맷은 아래와 같다.
### 메시지 포맷
```text
COMMAND

BODY
```
- COMMAND: API 명령어
- BODY: 해당 명령어에 해당하는 바디(json 포맷)

# 웹소켓 1대1 채팅 서비스
경로: ws://localhost:8080/chat/open

## 1) 메시지 포맷
### 보내는 메시지
```text
{to_user_id}
{content}
```

### 받는 메시지
```text
from: {user_id}
{content}
```

# 웹소켓 API 서비스
경로: ws://localhost:8080/api/open

## 1) 메시지 포맷
```text
COMMAND

BODY
```
- COMMAND: API 명령어
- BODY: 해당 명령어에 해당하는 바디(json 포맷)

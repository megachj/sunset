## 1. 실행 방법
1. 와이어샤크를 실행해, log1 파일을 연다.
2. Display Filter 에 'tcp.port == 57602' 를 추가한다.

## 2. 테스트 과정
1. 웹소켓 서버 애플리케이션(8080) 실행.
2. 웹소켓 클라이언트에서 서버로 커넥션 요청. (ws://localhost:8080/chat)
3. 커넥션 연결이 완료되면, 클라에서 서버로 메시지 전송. (하이\n)

## 3. 테스트 환경
- OS: 윈도우 10
- 서버: 인텔리제이에서 스프링 부트 실행, localhost(127.0.0.1)
- 클라: Simple Websocket Client 크롬 플러그인, localhost(127.0.0.1)
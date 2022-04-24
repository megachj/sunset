## 1. 실행 방법
1. 와이어샤크를 실행해, log2 파일을 연다.
2. Display Filter 에 'tcp.port == 53743 || tcp.port == 53744' 를 추가한다.

## 2. 테스트 과정
1. nginx(80) 실행.
2. 웹소켓 서버 애플리케이션(8080) 실행.
3. 웹소켓 클라이언트에서 서버로 커넥션 요청. (ws://localhost/chat)
4. 커넥션 연결이 완료되면, 클라에서 서버로 메시지 전송. (하이)

## 3. 테스트 환경
- OS: 윈도우 10
- nginx: 윈도우 10용 설치후 실행
- 서버: 인텔리제이에서 스프링 부트 실행, localhost(127.0.0.1)
- 클라: Simple Websocket Client 크롬 플러그인, localhost(127.0.0.1)

### nginx 실행하는 법
```
# 실행
nginx.exe 실행

# 종료 (https://harrydony.tistory.com/665)
tasklist /fi "imagename eq nginx.exe"
taskkill /pid <pid> /f
```

### nginx 웹소켓 리버스 프록시 설정
```
# nginx.conf 에서 server 괄호 안에 아래를 추가. https://hyeon9mak.github.io/nginx-web-socket-proxy-configuration/
location /chat {
    proxy_pass http://localhost:8080/chat;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
    proxy_set_header Host $host;
}
```
# 실행 방법
## 도커 컴포즈 실행
```sh
# 0. DB 데이터 삭제(선택 사항)
sh ./delete-volume.sh

# 1. 도커 컴포즈 실행
docker-compose up -d {--build}

# 2. 도커 컴포즈 종료
docker-compose down
```
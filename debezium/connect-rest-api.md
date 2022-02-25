## Kafka Connect REST API

### Documents

https://docs.confluent.io/platform/current/connect/references/restapi.html

### Connect Cluster API

* 카프카 클러스터 정보 조회: GET /

### Connectors API

* 커넥터 리스트 조회: GET /connectors
* 커넥터 등록: POST /connectors
* 커넥터 단일 조회: GET /connectors/{connectorName}
* 커넥터 컨피그 조회: GET /connectors/{connectorName}/config
* 커넥터 상태 조회: GET /connectors/{connectorName}/status
* 커넥터 재시작: POST /connectors/{connectorName}/restart
* 커넥터 일시중지: PUT /connectors/{connectorName}/pause
* 커넥터 재개: PUT /connectors/{connectorName}/resume
* 커넥터 삭제: DELETE /connectors/{connectorName}

### Tasks API

* 태스크 리스트 조회: GET /connectors/{connectorName}/tasks
* 태스크 상태 조회: GET /connectors/{connectorName}/tasks/{taskId}/status
* 태스크 재시작: POST /connectors/{connectorName}/tasks/{taskId}/restart

### Example

```bash
# 커넥터 등록
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" localhost:8083/connectors/ -d @mysql-source-connector.json

# 커넥터 상태 조회
curl -X GET host:port/connectors/{connectorName}/status

# 커넥터 일시중지
curl -X PUT host:port/connectors/{connectorName}/pause

# 커넥터 삭제
curl -X DELETE host:port/connectors/{connectorName}
```

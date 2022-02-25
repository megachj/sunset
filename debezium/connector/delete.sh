#!bin/bash
curl -X PUT localhost:8083/connectors/mysql-source-connector/pause && curl -X DELETE localhost:8083/connectors/mysql-source-connector

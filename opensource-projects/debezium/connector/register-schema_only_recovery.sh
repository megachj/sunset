#!bin/bash
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" localhost:8083/connectors/ -d @connector/connector-schema_only_recovery.json

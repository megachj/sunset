#!bin/bash
docker network create --gateway 172.254.0.1 --subnet 172.254.0.0/16 debezium-docker-net

# kafka connect 용 토픽 생성
/kafka/bin/kafka-topics.sh --zookeeper zookeeper:2181/kafka --replication-factor 1 --partitions 1 --topic connect.configs --create
/kafka/bin/kafka-topics.sh --zookeeper zookeeper:2181/kafka --replication-factor 1 --partitions 1 --topic connect.offsets --create
/kafka/bin/kafka-topics.sh --zookeeper zookeeper:2181/kafka --replication-factor 1 --partitions 1 --topic connect.statuses --create

/kafka/bin/kafka-configs.sh --zookeeper zookeeper:2181/kafka --alter --entity-type topics --entity-name connect.configs  --add-config cleanup.policy=compact
/kafka/bin/kafka-configs.sh --zookeeper zookeeper:2181/kafka --alter --entity-type topics --entity-name connect.offsets  --add-config cleanup.policy=compact
/kafka/bin/kafka-configs.sh --zookeeper zookeeper:2181/kafka --alter --entity-type topics --entity-name connect.statuses --add-config cleanup.policy=compact

# debezium source connector 용 토픽 생성
## DB History 토픽은 무제한 보존이 필요, 파티션도 1 이어야 문제가 없는 것 같음
/kafka/bin/kafka-topics.sh --zookeeper zookeeper:2181/kafka --replication-factor 1 --partitions 1 --topic moneydb_server.dbhistory-money --create
/kafka/bin/kafka-configs.sh --zookeeper zookeeper:2181/kafka --alter --entity-type topics --entity-name moneydb_server.dbhistory-money --add-config retention.ms=-1
/kafka/bin/kafka-configs.sh --zookeeper zookeeper:2181/kafka --alter --entity-type topics --entity-name moneydb_server.dbhistory-money --add-config retention.bytes=-1

## database.server.name 토픽, include.schema.changes 옵션을 true 로 사용할 때 필요
# /kafka/bin/kafka-topics.sh --zookeeper zookeeper:2181/kafka --replication-factor 1 --partitions 3 --topic moneydb_server --create

## tables 토픽 ({database.server.name}.{논리database명}.{table명})
/kafka/bin/kafka-topics.sh --zookeeper zookeeper:2181/kafka --replication-factor 1 --partitions 3 --topic moneydb_server.moneydb.user --create
/kafka/bin/kafka-topics.sh --zookeeper zookeeper:2181/kafka --replication-factor 1 --partitions 3 --topic moneydb_server.moneydb.tx_event --create

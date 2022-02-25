FROM debezium/connect:1.5
LABEL MAINTAINER="sunset.choe"

RUN curl https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.15.0/jmx_prometheus_javaagent-0.15.0.jar > /kafka/jmx_prometheus_javaagent.jar
COPY metrics-config.yml /kafka/metrics-config.yml

COPY custom-converter.jar /kafka/connect/debezium-connector-mysql/
FROM prom/prometheus:v2.25.2

RUN sed -i -e "s/'localhost:9090'/'localhost:9090','connect1:8080','connect2:8080'/" /etc/prometheus/prometheus.yml

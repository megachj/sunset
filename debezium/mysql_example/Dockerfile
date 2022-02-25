FROM mysql:5.7
LABEL maintainer="sunset.choe"

COPY mysql.cnf /etc/mysql/conf.d/
COPY init-db.sql /docker-entrypoint-initdb.d/

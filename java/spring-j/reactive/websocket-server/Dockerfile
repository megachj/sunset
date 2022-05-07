FROM openjdk:11.0.15-oraclelinux8
LABEL MAINTAINER="sunset"

VOLUME /tmp
ARG JAR_FILE
COPY --chown=deploy:deploy ${JAR_FILE} app.jar

ENV TZ=Asia/Seoul

ENTRYPOINT exec java $JAVA_OPTS -jar ./app.jar
CMD []

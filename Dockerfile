FROM gradle:4.2.1-jdk8-alpine

LABEL maintainer="alejandro.hernandez@liferay.com"

EXPOSE 8080 6666

WORKDIR /sample

COPY / /sample

USER root

RUN chown -R gradle /sample

USER gradle

ENTRYPOINT ["./gradlew", "run"]
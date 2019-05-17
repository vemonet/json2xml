FROM maven:3.6.0-jdk-8

LABEL maintainer="Lukáš Křečan <lukas@krecan.net>"

WORKDIR /usr/src/json2xml

COPY . .

RUN mvn clean install

### entrypoint

ENTRYPOINT ["java", "-jar", "target/json-xml-4.3-SNAPSHOT-jar-with-dependencies.jar"]

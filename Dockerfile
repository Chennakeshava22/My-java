FROM maven:3-eclipse-temurin-17-noble AS builder
MAINTAINER Chennakeshava reddy busupalle
LABEL this is the java based static web application
WORKDIR /java
COPY pom.xml .
COPY ./src .
RUN mvn clean install -DskipTests



FROM tomcat:8.0.20-jre8
RUN rm -rf /usr/local/tomcat/webapps/*
COPY ./tomcat-users.xml /usr/local/tomcat/conf/
COPY --from=builder /java/target/*.war /usr/local/tomcat/webapps/

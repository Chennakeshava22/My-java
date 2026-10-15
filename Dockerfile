FROM maven:3-eclipse-temurin-17-noble AS builder
MAINTAINER Chennakeshava reddy busupalle
LABEL this is the java based static web application
WORKDIR /java
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests



FROM tomcat:9.0.123-jdk17-temurin-noble
RUN rm -rf /usr/local/tomcat/webapps/*
COPY ./tomcat-users.xml /usr/local/tomcat/conf/
COPY --from=builder /java/target/*.war /usr/local/tomcat/webapps/ROOT.war

FROM openjdk:8

RUN apt-get update && apt-get install lsof -qy

WORKDIR /

ADD attachme-agent.jar EchoService.java ./
RUN javac EchoService.java

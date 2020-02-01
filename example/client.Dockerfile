FROM openjdk:8

RUN apt-get update && apt-get install lsof -qy

WORKDIR /

ADD attachme-agent.jar Client.java ./
RUN javac Client.java

FROM openjdk:8

RUN apt-get update && apt-get install lsof -qy

WORKDIR /app
COPY build/libs/attachme-agent.jar .
COPY src/test/resources/ForkBomb.java .

# Notifies the host at port 9090, debugger attaches to the container port 9091, so need to expose 9091 inside the host to be able to debug
CMD ["bash", "-c", "cd /app && javac ForkBomb.java && NO_WAIT=true JAVA_TOOL_OPTIONS='-javaagent:attachme-agent.jar=port:9090,host:172.17.0.1 -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=127.0.0.1:9091' java ForkBomb 0" ]

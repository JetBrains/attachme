JDWP_ARGS="${JDWP_ARGS:=transport=dt_socket,server=y,suspend=y,address=*:0}"
echo "Using JDWP arguments ${JDWP_ARGS}"
export JAVA_TOOL_OPTIONS="-javaagent:${HOME}/.attachme/attachme-agent-0.0.3.jar -agentlib:jdwp=${JDWP_ARGS}"

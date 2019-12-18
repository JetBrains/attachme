JDWP_ARGS="${JDWP_ARGS:=transport=dt_socket,server=y,suspend=y,address=*:0}"
AM_PORT="${AM_PORT:=7857}"
echo "Picked up JDWP arguments ${JDWP_ARGS}"
export JAVA_TOOL_OPTIONS="-javaagent:${HOME}/.attachme/attachme-agent-0.0.3.jar=port:${AM_PORT} -agentlib:jdwp=${JDWP_ARGS}"

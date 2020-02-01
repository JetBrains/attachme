VERSION="@ATTACHME_VERSION@"

JDWP_ADDR="${JDWP_ADDR:=127.0.0.1}"
JDWP_PORT="${JDWP_PORT:=0}"
JDWP_ARGS="${JDWP_ARGS:=transport=dt_socket,server=y,suspend=y,address=${JDWP_ADDR}:${JDWP_PORT}}"

AM_PORT="${AM_PORT:=7857}"
AM_JAR="${AM_JAR:=${HOME}/.attachme/attachme-agent-${VERSION}.jar}"
AM_HOST="${AM_HOST:=localhost}"

echo "Picked up JDWP arguments ${JDWP_ARGS}"
export JAVA_TOOL_OPTIONS="-javaagent:${AM_JAR}=port:${AM_PORT},host:${AM_HOST} -agentlib:jdwp=${JDWP_ARGS}"

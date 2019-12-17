#!/usr/bin/env bash
set -e

INSTALL_DIR="${INSTALL_DIR:=${HOME}/.config/attachme}"
mkdir -p "${INSTALL_DIR}"
AGENT_PATH="${INSTALL_DIR}/agent.jar"
if [ ! -f "${AGENT_PATH}" ]; then
  echo "Downloading agent... ${AGENT_PATH}"
  wget -q -O "${AGENT_PATH}" "https://github.com/samvel1024/attachme/releases/download/agent-0.0.1/attachme-agent.jar"
fi
if [ ! -f "${HOME}/.attachme" ]; then
  {
    echo "AM_JDWP_ARGS=\"\${JDWP_ARGS:=transport=dt_socket,server=y,suspend=y,address=*:0}\""
    echo "echo Using JDWP arguments \${AM_JDWP_ARGS}"
    echo "export JAVA_TOOL_OPTIONS=\"-javaagent:${AGENT_PATH} -agentlib:jdwp=\${AM_JDWP_ARGS}\""
    echo "echo \"AttachMe configured successfully\""
  } >> "${HOME}/.attachme"
fi


cat << EOF
AttachMe installed successfully
Usage example:
   #(Optional) export JDWP_ARGS="transport=dt_socket,server=y,suspend=n,address=127.0.0.1:0"
   source ~/.attachme
   java com.example.Application
EOF
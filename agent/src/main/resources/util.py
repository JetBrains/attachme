# This script looks for the ports in its parent process and sends
# those ports numbers along with the parent pid to a socket localhost:env['ATTACHME_PORT'].
# The presence of env variable 'ATTACHME_PORT' is required
import json
import os
import psutil
import socket
import time

# Backwards compatible way for getting parent pid
parent_pid = psutil.Process(os.getpid()).ppid()
parent_proc = psutil.Process(parent_pid)
# Get parents open ports
ports = []
# Poll for 2*max millis
wait_ms = 1
max = 32
while len(ports) is 0 and wait_ms <= max:
    time.sleep(wait_ms / 1000)
    ports = [x.laddr.port for x in parent_proc.connections()]
    wait_ms *= 2
if len(ports) > 0:
    json_str = json.dumps({'ports': ports, 'pid': parent_pid})
    print("Detected debugger for pid " + str(parent_pid) + " on one of the ports " + str(ports))
    # Send with a tcp socket
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect(('127.0.0.1', int(os.environ['ATTACHME_PORT'])))
        s.sendall(json_str.encode())
else:
    print("No debugger detected")

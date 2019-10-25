import json
import os
import psutil
import socket

parent_pid = psutil.Process(os.getpid()).ppid()
list = psutil.net_connections()
ports = []
for row in list:
    if str(row.pid) == str(parent_pid):
        ports.append(str(row.laddr.port))

json_str = json.dumps({'ports': ports, 'pid': parent_pid})
print("Detected open ports " + json_str)

HOST = '127.0.0.1'
PORT = 7857

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((HOST, PORT))
    s.sendall(json_str.encode())

import json
import os
import psutil


parent_pid = psutil.Process(os.getpid()).ppid()
list = psutil.net_connections()
ports = []
for row in list:
    if str(row.pid) == str(parent_pid):
        ports.append(str(row.laddr.port))

json_str = json.dumps({'socketPorts': ports, 'pid': parent_pid});
print("forkattach:" + json_str)

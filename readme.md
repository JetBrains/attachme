## AttachMe - IntelliJ debugger plugin

- AttachMe will attach the IntelliJ debugger automatically even if you start your app from terminal (or any other way).
You don't need to trigger `Attach to process` action manually.
- If you have a complex process hierarchy with many parent/child processes,
AttachMe can auto-attach to all the newly forked child JVM processes.
The behaviour will be similar to `set follow-fork-mode child` in the GDB debugger.


![](demo.gif)

#### Installation and usage

1. Download and install the plugin from IntelliJ ([link](https://plugins.jetbrains.com/plugin/13263-attachme/))
2. Download the JVM agent library and unzip it. Place the `attachme-agent.jar` in an accessible location ([link](https://github.com/samvel1024/attachme/releases/latest/downloads/attachme-agent.zip))
3. Verify that you have python3 installed.
4. Install `psutil` python library. (`pip install psutil`)
5. Start the AttachMe listener by going to ` Run > Edit Configurations > Add New` . Then search for `Attachme`, select it and run.
6. Now that you see the AttachMe window with the `AttachMe listening ...` message, you are ready to start your debuggee java process.
The idea is to configure AttachMe agent to run before the debugger (JDWP) agent (the order of agent args is important if you have `suspend=y` for jdwp).
A possible way of configuration can be through the `JAVA_TOOL_OPTIONS` env variable. 

``` bash
ATTACHME="/<YOUR_PATH_HERE>/attachme-agent.jar
export JAVA_TOOL_OPTIONS="-javaagent:${ATTACHME}=python:$(which python3) -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:0"
java com.example.MyProgram
```

#### Notes

- The port of the AttachMe listener can be changed in the `Run Configuration` settings inside IntelliJ
- The attachme agent can receive additional optional arguments.
```
-javaagent:/Users/sme/forkattach/agent/build/libs/agent.jar=python:/usr/bin/python3,port:8080`
```
- If you want to auto-attach to any newly forked child JVM process then you have to use environment variable `JAVA_TOOL_OPTIONS` and set 
JDWP to listen to port 0 (possibly by `address=*:0`).
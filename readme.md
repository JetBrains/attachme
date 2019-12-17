## AttachMe - IntelliJ debugger plugin

- AttachMe will attach the IntelliJ debugger automatically even if you start your app from terminal (or any other way).
You don't need to trigger `Attach to process` action manually.
- If you have a complex process hierarchy with many parent/child processes,
AttachMe can auto-attach to all the newly forked child JVM processes.
The behaviour will be similar to `set follow-fork-mode child` in the GDB debugger.


![](demo.gif)

#### Installation and usage

1. Download and install the plugin from IntelliJ ([link](https://plugins.jetbrains.com/plugin/13263-attachme/))
2. Run the agent installer script
```
curl -sSL https://raw.githubusercontent.com/JetBrains/attachme/master/installer.sh | sh
```
3. Start the AttachMe listener by going to ` Run > Edit Configurations > Add New` . Then search for `Attachme`, select it and run.
4. Now that you see the AttachMe window with the `AttachMe listening ...` message, you are ready to start your debuggee java process.

``` bash
source ~/.attachme
java com.example.MyProgram
```

#### Notes

- The port of the AttachMe listener can be changed in the `Run Configuration` settings inside IntelliJ
- The attachme agent can receive additional optional arguments.
```
-javaagent:/Users/sme/forkattach/agent/build/libs/agent.jar=port:8080`
```
- If you want to auto-attach to any newly forked child JVM process then you have to use environment variable `JAVA_TOOL_OPTIONS` and set 
JDWP to listen to port 0 (possibly by `address=*:0`).
- Not all operating systems are tested thoroughly. Please report an issue if you find a strange behaviour.

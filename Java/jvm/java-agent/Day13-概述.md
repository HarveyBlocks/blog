# Java Agent

1.  诊断类工具
    -   Arthas
    -   VisualVM
2.  开发类工具
    -   Idea
    -   Eclipse
3.  APM应用性能检测工具
    -   Skywalking
    -   Zipkin
4.  热部署工具
    -   Jrebel

## 加载模式

### 静态加载

在程序一开始就执行代码

适合APM等性能检测系统从一开始就监控程序的执行性能

```java
public static void premain(String agentArgs, Instrumentation inst){
    
}
```

启动Java-agent

```shell
 java -javaagent:.\target\test-agent-1.0-SNAPSHOT-jar-with-dependencies.jar -jar .\spring-demo\target\spring-demo-0.0.1-SNAPSHOT.jar
```



### 动态加载

随时让Java agent代码实行

适用于诊断系统

启动JavaAgent的Main

```java
public static void agentmain(String agentArgs, Instrumentation inst)
        throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
}
```

启动动态加载的Main

```java
public class AttachMain {
    public static void main(String[] args)
            throws IOException, AttachNotSupportedException,
            AgentLoadException, AgentInitializationException {
        String pid = "17088";
        
        String agentJarPath = "D:\\IT_study\\source\\JDK\\test-agent\\" +
                "target\\test-agent-1.0-SNAPSHOT-jar-with-dependencies.jar";
        VirtualMachine.attach(pid).loadAgent(agentJarPath);
    }
}
```


# 自定义链路追踪

本来力度是服务与服务之间,  现在想要服务内的追踪?

## 引入依赖



```xml
<!--自定义链路追踪-->
<dependency>
    <groupId>org.apache.skywalking</groupId>
    <artifactId>apm-toolkit-trace</artifactId>
    <version>8.14.0</version>
</dependency>
```



## API

### Trace上下文

```java
// trace id
System.out.println("TraceContext.traceId() = " + TraceContext.traceId());
// 存储标签信息
Optional<String> optional = TraceContext.putCorrelation("key", "value");

String msg = optional.orElse("没有的情况, 可以为null");
System.out.println("msg = " + msg);
// 获取数据
Optional<String> valueOptional = TraceContext.getCorrelation("key");
String value = valueOptional.orElse("没有的情况, 可以为null");
System.out.println("value = " + value);
```
### Trace标记

```java
@Trace(operationName = "self-trace")
@Tags({
        @Tag(key = "the first param", value = "arg[0]"), // arg[0] 自动拿到参数
        @Tag(key = "the return value", value = "returnedObj") // returnedObj 自动拿到返回对象
})
public static void extracted(int num) {
	// do something
}
```

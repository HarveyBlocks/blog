# SpringBoot集成gRPC

## 依赖

引入Spring依赖(当然protobuf解析的放在一个api模块)

```xml
<dependency>
    <groupId>net.devh</groupId>
    <artifactId>grpc-server-spring-boot-starter</artifactId>
    <version>2.14.0.RELEASE</version>
</dependency>
<dependency>
    <groupId>net.devh</groupId>
    <artifactId>grpc-server-spring-boot-autoconfigure</artifactId>
    <version>2.14.0.RELEASE</version>
</dependency>
```

## API模块

为了防止API模块里的版本冲突, 设置API模块里的依赖为

```xml
<dependency>
    <groupId>net.devh</groupId>
    <artifactId>grpc-server-spring-boot-autoconfigure</artifactId>
</dependency>
<!--↓这个依赖是autoconfig里没有的-->
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-stub</artifactId>
    <version>1.63.0</version>
</dependency>
```

## 服务端

### 配置

```yaml
grpc:
  server:
    address: localhost
    port: 9090
```

检查一下Spring的Tomcat有没有启动, 不想要就关掉

```yaml
spring:
  application:
    name: grpc-server
  main:
    web-application-type: none # 不启动tomcat
```

把tomcat依赖也关一下掉

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

很奇怪, 我测试出来发现tomcat的服务器启动没有打印日志, 但其实还是开启了, 用浏览器测试

### 代码

```java
@GrpcService // 开启gRPC调用
@Slf4j
public class UserServiceImpl extends UserServiceImplBase {
    @Override
    public void login(LoginDto request, StreamObserver<UserDto> responseObserver) {
        log.debug("login, {}", request);
        try {
            responseObserver.onNext(UserDto.newBuilder().build());
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void queryById(Id request, StreamObserver<UserDto> responseObserver) {
        log.debug("login, {}", request);
        try {
            responseObserver.onNext(UserDto.newBuilder().build());
        } finally {
            responseObserver.onCompleted();
        }
    }

}
```

## 客户端

### 引入依赖

```xml
<dependency>
    <groupId>net.devh</groupId>
    <artifactId>grpc-client-spring-boot-autoconfigure</artifactId>
    <version>${grpc.version}</version>
</dependency>
```

那api里的依赖是否也能有所改进? 

### 配置文件

```yaml
spring:
  application:
    name: grpc-client

grpc:
  client:
    user-server: # 这给`user-server`算是一个变量, 需要做注解的参数
      address: static://localhost:9090
      negotiation-type: plaintext
  server:
    port: 0 # 随机端口, 不配置默认9090, 注意端口冲突的可能

```

有没有必要关闭Tomcat? 看情况吧

### 代码

```java
@GrpcClient("user-server") // 来自配置文件
private UserServiceBlockingStub userService;

public static void main(String[] args) {
    ClientApplication mianBean = SpringApplication.run(ClientApplication.class, args).getBean(ClientApplication.class);
    UserDto userDto = mianBean.userService.queryById(Id.newBuilder().setId(12).build());
    log.info("userDto is : {}", userDto);
}
```


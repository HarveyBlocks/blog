# 实现ProtoBuf生成的API

>   可以让别人来调用我们实现的方法的时候执行正确的逻辑

## 流程

-   创建Service类
-   继承`[ServiceName]ImplBase`
-   实现里边的方法

## 服务端

gRPC使用Netty来创建服务端

### 继承

```java
package com.harvey.grpc.server.service;

import io.grpc.stub.StreamObserver;

import java.util.Map;

import static com.harvey.grpc.proto.UserServiceGrpc.*;
import static com.harvey.grpc.proto.UserProto.*;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-04-13 16:05
 */
public class UserService extends UserServiceImplBase {
    private final UserIService userIService = new UserIService();
    @Override
    public void login(LoginDto request,
                      StreamObserver<UserDto> response)  {
        // 采用两个参数,没有返回值
        // 其实是因为gRpc采用观察者模式来进行api的创建
        // 请求的时候, 用流多次传数据, 响应也用流多次响应数据
        // 这个流怎么来呢? 用参数传给实现者
        try {
            UserDto login = userIService.login(request);
            // 回传数据
            response.onNext(login);
        }catch (Exception e){
            response.onError(e);
        }finally {
            // 表示事情做完了
            response.onCompleted();
        }
    }

    @Override
    public void queryById(Id request, StreamObserver<UserDto> response) {
        try {
            UserDto queryById = userIService.queryById(request.getId());
            response.onNext(queryById);
        }catch (Exception e){
            response.onError(e);
        }finally {
            // 表示事情做完了
            response.onCompleted();
        }
    }

}
```

客户端监听服务端发送的完成消息

服务端的完成消息由

```java
response.onCompleted();
```

调用完成

如果客户端监听不到这个消息, 或者说, 如果服务端不执行`onComplete()`方法, 客户端就会一直监听

### 实现业务逻辑

```java
class UserIService{
    /**
     * 虚假的数据库
     */
    private final Map<Long, String> userMap = Map.of(
            1L, "张三",
            2L, "李四",
            3L, "王五"
    );

    public UserDto login(LoginDto loginDtoMsg) throws Exception{
        // 创建响应对象的构建者
        UserDto.Builder builder = UserDto.newBuilder();

        String username = loginDtoMsg.getUsername();
        String password = loginDtoMsg.getPassword();
        // username和password似乎永远不会为null
        if (username.equals(password)) { // 密码正确
            // 填充数据
            builder.setIcon(username);
            builder.setIcon(username);
            Long id = -1L;
            for (Long key : userMap.keySet()) {;
                if (userMap.get(key).equals(username)){
                    // 用户存在
                    id = key;
                    break;
                }
            }
            builder.setId(id);
            // 封装对应的响应
            return builder.build();
        } else {
            // 密码错误
            throw new IllegalAccessError("密码错误");
        }
    }

    public UserDto queryById(long id) {
        String username = userMap.get(id);

        // 创建响应对象的构建者
        UserDto.Builder builder = UserDto.newBuilder();

        // 填充数据
        builder.setIcon(username);
        builder.setId(id);
        builder.setNickName(username);

        // 封装对应的响应
        return builder.build();
    }
}
```

### 开启服务端

```java
public static void main(String[] args) {
    // 绑定端口
    ServerBuilder<?> serverBuilder = ServerBuilder.forPort(8080);
    // 发布服务
    serverBuilder.addService(new UserService());
            // .addService();
    // 创建服务对象
    Server server = serverBuilder.build();
    try {
        server.start();
        server.awaitTermination(); // 阻塞等待请求
    } catch (IOException | InterruptedException e) {
        throw new RuntimeException(e);
    }

}
```


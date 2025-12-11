# gRpc通信方法

-   简单RPC/一元RPC
    -   Unary RPC
-   服务端流式RPC
    -   Server Streaming RPC
-   客户端流式RPC
    -   Client Streaming RPC
-   双向流RPC
    -   Bi-direcional RPC

不同的通信方式导致生成的代码不同, 有时候只有特定的代理(BlockingStub/FutureStub/Stub)才能调用该方法, 有时候不正确的代理不能调用通信方式不会口的方法, 因为没有生成

## Unary RPC

Client-Server

点对点

客户端阻塞等待服务端响应

```protobuf
service UserService{
  rpc login(LoginDto) returns(UserDto){};
  rpc queryById(Id) returns(UserDto){};
}
```

## Server Streaming RPC

一个请求对象, 服务端可以回传多个结果对象

结果对象是分批分期传输的

两个对象需要长期连接, 知道服务端打上结束的标记

例如垃圾百度云的下载

### ProtoBuf语法

```protobuf
service UserService{
  rpc login(LoginDto) returns(stream UserDto){};
  rpc queryById(Id) returns(stream UserDto){};
}
```

### Java

#### 服务端

```java
try {
    P apply = mapper.apply(request);
    int i = 9;
    while (i-->0) {
        result = // 业务逻辑
        response.onNext(result);
        Thread.sleep(500); // 方便测试
    }
} catch (Exception e) {
    response.onError(e);
} finally {
    response.onCompleted();
}
```

#### 客户端

```java
Iterator<UserDto> iterator = userService.login(builder.build());
while (iterator.hasNext()){
    UserDto next = iterator.next();
    System.out.println(next.getId());
}
```

经测试, 每次传完next之后, 都会使客户端接收到消息, 即hasNext不再阻塞, 返回true

## Client Streaming RPC

IOT物联网传感器传输数据就是一段一段的

### ProtoBuf语法

```protobuf
service UserService{
  rpc login(stream LoginDto) returns(UserDto){};
  rpc queryById(stream Id) returns(UserDto){};
}
```

### Java

#### 服务端

服务端需要根据客户端传来的请求各有处理, 其方法就是返回一个针对响应的策略

```java
@Override
public StreamObserver<Id> queryById(StreamObserver<UserDto> response) {
    // 监控请求的发送, 返回监控这些返回值的策略
    return new StreamObserver<>() {
        final List<String> results = new ArrayList<>();
        @Override
        public void onNext(Id id) {
            System.out.println("服务端接收到数据 id = " + id.getId());
            try {
                System.out.println("服务端开始处理客户端的数据了");
                UserDto userDto = userIService.queryById(id.getId());
                System.out.println("服务端准备好了数据, 放入了List结果集合了");
                // response.onNext(userDto);// 这里不在Next中添加数据
                results.add(userDto.toString());
            } catch (Exception e) {
                System.out.println("服务端发生了异常");
                e.printStackTrace();
                response.onError(e);
            }
            // 由于只能回传一次, 这里不传回数据
        }
        @Override
        public void onError(Throwable t) {
            // 传输请求时发生异常(客户端发生异常)
            System.err.println("传输数据时发生了异常");
            t.printStackTrace();
        }
        @Override
        public void onCompleted() {
            // 数据传输完成
            System.out.println("服务端接收到所有数据, 传输数据完成了, 服务器也要断开连接了");
            UserDto userDto = UserDto.newBuilder().setNickName(results.toString()).build();
            // results.clear(); 不用调用这个, 实体类是同一个, 但这个方法每次代理都会调用一次,
            // 调用一次就创建一个新的客户请求端流实体, 新的客户端实体里的对象也成为新构建的了
            response.onNext(userDto);
            response.onCompleted();
        }
    };
}
```

#### 客户端

客户端异步接收响应, 创建响应策略, 面对不同的响应怎么做?

```java
private static StreamObserver<UserDto> createUserDtoStreamObserver() {
    // 响应策略
    return new StreamObserver<>() {
        @Override
        public void onNext(UserDto userDto) {
            System.out.println("服务端处理完毕, 返回了一个结果");
            System.out.println(userDto);
        }

        @Override
        public void onError(Throwable t) {
            System.err.println("服务端发生错误");
            t.printStackTrace();
        }

        @Override
        public void onCompleted() {
            System.out.println("服务端全部处理完毕");
        }
    };
}
```

客户端调用代理

```java
public static void doClient(ManagedChannel clientChannel) throws InterruptedException {
    // 获取客户端传输流, idStreamObserver让我们在后续传数据
    StreamObserver<Id> idStreamObserver = UserServiceGrpc.newStub(clientChannel) // 生成代理
            .queryById(createUserDtoStreamObserver()); // 不发送参数数据, 或许会发送调用的接口信息的数据, 也有可能不发
			// 将策略传入业务
    int i = 10;
    while (i-- > 0) {
        idStreamObserver.onNext(Id.newBuilder().setId(i).build()); // 传数据
        System.out.println("第" + i + "个数据发送完毕");
        clientChannel.awaitTermination(2, TimeUnit.SECONDS);
        // 扩大间隔, 方便观察
    }
    idStreamObserver.onCompleted();
    System.out.println("客户端全部数据发送给完毕, 客户端开始等待服务端发送的结果");
    clientChannel.awaitTermination(5, TimeUnit.SECONDS);
}
```

## Bi-direcional RPC

双方不定时地互相发数据

```protobuf
service UserService{
  rpc login(stream LoginDto) returns(stream UserDto){};
  rpc queryById(stream Id) returns(stream UserDto){};
}
```

与Client Streming RPC 唯一的区别:

 在服务端 , `onNext()`的回调函数处, 做了个循环, 表示随机地, 不定时地传回数据

```java
System.out.println("服务端接收到数据 id = " + id.getId());
try {
    UserDto userDto;

    int i = Randoms.nextInt(5) + 1;
    System.out.println("i = " + i);

    List<String> littleResults = new ArrayList<>();
    while (i-- > 0) {
        System.out.println("服务端开始处理客户端的数据了");
        userDto = userIService.queryById(id.getId() + i);

        System.out.println("服务端处理好数据, 将数据传会客户端");
        response.onNext(userDto);

        System.out.println("将数据放入了List结果集合了");
        results.add(userDto.toString());
        littleResults.add(userDto.toString());

        Thread.sleep(1000);
    }

    System.out.println("把一次请求多次循环的结果封装到List发给客户端");
    userDto = UserDto.newBuilder().setNickName(littleResults.toString()).build();
    response.onNext(userDto);
} catch (Exception e) {
    System.out.println("服务端发生了异常");
    e.printStackTrace();
    response.onError(e);
}
```

客户端可以在任意时刻发送信息给服务端, 服务端也可以在任意时刻发送数据给客户端

客户端和服务端互相不知道那个响应对应的是哪个请求

## 拦截器

### 一元拦截器

针对于一元RPC的拦截器

### Stream Tracer

监听流, 流拦截器

## Retry Policy

客户端重试机制

## NameResover

>   注册中心 

对于集群的管理

需要自己实现

注册中心中间件

-   consult
-   etcd

## 负载均衡

默认的策略: 

​	`pick-first` 第一次访问的服务

​	`round_robin`轮询

```yml
grpc:
  server:
    address: localhost
    port: 9090
  client:
    GLOBAL:
      default-load-balancing-policy: round_robin
```

## 微服务

grpc序列化protobuf+Dubbo

grpc+ Dubbo

grpc+Gateway

grpc+JWT

grpc+Nacos2.0

gRPC替换OpenFeign


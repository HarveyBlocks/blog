# 调用ProtoBuf生成的代理

>   这样我们就可以调用别人的代码了

## 导入

```java
import com.harvey.grpc.proto.UserProto;
import com.harvey.grpc.proto.UserProto.*;
```

这样就不用一直`UserProto.Id`之类地去调用了

## 生成Channel

```java
ManagedChannel clientChannel = null;
try {
    // 创建被gRPC封装的Channel
    clientChannel = ManagedChannelBuilder
            .forAddress("localhost", 8080)
            .usePlaintext().build();
    // 调用逻辑
} finally {
    if (clientChannel != null) {
        clientChannel.shutdown();
    }
}
```

## 调用代理

### 阻塞调用

```java
UserServiceBlockingStub userService = UserServiceGrpc.newBlockingStub(clientChannel);
```

```java
// 准备参数
Id id = Id.newBuilder().setId(1L).build();
// 注意, 如果message定义了repeated, 就使用addId();
UserDto login = userService.queryById(id);
System.out.println(login);
System.out.println(login.getNickName());
```

### 异步调用, 监听+回调

`UserServiceStub`, 这傻逼取名法

```java
UserServiceStub userFutureService = UserServiceGrpc.newStub(clientChannel);
userFutureService.login(loginDto, new StreamObserver<>() {
    @Override
    public void onNext(UserDto userDto) {
        System.out.println("userDto = " + userDto);
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void onCompleted() {
        System.out.println("结束了");
    }
});
clientChannel.awaitTermination(20,TimeUnit.SECONDS);
```

### Future异步获取结果

`UserServiceFutureStub`

只能应用于一元RPC

```java
UserServiceFutureStub userService = UserServiceGrpc.newFutureStub(clientChannel);
ListenableFuture<UserDto> queryFuture = userService.queryById(Id.newBuilder().setId(1L).build());

queryFuture.addListener(() -> {
    try {
        UserDto userDto = queryFuture.get();
        System.out.println("在监听器里获取的数据: " + userDto); // 可行
    } catch (InterruptedException | ExecutionException e) {
        System.err.println("不能再异步监听器里调用get获取返回值");
    }
    System.out.println("完成");
}, Executors.newSingleThreadExecutor()); // 添加回调函数
// 这里这么写是因为表示可以在这里用线程池做参数
if (queryFuture.isDone()) {
    System.out.println("早早地完成了");
    // 没有进入该分支
}

System.out.println("这里先输出表示这个addListener是异步");
// 再输出↑之后, get返回之间调用了上面的回调函数

UserDto userDto = queryFuture.get(); // 阻塞式
System.out.println("数据如下: " + userDto);
```

```test
这里先输出表示这个addListener是异步
在监听器里获取的数据: id: 1
icon: "\345\274\240\344\270\211"
nickName: "\345\274\240\344\270\211"

完成
数据如下: id: 1
icon: "\345\274\240\344\270\211"
nickName: "\345\274\240\344\270\211"
```

#### Futures工具类

```java
UserServiceFutureStub userService = UserServiceGrpc.newFutureStub(clientChannel);
ListenableFuture<UserDto> queryFuture = userService.queryById(Id.newBuilder().setId(1L).build());

Futures.addCallback(queryFuture, new FutureCallback<>() {
    @Override
    public void onSuccess(UserDto result) {
        System.out.println("成功, 返回结果: " + result);
    }

    @Override
    public void onFailure(Throwable t) {
        System.err.println("服务端发生错误啦");
        t.printStackTrace();
    }
}, Executors.newSingleThreadExecutor());
```


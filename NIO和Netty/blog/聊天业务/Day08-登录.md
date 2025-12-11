# 登录

## 客户端发送登录请求

法一: 

```java
ChannelFuture connect = bootstrap.connect("localhost", 8080);
Channel channel = connect.addListener((ChannelFutureListener) channelFuture -> {
    //发送登录请求
}).channel();
```

法二

```java
ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 连接建立后执行, 发送登录请求
        super.channelActive(ctx);
    }
}) ;
```

## 客户端

```java
public class ClientLoginHandler extends ChannelInboundHandlerAdapter {

    public static final Scanner SCANNER = new Scanner(System.in);
    private static final ExecutorService LOGIN_INPUT_EXECUTOR = Executors.newSingleThreadExecutor();
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 连接建立后执行, 发送登录请求
        LOGIN_INPUT_EXECUTOR.execute(
                ()->{
                    ctx.writeAndFlush(getLoginMessage());
                }
        ); // 要使用自创的多线程, 如果用一个新的DefaultEventLoopGroup, 还是和阻塞没区别, 异步了,但其他清流来还是会经过DefaultEventLoopGroup(即使这个请求不会用到DefaultEventLoopGroup,)
    }

    public static final Scanner SCANNER = new Scanner(System.in);

    private static LoginRequestMessage getLoginMessage() {
        LoginRequestMessage message;
        while (true) {
            System.out.print("用户名: ");
            String username = null;
            if (SCANNER.hasNext()) {
                username = SCANNER.next();
            }
            System.out.print("密码: ");
            String password = null;
            if (SCANNER.hasNext()) {
                password = SCANNER.next();
            }
            try {
                message = new LoginRequestMessage(username, password);
                // System.out.println(message);
                break;
            } catch (NullPointerException ignore) {
            }
        }
        return message;
    }

    public static class ResponseHandler extends SimpleChannelInboundHandler<LoginResponseMessage> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, LoginResponseMessage msg) throws Exception {
            if (msg.isSuccess()) {
                // 成功
                System.out.println(msg.getReason());
                ctx.fireChannelRead(msg);
            } else {
                System.err.println(msg.getReason());
                ctx.writeAndFlush(getLoginMessage());
            }
        }
    }
}
```

### 服务端

```java
public class ServiceLoginHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        UserService userService = UserServiceFactory.getUserService();
        Boolean login = userService.login(username,password);
        LoginResponseMessage respMsg;
        if(login == null){
            respMsg = new LoginResponseMessage(false,"该用户不存在");
        }else if(login) {
            respMsg = new LoginResponseMessage(true,"登录成功");
            SessionFactory.getSession().bind(ctx.channel(),username); 
            // 服务器保存用户, 方便下次服务器发送数据
        }else {
            respMsg = new LoginResponseMessage(false,"用户名或密码错误");
        }
        ctx.writeAndFlush(respMsg);
    }
}
```

## 平稳关闭

```java
/*
* 在使用右上角的关闭按钮
* 或调用 System.exit() 方法关闭程序时，
* Runtime.getRuntime().addShutdownHook() 注册的钩子线程仍然会被调用。
* 但是，
* 如果程序正常执行完成并退出时，
* 注册的钩子线程不会被调用。
* 注册的钩子线程主要是在 JVM 即将关闭时执行清理操作或执行特定的逻辑。
* */
Runtime.getRuntime().addShutdownHook(new Thread(group::shutdownGracefully));
```


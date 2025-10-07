# 责任链

![image-20240607163814675](../../assets/Day05-%E8%B4%A3%E4%BB%BB%E9%93%BE/image-20240607163814675.png)

甩锅链-不负责任链

或职责链模式

为了避免请求发送者与多个请求处理者耦合, 将所有的请求处理者通过前一对象记住其下一对象的引用而形成的链式结构

有请求发生时, 将请求沿这条链传递出去, 直到有对象处理它为止

降低了请求发送者和请求接收者之间的耦合

增强了系统的可拓展型

增强了给对象指派流程的灵活性, 增加流程和去掉流程也很方便

简化对象之间的连接, 避免众多的`if-else`

## 缺点

不能保证每一个请求都一定被处理

对比较长的职责链, 请求的处理可能涉及多个处理对象,系统性能收到一定的影响(还容易爆栈)

职责链的流程顺序控制交由客户端决定, 增加了客户端的复杂度, 可能会由于职责链的错误设置而导致系统出错, 如可能会造成循环调用

## 结构



-   抽象处理类
    -   Handler
    -   包含抽象处理方法和一个后继连接
-   具体处理者
    -   Concrete Handler
    -   实现抽象处理者类的处理方法
    -   判断能否处理本次请求, 能则处理, 不能则转给后继者
-   客户端
    -   Client
    -   创建处理链
    -   向链头的具体处理者对象提交请求
    -   不关心处理细节和请求的传递过程

## 实现流程

### 抽象处理类

```java
public abstract class Handler {

    protected abstract boolean in(Request request);

    protected abstract Response handle(Request request);

    public Response execute(Request request) {
        if (in(request)) {
            return handle(request);
        } else if (post != null) {
            return post.execute(request);
        }
        return null;
    }

    public final void setPost(Handler post) {
        this.post = post;
    }
    
    public static final Handler HEAD = new Handler() {
        @Override
        protected boolean in(Request request) {
            return false;
        }

        @Override
        protected Response handle(Request request) {
            return null;
        }
    };
    private static final Handler TAIL = new Handler() {
        @Override
        protected boolean in(Request request) {
            return true;
        }

        @Override
        protected Response handle(Request request) {
            return new Response(404, "Not Found", null);
        }
    };
    private Handler post = TAIL;
    
}
```

### 具体处理者

```java
public class GetHandler extends Handler {
    @Override
    protected boolean in(Request request) {
        return request.getRequestMethod() == Request.RequestMethod.GET;
    }

    @Override
    protected Response handle(Request request) {
        return new Response(200, "OK", request.getRequestMethod().toString());
    }
}
```

```java
public class PostHandler extends Handler {
    @Override
    protected boolean in(Request request) {
        return request.getRequestMethod() == Request.RequestMethod.POST;
    }

    @Override
    protected Response handle(Request request) {
        return new Response(200, "OK", request.getRequestMethod().toString());
    }
}
```

### Client

```java
public static void demo() {
    Handler getHandler = new GetHandler();
    Handler postHandler = new PostHandler();
    getHandler.setPost(postHandler);
    Handler.HEAD.setPost(getHandler);
    Request request = new Request(Request.RequestMethod.GET);
    Response execute = Handler.HEAD.execute(request);
    System.out.println(execute);
    System.out.println(Handler.HEAD.execute(new Request(Request.RequestMethod.POST)));
    System.out.println(Handler.HEAD.execute(new Request(Request.RequestMethod.DELETE)));
}
```

## Java Web中的应用

FilterChain

Netty-Channel-pipline
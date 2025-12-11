## 函数式接口

```java
@FunctionalInterface
public interface PreMap {
    Object[] map(Object... params) throws Exception;
}

@FunctionalInterface
public interface PostMap {
    Object map(Object result) throws Exception;
}

@FunctionalInterface
public interface MethodExecutor {
    Object execute(Object... params) throws Exception;
}

```
## 对函数式接口的封装

```java
public interface MethodAdvice {
    Object[] pre(Object... params) throws Exception;

    Object post(Object result) throws Exception;
}

public static class SimpleMethodAdvice implements MethodAdvice {
    private final PreMap pre;
    private final PostMap post;

    public SimpleMethodAdvice(PreMap pre, PostMap post) {
        this.pre = pre;
        this.post = post;
    }

    @Override
    public Object[] pre(Object... params) throws Exception {
        return pre == null ? params : this.pre.map(params);
    }

    @Override
    public Object post(Object result) throws Exception {
        return post == null ? result : this.post.map(result);
    }
}

```

### 构建者类

```java
public class MethodExecutorBuilder {

    private MethodExecutor runnable;

    public MethodExecutorBuilder(MethodExecutor task) {
        this.runnable = task;
    }

    public MethodExecutor getTask() {
        return runnable;
    }

    public MethodExecutorBuilder addAdvice(PreMap pre, PostMap post) {
        MethodExecutor task = runnable;
        runnable = (param) -> {
            if (pre != null) {
                param = pre.map(param);
            }
            Object result = null;
            if (task != null) {
                result = task.execute(param);
            }
            if (post != null) {
                result = post.map(result);
            }
            return result;
        };
        return this;
    }

    public MethodExecutorBuilder addAdvice(MethodAdvice advice) {
        MethodExecutor task = runnable;
        runnable = params -> advice.post(task.execute(advice.pre(params)));
        return this;
    }

    public static MethodExecutor defaultAdvice(MethodExecutor executor){
        // ...
    }
}
```

## 增强

```java
public static MethodExecutor defaultAdvice(MethodExecutor executor) {
    MethodAdvice recode = new SimpleMethodAdvice(params -> {
        System.out.println("params = " + Arrays.toString(params));
        return params;
    }, result -> {
        System.out.println("result = " + (result == null ? "null" : result.getClass().isArray() ? Arrays.toString((Object[]) result) : result.toString()));
        return result;
    });
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS");
    MethodAdvice time = new MethodAdvice() {

        private long startNano;

        @Override
        public Object[] pre(Object... params) {
            System.out.println(LocalDateTime.now().format(formatter));
            startNano = System.nanoTime();
            return params;
        }

        @Override
        public Object post(Object result) {
            double cost = (System.nanoTime() - startNano) / 1000000.0;
            System.out.println(LocalDateTime.now().format(formatter));
            System.out.printf("cost : %.3f ms\n", cost);
            return result;
        }
    };

    return new MethodExecutorBuilder(executor).addAdvice(recode).addAdvice(time)
            .addAdvice(params -> {
                System.out.println("start");
                return params;
            }, result -> {
                System.out.println("end");
                return result;
            }).getTask();
}
```

## 代理

```java
public static <I, R extends I> I getProxy(Class<I> interfaceType, R realSubject) {
    if (interfaceType == null ||
            !interfaceType.isInterface() ||
            !interfaceType.isInstance(realSubject)) {
        return null;
    }
    Class<?> realType = realSubject.getClass();
    Object proxyInstance = Proxy.newProxyInstance(
            realType.getClassLoader(), // 和具体主体类加载器一致
            realType.getInterfaces(), 
            (proxy, method, args) -> MethodExecutorBuilder
        .defaultAdvice(params -> method.invoke(realSubject, params)).execute(args));
    return (I) proxyInstance;
}
```

## 使用

```java
public static void main(String[] args) {
    Subject proxy = MethodExecutorBuilder.getProxy(Subject.class, new RealSubject());
    proxy.run();
}
```


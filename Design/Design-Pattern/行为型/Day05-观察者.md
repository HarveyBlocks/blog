# 观察者

发布-订阅模式

Publish-Subscribe

定义了一对多的依赖关系, 让多个观察者对象同时监听某一个主题对象

主题对象发生状态改变, 会通知所有观察者, 使观察者更新自己

降低主题与观察度之间的耦合

## 缺点

观察者特别多, 最后一个观察者会有延时

出现循环依赖, 会导致系统崩溃 

## 适用场景

一对多

一个对象导致多个对象改变

## 结构

-   抽象主题
    -   Subject
    -   保存所有观察者
    -   保存一个集合, 把所有的观察者存入该集合
-   具体主题
    -   Concrete Subject
    -   将有关状态存入具体观察者对象
    -   在状态发生变化时广播所有观察者
-   抽象观察者
    -   Observer
    -   定义更新接口
-   具体观察者
    -   Concrete Observer
    -   在主题更新时更新自身状态

如果主题作为字段存储, 而观察者是局部变量且只在该方法内部生效, 那么JVM无法从主题的集合中回收该观察者最终有内存溢出的危险

## 实现流程

### Subject

```java
public abstract class Subject {
    private final Set<Observer> observers;
    protected Subject(){
        this.observers = new HashSet<>();
    }
    public abstract void broadcast(String msg);

    public Set<Observer> getObservers() {
        return observers;
    }

    public void add(Observer observer) {
        this.observers.add(observer);
    }
    public void remove(Observer observer) {
        this.observers.remove(observer);
    }
}
```

### Concrte Subject

```java
public class ConcreteSubject extends Subject {
    public ConcreteSubject() {
        super();
    }

    private static final ExecutorService POOL = Executors.newFixedThreadPool(5);

    @Override
    public void broadcast(String msg) {
        Set<Observer> observers = getObservers();
        for (Observer observer : observers) {
            POOL.execute(() -> {
                observer.listen(msg);
            });
        }
    }
}
```

### Observer

```java
public abstract class Observer {
    public abstract void listen(String msg);

    public abstract void register(Subject subject);

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);
}
```

### Concrete Observer

```java
public class ConcreteObserver extends Observer {
    @Override
    public void listen(String msg) {

        long threadId = Thread.currentThread().getId();
        System.out.printf("%2d %08X : %s\n", threadId, this.hashCode(), msg);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(Subject subject) {
        subject.add(this);
    }

    @Override
    public int hashCode() {
        return UUID.randomUUID().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }
}
```

### Demo

```java
public static final Scanner SCANNER = new Scanner(System.in);

public static void demo() {
    Subject subject = new ConcreteSubject();
    for (int i = 0; i < 19; i++) {
        subject.add(new ConcreteObserver());
    }
    ConcreteObserver observer = new ConcreteObserver();
    observer.register(subject);
    subject.broadcast("Hello World");
    while (SCANNER.hasNextLine()) {
        subject.broadcast(SCANNER.nextLine());
    }
}
```

## JDK中的观察者

`java.util.Observer `接口

`java.util.Observeable`  Subject抽象类

继承了Observeable的具体类, 需要在更新之后加上

```java
super.setChanged();
super.notifyObservers();
```


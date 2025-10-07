# 备忘录

状态恢复机制, 让用户方便地回到一个特定的历史步骤

又叫快照模式, 不破坏封装性的前提下, 捕获一个对象的内部状态, 并将该对象之外保存这个状态, 以便之后需要时对对象进行恢复

## 缺点

多个备份占用内存

## 使用场景

存储每一个历史版本的备忘录, 例如用栈或者时间-发起人映射

## 结构

-   发起人
    -   Originator
    -   记录当前时刻的内部状态信息
    -   提供创建备忘录(当前快照)和恢复备忘录数据的功能
    -   实现其他业务
    -   可以访问备忘录里所有信息
-   备忘录
    -   Memenoto
    -   负责存储发起人的内部状态, 需要时提供这些内部状态给发起人
-   管理者
    -   CareTaker
    -   对备忘录进行管理, 提供保存与获取备忘录的功能, **不能对备忘录的内容进行修改与访问**

备忘录的权限与接口

## 黑箱备忘录

-   **窄接口**

    管理者和其他发起人之外的对象, 只能看到备忘录的窄接口

    这个接口只允许它把备忘录对象传给其他人

-   **宽接口**

    发起人对象可以看到宽接口, 允许读取所有数据, 以便恢复这个发起人对象的内部状态

将Memenoto设计成Originator的私有内部成员类

### Memenoto

对外暴露的空接口, 保证备忘录的封装性

```java
public interface Memento {
    // 空
}
```

### Originator&BlackMemenoto

```java
public class Originator {
    private final List<String> messages = new ArrayList<>();

    public void add(String msg) {
        messages.add(msg);
    }

    public void show() {
        System.out.println("--------show---------");
        for (String msg : messages) {
            System.out.println(msg);
        }
    }
    public void clear(){
        this.messages.clear();
    }
    @Override
    public Originator clone() {
        Originator originator = new Originator();
        originator.messages.addAll(messages); // 深拷贝, 但只有一层
        return originator;
    }

    public Memento blackSave() {
        return new BlackMemento(this);
    }

    public void recover(Memento memento) {
        BlackMemento blackMemento;
        try {
            blackMemento = (BlackMemento) memento;
        } catch (ClassCastException e) {
            throw new RuntimeException(
                "You Can't Implement Memento By YOURSELF",e);
        }
        recover(blackMemento.copy);
    }

    private void recover(Originator originator) {
        this.messages.clear();
        this.messages.addAll(originator.messages);
    }

    private static class BlackMemento implements Memento {
        private final Originator copy;

        public BlackMemento(Originator originator) {
            copy = originator.clone();
        }
    }
}
```

### CareTaker

```java
public class BlackCareTaker {
    private Memento memento;

    public Memento get(){
        return memento;
    }
    public void set(Memento memento){
        this.memento = memento;
    }
}
```

### Demo

```java
public static void blackDemo() {
    Originator originator = init();
    originator.show();
    Memento memento = originator.blackSave();
    BlackCareTaker careTaker = new BlackCareTaker();
    careTaker.set(memento);
    originator.clear();
    originator.show();
    originator.recover(careTaker.get());
    originator.show();
}

private static Originator init() {
    Originator originator = new Originator();
    originator.add("A");
    originator.add("B");
    originator.add("C");
    originator.add("D");
    return originator;
}
```



## 白箱备忘录

不实现两个窄接口和宽接口

### WhiteMemento

```java
public class WhiteMemento {
    private final Originator copy;

    public WhiteMemento(Originator originator) {
        copy = originator.clone();
    }


    public Originator get() {
        return copy;
    }
}
```

### WhiteCareTaker

```java
public class WhiteCareTaker {
    private WhiteMemento whiteMemento;

    public WhiteMemento get(){
        return whiteMemento;
    }
    public void set(WhiteMemento whiteMemento){
        whiteMemento.get().add("不安全");
        this.whiteMemento = whiteMemento;
    }
}
```

### 

### Demo



```java
public static void whiteDemo() {
    Originator originator = init();
    originator.show();
    WhiteMemento whiteMemento = originator.whiteSave();
    whiteMemento.get().add("超级不安全");
    WhiteCareTaker whiteCareTaker = new WhiteCareTaker();
    whiteCareTaker.set(whiteMemento);
    originator.add("X");
    originator.show();
    originator.recover(whiteCareTaker.get());
    originator.show();
}
```
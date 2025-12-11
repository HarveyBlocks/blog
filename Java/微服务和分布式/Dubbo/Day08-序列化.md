# 序列化

两个机器传输数据, 该怎么封装对象?

dubbo内部已经将序列化和反序列化的过程内部封装了

我们只需要在**定义pojo的时候实现Serializable即可**

```java
@Getter
public class User implements Serializable {
    private final String username;

    public User(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                '}';
    }
}
```

**不实现, 就报错**


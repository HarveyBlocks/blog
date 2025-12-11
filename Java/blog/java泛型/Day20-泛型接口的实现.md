# 泛型接口

#### 实现类也是泛型类(下法一)

- 实现类和接口的泛型类型要一致

```java
class PersonImpl<T> implement Person<T>
```

##### 实现类是泛型类,且实现类的泛型比接口多

#### 实现类不是泛型类(下法二)

- 接口明确泛型的数据类型

```java
class PersonImpl implement Person<String>
```

```java
class PersonImpl implement Person<T>//编译时错误
```

``` java
class PersonImpl implement Person//这里Person默认成了Object
```

## 实现接口之法一

### 接口文件

```java
package GenericLearning;

public interface MyGenericInterface<T> {
    //能不能用static修饰T呢?
    //我们知道在接口中,所有定义的属性都是常量,那是要赋初值的
    //所以泛型的属性不能卸载接口里
    T server(T t);
}
```

### 接口实现类:

```java
package GenericLearning;

/**
 * @author HarveyBlocks
 * @date 2023/08/28 17:04
 **/
public class MyGenericInterfaceImpl implements MyGenericInterface<String> {
    //在实现接口的时候一定要要把类型告诉他-------------------------------↑

    @Override
    public String server(String t) {
        System.out.println(t);
        return t;
    }
}
```

### 测试类

```java
package GenericLearning;

/**
 * @author HarveyBlocks
 * @date 2023/08/28 16:24
 **/
public class TestGeneric {
    public static void main(String[] args) {
        MyGenericInterfaceImpl impl = new MyGenericInterfaceImpl();
        impl.server("xxxxxxxxxxxxxxxxxxx");
    }
}
```

## 实现泛型接口之法二

对于以上:

```java
public class MyGenericInterfaceImpl implements MyGenericInterface<String> {
    //在实现接口的时候一定要把类型告诉他-------------------------------↑
}
```

这句话.

不能把类型告诉他,在实现接口的时候不确定类型

**把实现类也做成泛型类**即可

```java
public class MyGenericInterfaceImpl<T> implements MyGenericInterface<T> {
    @Override
    public T server(T t) {//方法也要把String改成T
        System.out.println(t);
        return t;
    }
}
```

接口不变:

```java
package GenericLearning;

public interface MyGenericInterface<T> {
    static String name = "张三";
    T server(T t);
}
```

测试类:

```java
package GenericLearning;

/**
 * @author HarveyBlocks
 * @date 2023/08/28 16:24
 **/
public class TestGeneric {
    public static void main(String[] args) {
        MyGenericInterfaceImpl<String> impl1 = new MyGenericInterfaceImpl();
        impl1.server("hi");//hi

        MyGenericInterfaceImpl<Integer> impl2 = new MyGenericInterfaceImpl();
        impl2.server(2);//2

    }
}
```


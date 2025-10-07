# 泛型方法的实现

## 泛型方法实现

```java
package GenericLearning;

/**
 * 泛型方法
 * 语法:<T> 返回值类型
 * @author HarveyBlocks
 * @date 2023/08/29 08:58
 **/

public class GenericMethod {

    //泛型方法
    public <T> T show(T t) {
        T t1 ;
        System.out.println(t.hashCode());
        return t;
    }

    
    public <T,K> E showIt(E e) {// 所有E都编译时异常
        E e1;
        System.out.println(e.toString());
        return e;
    }
}
```



## 测试类



```java
package GenericLearning;

/**
 * @author HarveyBlocks
 * @date 2023/08/28 16:24
 **/
public class TestGeneric {
    public static void main(String[] args) {
        GenericMethod genericMethod = new GenericMethod();
        genericMethod.show("hi");//hi
        genericMethod.show(new Integer(12));//12
        genericMethod.showIt("hi");//hi
        genericMethod.showIt(12);//12
    }
}
```





## 泛型可变参数的方法

```java
public class Demo {
    public static void main(String[] args) {
        PrintSomething.print(1,3,4);
        PrintSomething.print("a","x","Z");
        PrintSomething.print("S",1,2.4);
    }
}
class PrintSomething<T>{
    public static <T> void print(T... t){
        System.out.print("[");
        for (int i = 0; i < t.length-1; i++) {//t.fori
            System.out.print(t[i]+",");
        }
        System.out.println(t[t.length-1]+"]");
    }
}
```


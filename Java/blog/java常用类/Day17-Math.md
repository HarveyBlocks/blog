Math的构造是private

```java
public class Main {
    public static void main(String[] args) {
        double a = Math.sqrt(2);
        System.out.println(a);
        System.out.println(Math.random());
        System.out.println(Math.sin(1.0));
        System.out.println(Math.sin(3.14159265358979323/2));
    }
}
```

为了避免溢出却没有报错,我们可以使用Math类的以下方法

```java
Math.addExact();
Math.subtractExact(); 
Math.multiplyExact();//诸如此类
```

Math的random

```java
int randomNumber = (int)(Math.random()*n)//Math.random()得到[0,1)之间的随机数
```

得到[0 , n-1]之间的整型随机数


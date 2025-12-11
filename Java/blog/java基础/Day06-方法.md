# 方法

- 方法具有原子性

## 方法的定义

``` java
[修饰符] [..] 方法类型 方法名([形参类型 形参名][,....]){
	...
    方法体
    ...
    return [返回值];
}
```

## 方法的引用

``` java
[type name] = 方法名([实参类型 实参名][,....])
```

``` java
public class Demo01 {
    public static void main(String[] args) {
        int i =1;
        String a=""+ i;
        System.out.println(plus(a));
    }

    public static String plus(String a){
        return a+=a;
    }

}
```

return i;
**只是往做返回容器用的寄存器里面复制入i的值而已。不对其他东西有影响**

所以就算有返回值,不赋值给任何数,不作为实参被调用,也不会报错

## 方法的重载

- 两同三不同。

	1. ——同一个类，同一个方法名。
	2. ——不同：参数列表不同。（类型，个数，顺序不同）

- 只有返回值不同不构成方法重载。
- 只有形参的名称不同，不构成方法重载。
- 与普通方法一样，构造函数也可以重载。

``` java
public class Demo01 {
    public static void main(String[] args) {
        System.out.println(add(23,4));
        System.out.println(add(2.3,4));
        System.out.println(add(2,3.4));
        System.out.println(add(2.3,3,4));

    }
    public static int add (int a,int b){
        return a+b;
    }
    public static double add (double a,int b){//参数类型不同
        return a+b;
    }
    public static double add (int a,double b){//参数顺序不同
        return a+b;
    }
    public static double add (double a,int b,int c){//参数个数不同
        return a+b+c;
    }
}

```

## 可变参数

- 当方法需要未知个数的参数是，使用可变参数

1. JDK1.5开始
2. 一个方法中只能指定一给可变参数
3. 这个可变参数必须是最后一个参数

声明方法：type... name

``` java
[修饰符] 方法类型 方法名([其他参数...，] 可变参数类型... 可变参数名){得到的可变参数是个数组
    方法体
}
```

示例：

``` java
package com.pac;

public class Demo01 {
    public static void main(String[] args) {
        Demo01 demo01= new Demo01();                //用这句话
        System.out.println(demo01.max(new double[] {1,2,3,4,5,6}));
                            //↑再引用以下类名
        //max方法就不用加修饰符static↓
    }

    public double max(double... list){
        if(list.length==0){
            System.out.println("不对");
            return 0.0;
        }
        double valueMax=list[0];
        for(double value:list){
            if (value>valueMax){
                valueMax=value;
            }
        }
        return valueMax;
    }
}

```

## 递归

``` java
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        int n =inPut () ;
        System.out.println("你要这么移动：");
        move('A','B','C',n);
    }
    public static int inPut (){
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入汉诺塔的层数");
        int i = scanner.nextInt();
        System.out.println("输入层数为：" + i);
        scanner.close();
        return i;
    }
    public static void move (char A,char B,char C,int n){
        if(n==1){
            System.out.println(A+"->"+C);
            return ;
        }
        move(A,C,B,n-1);
        System.out.println(A+"->"+C);
        move(B,A,C,n-1);
    }
}
```


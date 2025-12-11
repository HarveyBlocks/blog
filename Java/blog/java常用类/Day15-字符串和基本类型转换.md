# 字符串和基本类型的转化

## 基本类型转换成字符串

```java
public class Main {
    public static void main(String[] args) {
        int n1 = 100;
        //法一：+
        String str1 = n1 + "";
        //法二：Integer中的静态方法toString()
        String str2 = Integer.toString(n1);

        //进制转换,把15转化为16进制(任意进制)
        String str3 = Integer.toString(15, 16);//str3=f
        //进制转换,把15转化为二进制
        String str4 = Integer.toBinaryString(15);
        //进制转换,把15转化为八进制
        String str5 = Integer.toOctalString(15);
        //进制转换,把15转化为十六进制
        String str6 = Integer.toHexString(15);
    }

}
```

### 示例：字符转化为字符串

#### 方法一：使用Character.toString()

Character类提供了一个静态方法

toString()

用来将字符转换成字符串。

```java
char ch = 'U';
String charToString = Character.toString(ch);
```

#### 方法二：使用字符串连接符

当我们使用字符串连接符的时候

会自动将其他类型的变量转换为字符串类型

如下：

```java
char ch = 'U';

String str = ""+ch;
```

#### 方法三：使用String.valueOf()

```java
char ch = 'U';
String valueOfchar = String.valueOf(ch);
```

## 字符串转化为基本类型

```java
public class Main {
    public static void main(String[] args) {
        String str1="150";
        //使用Integer.parseXXX
        System.out.println(Integer.parseInt(str1)-1);//149
        int num = Integer.parseInt(str1);
        System.out.println(num-1);//149

        /*
        String str1="15O";//十五欧
        System.out.println(Integer.parseInt(str1)-1);
        //返回异常java.lang.NumberFormatException
         */
    }

}
```

double亦如此，其余自己推

```java
public class Main {
    public static void main(String[] args) {
        String str1="1.5";
        //使用Double.parseXXX
        System.out.println(Double.parseDouble(str1)-1);
    }
}
```

## boolean类型与字符串的转换

boolean转换成字符类型的时候

只有”true“可以转成true

非“true”通通转成false

```java
public class Main {
    public static void main(String[] args) {
        String string = "true";

        boolean flag = Boolean.parseBoolean(string);
        System.out.println(flag);//true

        flag = Boolean.parseBoolean("stupid");
        System.out.println(flag);//false
    }
}
```


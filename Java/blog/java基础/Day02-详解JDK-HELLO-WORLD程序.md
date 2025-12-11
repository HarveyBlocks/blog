# 详解JDK-HelloWorld程序

## 代码

**半角字符，懂？**

```java
public class hello {       //这个hello叫做类名，要和文件名匹配。这一排规定了“类”
	public static void main (String[] args){    //“main”规定了一个主方法
                                                //"tring[] args"是参数，但这里没有用到
		System.out.print("Hello World!");
	}
}
```

## 编译和运行

在cmd中运行代码

``` bush
javac 文件名.java  #进行编译，会出现“文件名.class”
java 文件名        #运行文件，不要输.class
```

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java基础/Day02-详解JDK-HELLO-WORLD程序/image-20230801135535684.png" alt="image-20230801135535684" style="zoom:50%;" />

## 可能会出现的问题

1. 大小写问题（JAVA是**大小写敏感的**）

2. 不支持中文

   ```java
   public class hello{
   	public static void main (String[] args){
   		System.out.print("你好世界");
   	}
   }
   ```

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java基础/Day02-详解JDK-HELLO-WORLD程序/image-20230801140511235.png" alt="image-20230801140511235" style="zoom:50%;" />


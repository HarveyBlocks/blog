# 循环结构

## while循环

``` java
while(){
    循环体
}
```

## do-while循环

``` java
do{
    循环体
}while()
```

## for循环

**注意分号**

``` java
for (初始化[int i =0];布尔值;更新){
    循环体
}
```

## 增强for循环

- 常用于遍历数组或集合
- 注意冒号

``` java
/*
for (声明语句：表达式) {
    [循环体]
}
*/

public class Main {
    public static void main(String[] args) {
        int[] numbers={1,2,3,4,5,6,7};
        for (int number:numbers){
            System.out.println(number);
        }
    }
}
```

## break和continue和标签

```java
//标签，太脏啦！！！！！！！！！！！

public class Main {
    public static void main(String[] args) {
        //接下来输出500-5000的所有质数
        fuck:for(int i =503;i<5000;i+=2){		//fuck在这里就是和一个标签。[标签名]:
            for(int j=2;j<=(int) Math.pow(i,0.5);j++){
                if(i%j==0){
                    continue fuck;              //和continue结合使用。
                }
            }
            System.out.println(i);

        }
    }
}
```

**break加标签和continue加标签的区别**

这是continue加标签

``` java
public class Main {
    public static void main(String[] args) {
        for(int i =0;i<10;i++){
            fuck:for(int j=0;j<=i;j++){			//**
                if (j==0){					
                    continue fuck;				//**
                }
                System.out.print(j);
            }
            System.out.println();

        }
    }
}
```

![屏幕截图 2023-08-03 190344](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java基础/Day05-循环结构/屏幕截图 2023-08-03 190344.png)

***
这是break加标签

``` java
public class Main {
    public static void main(String[] args) {
        for(int i =0;i<10;i++){
            fuck:for(int j=0;j<=i;j++){ 		//**
                if (j==0){
                    break fuck;					//**
                }
                System.out.print(j);			
            }
            System.out.println();

        }
    }
}
```

![屏幕截图 2023-08-03 190522](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java基础/Day05-循环结构/屏幕截图 2023-08-03 190522.png)

***
- continue加标签，回到标签处，标签冒号后的循环**再次**执行
- break加标签，回到标签处，标签冒号后的循环**不再**执行


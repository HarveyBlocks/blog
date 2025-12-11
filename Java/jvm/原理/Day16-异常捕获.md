# 异常捕获

## 异常表

包含了**异常捕获**的生效范围以及异常发生后跳转到的字节码指令位置

![image-20240602210423408](../assets/Untitled/image-20240602210423408.png)-

-   起始PC **异常捕获**生效的起始位置
-   结束PC **异常捕获**生效的结束位置
-   跳转PC **异常捕获**之后跳转到的字节码位置

![image-20240602211246408](../assets/Untitled/image-20240602211246408.png)

## 异常捕获

### try-catch

1.  将捕获到的异常存入局部变量表
2.  执行catch语句
3.  如果异常没有被捕获, 直接弹出栈帧, 在上一层的栈帧中进行异常捕获的查询

![image-20240602211342569](../assets/Untitled/image-20240602211342569.png)

### finally

1.  直接插入到try-catch代码块之后

    <img src="../assets/Untitled/image-20240602212504613.png" alt="image-20240602212504613" style="zoom:50%;" />

2.  没有catch住的异常, 让所有的异常都走一段finally

    <img src="../assets/Untitled/image-20240602212727273.png" alt="image-20240602212727273" style="zoom:50%;" />


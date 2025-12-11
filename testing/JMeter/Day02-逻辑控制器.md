# 逻辑控制器

![image-20240418150610455](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/testing/JMeter/Day02-逻辑控制器/image-20240418150610455.png)

## if控制器

设置父子集关系

![image-20240418150755151](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/testing/JMeter/Day02-逻辑控制器/image-20240418150755151.png)

![image-20240418151317018](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/testing/JMeter/Day02-逻辑控制器/image-20240418151317018.png)

应该就是按照java的逻辑表达式的语法来的

```if
1<${userId} && ${userId}<10
```

# ForEach

![image-20240418153039412](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/testing/JMeter/Day02-逻辑控制器/image-20240418153039412.png)

1.  构建出`name_1`
2.  在用户变量(等变量中)寻找`name_1`
3.  赋值给val
4.  在http-根据ID查询用户中使用变量`val`, 就会使用`val`的值

## 循环控制器

![image-20240418160908176](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/testing/JMeter/Day02-逻辑控制器/image-20240418160908176.png)


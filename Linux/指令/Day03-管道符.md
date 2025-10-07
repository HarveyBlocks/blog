# 管道符: | 

## 含义

将管道符左边的命令作为右边命令的输入

![image-20230929224740504](../shoot/Day03/image-20230929224740504.png)

## 应用实例

如果直接:

```linux
ls -l /usr/bin
```



![image-20230929230033365](../shoot/Day03/image-20230929230033365.png)

肥肠的不直观

但是:

```linux
ls -l /usr/bin | grep "gtf" 
```

![image-20230929230138654](../shoot/Day03/image-20230929230138654.png)



你甚至可以:

```Linux
ls -l /usr/bin | wc -l
```



![image-20230929230319801](../shoot/Day03/image-20230929230319801.png)

所以:**懒惰使人进步**

## 思考:管道符的嵌套

``` linux
cat ./text.tst | grep "know" | grep -n "death"
cat ./text.tst | grep "know" | wc -w
ls -l / | grep "game" | wc -l
```




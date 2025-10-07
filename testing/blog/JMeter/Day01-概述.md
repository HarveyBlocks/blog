# JMeter

## 简介

- Apache
- Java开发
- 用于对服务器, 网络或对象模拟巨大的负载
- 通过创建带有断言的脚本来验证程序是否能返回预期情况



## 优缺点

### 优点

-   开源, 免费
-   支持多协议
    -   Http
    -   FDB
-   跨平台
    -   跨操作系统
    -   跨硬件
    -   因为是Java实现的
-   小巧
-   功能强大



### 缺点

-   不支持IP欺骗
    -   IP造假
-   使用JMeter无法验证JS程序, 无法验证页面UI
    -   可以和Selenium配合来完成Web2.0应用的测试





## 安装启动

[Apache JMeter](https://jmeter.apache.org/)







### 支持中文编码集

[配置文件地址](D:\IT_study\apache-jmeter-5.6.2\bin\jmeter.properties)

![image-20240417163057637](../../assets/Day01-%E6%A6%82%E8%BF%B0/image-20240417163057637.png)





### 启动

`\bin`下

`JMeter.sh` linux启动

`JMeter.bat` Windows运行

`ApacheJMeter.jar`通用`java -jar`



## 简单操作

###创建线程组

![image-20240417164459721](../../assets/Day01-%E6%A6%82%E8%BF%B0/image-20240417164459721.png)

### 添加取样剂--Http请求

![image-20240417164604841](../../assets/Day01-%E6%A6%82%E8%BF%B0/image-20240417164604841.png)

### 设置Http请求

![image-20240417164846513](../../assets/Day01-%E6%A6%82%E8%BF%B0/image-20240417164846513.png)

### 设置监听器

方便观察测试结果

![image-20240417165331538](../../assets/Day01-%E6%A6%82%E8%BF%B0/image-20240417165331538.png)

### 启动运行

![image-20240417170619177](../../assets/Day01-%E6%A6%82%E8%BF%B0/image-20240417170619177.png)



### 运行结果

![image-20240417170742028](../../assets/Day01-%E6%A6%82%E8%BF%B0/image-20240417170742028.png)
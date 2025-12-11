# 图形化测试报告

在jMeter/bin目录下运行: 

```shell
./jmeter -n -t jmx文件路径 -l 日志文件 -e -o 生成目标目录
```

-   `-n`无图形化运行
-   `-t` 被允许的脚本
-   `-l` 将运行的信息写入日志文件
-   `-e` 生成测试报告
-   `-o`输出测试报告

```shell
java -jar ./ApacheJMeter.jar -n -t D:\IT_study\source\jmx\hvideo.jmx -l D:\IT_study\source\jmx\result.log -e -o D:\IT_study\source\jmx\result
```

`D:\IT_study\source\jmx\result`要求是个空目录


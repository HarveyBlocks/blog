# 部署

>   把SpringBoot的项目放到服务器上

1.  打Jar包(官方推荐)

    jar包目录下:`java -jar .\new-web-0.0.1-SNAPSHOT.jar`

2.  打war包

    放入Tomcat的webap目录下,然后启动tomcat`startup`

    注意在访问时要加目录`springboot` (具体看倒是后webapp目录下解压的war包) -> `localhost:8080/springboot/user/findall` 
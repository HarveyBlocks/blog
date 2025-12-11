编写Dockerfile

```dockerfile
FROM openjdk:11.0-jre-buster
ENV TZ=Asoz/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY "项目所在Jar包" "/app.jar"

ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=dev"]
```





```bash
docker build --help
```

```bash
docker build -t 取一个镜像名 Dockerfile文件路径
```



启动容器

```bash
docker run -d --name 容器名 -p 8080:8080 --network 网络名(和数据库等在一个网络下) 镜像名
```

-   端口映射看情况


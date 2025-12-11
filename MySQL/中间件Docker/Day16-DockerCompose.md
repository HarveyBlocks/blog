# Docker容器快速部署

一个项目可能涉及到多个容器, 例如Raddis,mysql,jar

## 手动部署的问题

1.  一个一个部署,麻烦
2.  一个一个部署,容器之间相互关联,两次部署之间出现问题,完蛋
3.  一个项目下的多个容器应该在同一个网络下, 这更说明几个容器应该是一个整体, 不应该被割裂

## DockerCompose

-   通过单独的**docker-compose.yml** **模板文件**定义一组相关联的应用容器
-   用于帮助我们实现多个相互关联的Docer容器的快速部署

### docker-compose.yml

-   **一个docker-compose.yml代表了一个项目**
-   docker-compose.yml文件中的一个**服务(service)**代表了一个容器
-   service中描述的信息应该于`docker run`大差不差

```yml
version: 3.8

service:
  mysql:
    image: mysql
    container_name: mysql
    ports:
      - "3306:3306"
    enviroment:
      TZ: Asiz/Shanghai
      MYSQL_ROOT_PASSWORD: 123
    volumns:
      - "./mysql/conf:/etc/mysql/conf.d"
      - "./mysql/data:/etc/mysql/mysql"
      - "./mysql/init:/etc/mysql/docker-entrypoint-initdb.d"
    networks:
      - my-net
    mainJar:
      build:
        context: .
        dockerfile: Dockerfile #自由自在?
      container_name: mainJar
      ports:
        - "8080:8080"
      networks:
        - hm-net
      depends_on: # 不写也没事, 区别在于, docker-compose会先去创建mysql
        - mysql
    nginx:
      image: nginx
      ...

net-work: #定义网络
  my-net: #上文网络的标识
    name: my-network #再次定义一个网络名
```

### dockerCompose部署命令

```bash
docker compose [OPTION] [COMMAND] [-d]
```

![image-20231214003828453](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/中间件Docker/Day16-DockerCompose/image-20231214003828453.png)

-   `-f`缺省,默认是当前工作目录
-   `-p`缺省,默认是root
-   `-d`表示后台运行


# 安装

## Docker

- Pull

  ```bash
  docker pull mongodb/mongodb-community-server:latest
  ```

- Run

  ```bash
  docker run --name mongodb 
  	-p 27017:27017
      -d mongodb/mongodb-community-server0:latest
  ```

  ```bash
  docker run \
  	--restart=always \
  	--name MongoDb \
      -p 27017:27017 \
      -e TZ=Asia/Shanghai \
      --privileged=true \
      -e MONGO_INITDB_ROOT_USERNAME=MongoRoot \
      -e MONGO_INITDB_ROOT_PASSWORD=123456 \
      -d mongodb/mongodb-community-server:latest
  ```

## MongoDB Shell

mongosh是一个 JavaScript 和 Node.js REPL 的命令行环境，用于与 MongoDB 服务器进行交互。

安装Windows版本

![image-20251103122225028](../../assets/Day01-Install/image-20251103122225028.png)

输入服务器信息

![image-20251105163302764](../../assets/Day01-Install/image-20251105163302764.png)

上面是无权限的游客进行登录

下面是登录Root

```bash
mongosh mongodb://MongoRoot:123456@centos:27017/test?authSource=admin
```

![image-20251105165919784](../../assets/Day01-Install/image-20251105165919784.png)

在控制台`TAB`会有指令提示

![image-20251105170631700](../../assets/Day01-Install/image-20251105170631700.png)

```bash
console.clear()
```

清除控制面板

![image-20251105170705608](../../assets/Day01-Install/image-20251105170705608.png)

可以使用`//`和`/**/`作为注释

## 数据

[将数据加载到 Atlas - Atlas - MongoDB Docs](https://www.mongodb.com/zh-cn/docs/atlas/sample-data/)

下载数据资源

```bash
curl  https://atlas-education.s3.amazonaws.com/sampledata.archive -o sampledata.archive
```

将数据加载到部署

```bash
docker cp ./sampledata.archive MongoDb:/usr
```

在容器内执行命令

```bash
mongorestore \
  --username MongoRoot \
  --password 123456 \
  --archive=sampledata.archive
```


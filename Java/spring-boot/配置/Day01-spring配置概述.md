# 配置

## 配置文件的分类

-   properties
-   yaml(重点)

### 更改默认配置

可以使用`application.properties`或`application.yml`(`application.yaml`)

-   ​	`application.properties`

    ```properties
    server.port=8080
    server.address=127.0.0.1
    server.servlert.context-path=/index
    ```

-   `application.yml`

    ```yaml
    server:
    	port: 8080
    ```

    类嵌套,属性

    **冒号和值之间应该有空格**

#### 配置文件的加载顺序

**`application.properties`->`application.yml`->`application.yaml`**

相同的配置, 先被加载的文件的配置**生效**

## 读取配置文件内容

## profile配置环境

和[maven高级](..\maven\maven_blog\blog\Day01-maven高级.md)还不太一样?!

## 配置加载顺序

[配置加载顺序](\Day02-配置生效顺序.md)


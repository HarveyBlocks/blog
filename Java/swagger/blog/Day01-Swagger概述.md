# Swagger

## Swagger2简介

### 接口文档

>   前后端消息传递时, 传递什么类型, 传递什么格式, 都要提前决定好. 这时候就需要**接口文档**
>
>   接口, 指**通讯标准**

-   自己写文档, 太麻烦
-   别人写文档,不规范, 更新不及时

### Open-API

>   OpneAPI Specification

-   一种规范
    -   和语言都没有关系
-   也叫Swagger规范
-   是Rest风格的API描述格式
    -   Restful->传参方式`/login/zhangsan/123`
    -   Rest->请求方式 `GET`表示查, `POST`表示增, `PUT`是改,`DELETE`删
-   可以用**YAML**或**JSON**进行编写

OpenAP要描述整个API, 需要包括

-   每个访问地址的( `GET`, `POST`...)
-   每隔操作的参数, 包括**请求参数**和响应参数
-   认证方法
-   连接信息, 声明, 使用团队和其他信息





### Swagger2

>   围绕偶OpenApi的开源工具, 帮助**设计**, **构建**, **记录**和**使用**Rest API

后端把一些注解嵌入到代码

前端访问一个浏览器页面, 就能知道有: 请求路径,请求参数, 请求方法

只要关心代码, 不用关心文档



## Spring fox

我们在使用Swagger的时候, 如果碰见版本更新或迭代的时候, 只需要更改Swagger的描述文件即可. 但是反复更新项目版本, 让开发人员觉得即使修改**描述文件(yml或json)**也是一份工作负担

 

Spring-fox(第三方组件)就基于SpringMVC,准备了一些注解, 帮助自动生成文档

[SpringFox ](https://springfox.github.io/springfox/)

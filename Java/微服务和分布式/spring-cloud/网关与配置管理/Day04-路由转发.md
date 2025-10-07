# 路由转发

> 对于前端请求的路由转发

路由规则, 原来的请求和现在微服务的映射关系

## 代码实现

### 创建新项目



### 引入网关依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

####完整版



```xml
<parent>
    <groupId>com.heima</groupId>
    <artifactId>hmall</artifactId>
    <version>1.0.0</version>
</parent>
<dependencies>
    <!--Gateway-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    <!--服务注册-->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    <!--负载均衡策略器-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-loadbalancer</artifactId>
    </dependency>
    <!--common-->
    <dependency>
        <groupId>com.heima</groupId>
        <artifactId>hm-common</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
<build>
    <finalName>${project.artifactId}</finalName>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

### 编写启动类



### 配置路由规则

id是路由的唯一标识, 多套路由id必须是不同的

```yaml
spring:
  cloud:
    gateway:
      routes: 
        - id: item # 路由规则, 自定义, 唯一
          uri: lb://item-service # 路由目标微服务, lb(load balance)协议代表负载均衡
          predicates: # 路由断言,判断请求是否符合规则,符合则路由到目标
            - Path=/items/** # 以请求路径作为判断规则
            - Path=/search/**
        - id: xx
          uri: lb://xx-service
          predicates:
            - Path=/xx/**
```

#### 完整版

```yaml
server:
  port: 8080
spring:
  application:
    name: hm-gateway
  profiles:
    active: dev
  cloud:
    nacos:
      discovery:
        server-addr: ${hm.nacos.host}:8848
    gateway:
      routes:
        - id: item-service
          uri: lb://item-service
          predicates:
            - Path=/items/**,/search/** # 逗号隔开
        - id: cart-service
          uri: lb://cart-service
          predicates:
            - Path=/carts/**
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/users/**,/addresses/** 
        - id: trade-service
          uri: lb://trade-service
          predicates:
            - Path=/orders/**
        - id: pay-service
          uri: lb://pay-service
          predicates:
            - Path=/pay-orders/**
logging:
  level:
    com.hmall: debug
  pattern:
    dateformat: HH:mm:ss:SSS
  file:
    path: "logs/${spring.application.name}"
```

## 路由属性

>   网关路由是RouteDefinition来读取的, 其中有常见类型如`id`,`uri`,`predicates`,`fielters`

-   `id`

    路由唯一标识

-   `uri`

    路由目标地址

-   `predicates`

    路由断言

    判断请求是否符合当前路径

-   `filter`

    路由过滤器

    对请求或响应做加工处理

### 路由断言

[路由断言工厂](https://docs.spring.io/spring-cloud-gateway/docs/3.1.8/reference/html/#gateway-request-predicates-factories)

![image-20240110143440419``](../../assert/Day04-Gateway%E6%8A%80%E6%9C%AF/image-20240110143440419.png)

-   `-Path=/red/{segment}`,传递参数, 要求路径要符合有参数这一规则

![image-20240110144422092](../../assert/Day04-%E8%B7%AF%E7%94%B1%E8%BD%AC%E5%8F%91/image-20240110144422092.png)

### 路由过滤器

[过滤器工厂](https://docs.spring.io/spring-cloud-gateway/docs/3.1.8/reference/html/#gatewayfilter-factories)

![image-20240110144501258](../../assert/Day04-%E8%B7%AF%E7%94%B1%E8%BD%AC%E5%8F%91/image-20240110144501258.png)

-   `StripPrefix`

    前端向后端发请求时常常用`/api/items/list`这种格式

    但是对于微服务来说, 大多是`/items/list`, 若是全部加没必要的`/api`略显......

    就可以剔除前缀`/api` 这个用处

![image-20240110145050509](../../assert/Day04-%E8%B7%AF%E7%94%B1%E8%BD%AC%E5%8F%91/image-20240110145050509.png)

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: ingredients
        uri: lb://ingredients
        predicates:
        - Path=//ingredients/**
        filters:
        - name: CircuitBreaker=fetchIngredients,forward:/fallback
      - id: ingredients-fallback
        uri: http://localhost:9994
        predicates:
        - Path=/fallback
        filters:
        - name: FallbackHeaders=Test-Header
```

#### default-filter

对所有路由生效

```yaml
spring:
  cloud:
    gateway:
      default-filter: 
        - AddRequestHeader=true,"if 'A is B' is true,and 'B is C' is true, then A is C"
      routes:
      - id: ingredients
        uri: lb://ingredients
        predicates:
        - Path=//ingredients/**
        filters:
        - name: CircuitBreaker=fetchIngredients,forward:/fallback
      - id: ingredients-fallback
        uri: http://localhost:9994
        predicates:
        - Path=/fallback
        filters:
        - name: FallbackHeaders=Test-Header
```


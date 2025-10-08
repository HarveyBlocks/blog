# Eureka

-   Netflix

## 搭建EurekaServer

服务的提供方

### 引入依赖



在父工程指定SpringCloud版本

![image-20240502140346642](../../assert/Day03-Eureka%E6%B3%A8%E5%86%8C%E4%B8%AD%E5%BF%83/image-20240502140346642.png)



Eureka依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

### 编写服务端启动类

加入`@EnableEurekaServer`注解

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class, args);
    }

}
```

### 配置

```yaml
server:
  port: 10086
spring:
  application:
    name: eureka
eureka:
  client:
    service-url:
      # eureka的地址信息, eureka自己也是微服务, eureka会把自己注册到注册中心
      defaultZone: http://localhost:10086/eureka
```

### 启动与图形化界面

[eureka界面](http://localhost:10086)

![image-20240502151310015](../../assert/Day03-Eureka%E6%B3%A8%E5%86%8C%E4%B8%AD%E5%BF%83/image-20240502151310015.png)

## 服务注册

### 引入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

↓这个会出现版本依赖问题😓

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-netflix-eureka-client</artifactId>
</dependency>
```

### 配置

```yml
server:
  port: 8080
spring:
  application:
    name: user
eureka:
  client:
    service-url:
   	  # 指定注册中心的位置
      defaultZone: http://localhost:10086/eureka
```

### 启动

![image-20240502154655928](../../assert/Day03-Eureka%E6%B3%A8%E5%86%8C%E4%B8%AD%E5%BF%83/image-20240502154655928.png)

### 多个服务实例

![image-20240502155549871](../../assert/Day03-Eureka%E6%B3%A8%E5%86%8C%E4%B8%AD%E5%BF%83/image-20240502155549871.png)

## 服务发现

```java
@Bean
@LoadBalanced // 默认轮询
public RestTemplate restTemplate(){
    return new RestTemplate();
}
```



```java
UserDto userDto = restTemplate.getForObject("http://user/user/"+userId, UserDto.class);
```

-   服务名`user`代替host和port

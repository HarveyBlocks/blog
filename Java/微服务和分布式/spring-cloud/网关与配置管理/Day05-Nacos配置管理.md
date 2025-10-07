# 配置管理

## 存在问题

-   微服务太多

-   配置文件太多的重复(日志, Spring等)

-   需要修改时要改很多
-   修改配置后的项目虽然不用重新编译打包, 但也要花半天重启
-   如果想修改网关的配置, 而网关是所有请求的入口, 如果网关需要改配置而重启, 那用户体验将会极差

-   **成本太高**,维护麻烦

**配置管理服务**, 让其他服务请求配置管理服务获取配置

## 原理

和业务有关的, 经常会变动的配置交由配置管理服务管理或网关的路由管理

到时候服务配置变更了, 推送给指定服务

-   配置统一管理

-   实现配置热更新

## Nacos配置管理中心

说淘宝垃圾,不必要的功能一推, 说Nacos牛逼, 喜欢的功能一应俱全

![image-20240111141038973](../../assert/Day05-Nacos%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/image-20240111141038973.png)

 ### 配置共享

#### 增加配置

对于大同

```yaml
spring:
  datasource:
    url: jdbc:mysql://${hm.db.host}:3306/${hm.db.name}?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: ${hm.db.pw}
mybatis-plus:
  configuration:
    default-enum-type-handler: com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler
  global-config:
    db-config:
      update-strategy: not_null
      id-type: auto
```

对于小异

```el
${hm.db.name}
${hm.db.host:centos}:${hm.db.port:3306} # 设定默认值3306
```

![image-20240111141937018](../../assert/Day05-Nacos%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/image-20240111141937018.png)

##### jdbc-mybatis.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://${hm.db.host:centos}:${hm.db.port:3306}/${hm.db.name}?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: ${hm.db.un:root}
    password: ${hm.db.pw:123}
mybatis-plus:
  configuration:
    default-enum-type-handler: com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler
  global-config:
    db-config:
      update-strategy: not_null
      id-type: auto
```

##### logging.yml

```yaml
logging:
  level:
    com.hmall: debug
  pattern:
    dateformat: HH:mm:ss:SSS
  file:
    path: "logs/${spring.application.name}"
```

##### swagger.yml

```yml
knife4j:
  enable: true
  openapi:
    title: ${hm.api.tittle}管理接口文档
    description: "${hm.api.tittle}管理接口文档"
    email: ${hm.api.email:114514@810.com}
    concat: ${hm.api.concat:Jack}
    url: ${hm.api.url:www.baidu.com}
    version: ${hm.api.version:v1.0.0}
    group:
      default:
        group-name: default
        api-rule: package
        api-rule-resources:
          - ${hm.api.package}
```

##### feign.yml

```yml
feign:
  okhttp:
    enabled: true
```

##### 

#### 拉取共享配置

-   在初始化CloudAppicationContext前, 需要先拉取Nacos配置
-   拉取Nacos配置前, 需要获取Nacos地址
-   获取Nacos地址之前, 需要获取BootApplicationContext
-   获取BootApplicationContext之前, 需要解析application.yml
-   解析application ,yml之前需要初始化CloudAppicationContext
-   完美闭环



>   解决方案: 使用在初始化CloudApplicationContext之前的配置文件引导配置文件 **`bootstrap.yml`** 

##### 拉取=引入依赖

```xml
<!--配置管理-->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

```xml
<!--读取bootstrap文件-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
```





#### 配置bootstrap.yaml文件

```yaml
spring:
  application:
    name: user-service
  profiles:
    active: dev
  cloud:
    nacos:
      server-addr: centos:8848 # 这里用${hm.nacos.host}报错,原因未知
      config:
        file-extension: yaml
        shared-configs:
          - data-id: jdbc-mybatis.yml
          - data-id: logging.yml
          - data-id: swagger.yml
          - data-id: feign.yml
```



#### 运行测试

```
2024-01-11 15:23:23.667  INFO 12968 --- [           main] b.c.PropertySourceBootstrapConfiguration : Located property source: [BootstrapPropertySource {name='bootstrapProperties-cart-service-local.yaml,DEFAULT_GROUP'}, BootstrapPropertySource {name='bootstrapProperties-cart-service.yaml,DEFAULT_GROUP'}, BootstrapPropertySource {name='bootstrapProperties-cart-service,DEFAULT_GROUP'}, BootstrapPropertySource {name='bootstrapProperties-feign.yml,DEFAULT_GROUP'}, BootstrapPropertySource {name='bootstrapProperties-swagger.yml,DEFAULT_GROUP'}, BootstrapPropertySource {name='bootstrapProperties-logging.yml,DEFAULT_GROUP'}, BootstrapPropertySource {name='bootstrapProperties-jdbc-mybatis.yml,DEFAULT_GROUP'}]
```

### 配置热更新

-   可能在上线之后需要调整, 如超时时长

#### 前提条件

-   nacos中要有一个与微服务名有关的配置文件

    ```
    微服务名称[-项目环境].文件类型
    spring.application.name-spring.actice.profile.file-extension
    cart-service-dev.yaml
    ```

    你看上面的日志

    ```
    bootstrapProperties-cart-service-local.yaml
    ```

-   微服务需要以特定的方式读取热更新的配置属性

    ```java
    @ConfigurationProperties(prefix = "hm.cart")
    public class CartProperties {
        private Integer maxItems;
    }
    ```

    或

    ```java
    @RefreshScope
    public class CartProperties {
        @Value("${hm.cart.maxItems}")
        private List<String> maxItems;
    }
    ```

#### 代码实践

配置类

```java
@Component
@ConfigurationProperties(prefix = "hm.cart")
public class CartProperties {
    private Integer maxItems;
}
```

业务代码

```java
@Resource
private CartProperties cartProperties;
private void checkCartsFull(Long userId) {
    int count = lambdaQuery().eq(Cart::getUserId, userId).count();
    int maxItems = cartProperties.getMaxItems();
    if (count >= maxItems) {
        throw new BizIllegalException(StrUtil.format("用户购物车数量不能超过{}", maxItems));
    }
}
```



nacos

![image-20240111160727083](../../assert/Day05-Nacos%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/image-20240111160727083.png)

### 动态路由

之前的网关全部采用静态路由, 所有配置项都要再application.yml中配置, 太过麻烦

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: item-service
          uri: lb://item-service
          predicates:
            - Path=/items/**,/search/**
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
```



#### 引入依赖

```XML
<!--统一配置管理-->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
<!--加载bootstrap-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
```



#### 创建ConfigService

>   为的是连接到Nacos



由于我们采用了`spring-cloud-starter-alibaba-nacos-config`自动装配, 所以`ConfigService`已经在`com.alibaba.cloud.nacos.NacosConfigAutoConfiguration`中自动创建好了



![image-20240116120559235](../../assert/Day05-Nacos%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/image-20240116120559235.png)



-   因此，只要我们拿到`NacosConfigManager`就等于拿到了`ConfigService`，就算是创建ConfigService

#### 添加配置监听器

>编写配置变更的通知处理逻辑

使用的API

```java
ConfigService configService = nacosConfigManager.getConfigService();
String dataId = "";
String groupId = "";
Long timeoutMs = 1000L;

String configAndSignListener = configService.getConfigAndSignListener(
        dataId,
        groupId,
        timeoutMs,
        MyListener
);
```

代码

```java
/**
 * 监听器
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-01-12 15:24
 */
@Configuration
@Slf4j
public class DybanucRouteLoaderListener {
    @Autowired
    private RouteDefinitionWriter writer;
    @Autowired
    private NacosConfigManager nacosConfigManager;

    // 路由配置文件的id和分组
    private final String dataId = "gateway-routes.json";
    private final String group = "DEFAULT_GROUP";
    // 保存更新过的路由id
    private final Set<String> routeIds = new HashSet<>();

    @PostConstruct// javax注解
    public void initRouteConfigListener() throws NacosException {
        // 1.注册监听器并首次拉取配置
        String configInfo = nacosConfigManager.getConfigService()
                .getConfigAndSignListener(dataId, group, 5000,
                        new Listener() {
                            @Override
                            public Executor getExecutor() {
                                return null;
                            }

                            @Override
                            public void receiveConfigInfo(String configInfo) {
                                updateConfigInfo(configInfo);
                            }
                        });
        // 2.首次启动时，更新一次配置
        updateConfigInfo(configInfo);
    }

    private void updateConfigInfo(String configInfo) {
        log.debug("监听到路由配置变更，{}", configInfo);
        // 1.反序列化
        List<RouteDefinition> routeDefinitions = 
            JSONUtil.toList(configInfo, RouteDefinition.class);
        // 2.更新前先清空旧路由
        // 2.1.清除旧路由
        for (String routeId : routeIds) {
            // 在RouteDefinitionWriter里删除
            writer.delete(Mono.just(routeId)).subscribe();
        }
        routeIds.clear();
        // 2.2.判断是否有新的路由要更新
        if (routeDefinitions==null||routeDefinitions.isEmpty()) {
            // 无新路由配置，直接结束
            return;
        }
        // 3.更新路由
        routeDefinitions.forEach(routeDefinition -> {
            // 3.1.更新路由
            writer.save(Mono.just(routeDefinition)).subscribe();
            // 3.2.记录路由id，方便将来删除
            routeIds.add(routeDefinition.getId());
        });
    }
}
```

-   **Mono.defer**方法创建数据源属于**懒汉型**，**Mono.just**方法创建数据源属于**饿汉型**
-   首先是Mono.just()，直接由这个对象构造出一个Mono。

#### 更新路由

`org.springframework.cloud.gateway.route.RouteDefinitionWriter`



配置

```yml
- id: item-service
          uri: lb://item-service
          predicates:
            - Path=/items/**,/search/**
```

转json

```json
{
  "id": "item-service",
  "uri": "lb://item-service",
  "filters": [],
  "predicates": [{
    "name": "Path",
    "args": {
      "_pattern_0": "/items/**",
      "_pattern_1": "/search/**"
    }
  }]
}
```

-   id：路由id
-   predicates：路由匹配规则
-   filters：路由过滤器
-   uri：路由目的地



需要有一个路由的配置类和一个路由的监听类

```json
[{
        "id": "item-service",
        "uri": "lb://item-service",
        "filters": [],
        "predicates": [{
            "name": "Path",
            "args": {
                "_pattern_0": "/items/**",
                "_pattern_1": "/search/**"
            }
        }]
    },
    {
        "id": "user-service",
        "uri": "lb://user-service",
        "filters": [],
        "predicates": [{
            "name": "Path",
            "args": {
                "_pattern_0": "/users/**",
                "_pattern_1": "/addresses/**"
            }
        }]
    },
    {
        "id": "cart-service",
        "uri": "lb://cart-service",
        "filters": [],
        "predicates": [{
            "name": "Path",
            "args": {
                "_pattern_0": "/carts/**"
            }
        }]
    },
    {
        "id": "trade-service",
        "uri": "lb://trade-service",
        "filters": [],
        "predicates": [{
            "name": "Path",
            "args": {
                "_pattern_0": "/orders/**"
            }
        }]
    },
    {
        "id": "pay-service",
        "uri": "lb://pay-service",
        "filters": [],
        "predicates": [{
            "name": "Path",
            "args": {
                "_pattern_0": "/pay-orders/**"
            }
        }]
    }
]
```

#### 测试



![image-20240116123628643](../../assert/Day05-Nacos%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/image-20240116123628643.png)



访问[localhost:8080/search/list](http://localhost:8080/search/list?pageNo=1&pageSize=1)

![image-20240116124502212](../../assert/Day05-Nacos%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/image-20240116124502212.png)
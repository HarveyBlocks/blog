# 登录校验

-   登录认证授权放在user-service不变
-   登录校验放在网关

## 网关执行流程

>   思考如何在路由转发之前完成登录校验

![spring_cloud_gateway_diagram.png (443×595)](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/微服务和分布式/spring-cloud/网关与配置管理/Day04-登录校验/spring_cloud_gateway_diagram.png)

-   `Handler Mapping`

    路由映射器

    -   默认实现是`RoutePredicateHandlerMaping`
    -   `HandlerMapping`根据请求到的匹配的路由并存入上下文

-   `Web Handler`

    请求处理器

    -   默认实现是`FilteringWebHandler`

        过滤器处理器

        会加载网关中配置的多个过滤器, 放入集合中并排序, 形成**过滤器链`FilterChain`**

        然后依次执行**过滤器链**

-   `Filter Chain`

    过滤器链

    -   具有**Pre**和**Post**两部分逻辑逻辑
        -   `Pre`逻辑执行失败, 直接结束
    -   自定义过滤器
        -   增加**身份校验的过滤器**

    -   特殊的过滤器`NettyRoutingFilter`
        -   无需配置
        -   对所有路由生效
        -   放在过滤器链的最后
        -   执行对**微服务的代理**

-   `Proxied Service`

    服务代理

    -   向目标路径的微服务发送请求, 并响应目标微服务返回的响应

## 自定义身份校验过滤器

>JWT校验

### 分析

-   自定义过滤器

-   Q: 网关需要传递用户信息给微服务

    A:  将用户信息保存到请求头

-   Q: 微服务之间需要传递用户信息

    A:  

### 自定义过滤器

#### 两种过滤器

-   `GatewayFilter`

    -   路由过滤器

    -   作用于任意指定的路由

    -   默认不生效, 配置路由后生效

    -   例如: `Spring Cloud Gateway`提供的过滤器

    -   源码

        ```java
        public interface GatewayFilter extends ShortcutConfigurable {
            String NAME_KEY = "name";
            String VALUE_KEY = "value";

            Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain);
        }
        ```

-   `GlobalFilter`

    -   全局过滤器

    -   作用范围是所有路由

    -   声明后自动生效

    -   源码

        ```java
        /**
         * 执行Web请求并(可选)通过给定的参数{GatewayFilterChain}执行下一个WebFilter
         * @param exchange 提供网关内部的共享数据(request,response,session,或自定义共享属性)
         * @param chain 提供一种执行下一个过滤器的方式
         * @return 请求执行完成后, 将回调Mono, 执行Mono中的Post逻辑(非阻塞式编程思想, 减少filter方法的等待时间),怎么实现查官方please
         */
        public interface GlobalFilter {
            Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain);
        }
        ```

        [Global Filter](https://docs.spring.io/spring-cloud-gateway/docs/3.1.8/reference/html/#global-filters)

#### 过滤器排序

-   `org.springframework.core.Ordered`

[Global Filter](https://docs.spring.io/spring-cloud-gateway/docs/3.1.8/reference/html/#global-filters)

```Java
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("custom global filter");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;// 越小, 优先级越高,越早执行
    }
}
```

-   `NettyRoutingFilter`的优先级

    ![image-20240110161728900](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/微服务和分布式/spring-cloud/网关与配置管理/Day04-登录校验/image-20240110161728900.png)

#### 自定义`GlobalFilter`

-   实现两个接口

    ```java
    /**
     * 校验用户
     *
     * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
     * @version 1.0
     * @date 2024-01-10 15:46
     */
    @Component
    public class UserVerifyFilter implements GlobalFilter , Ordered{...}
    ```

-   实现`GlobalFilter`方法, 模拟登录校验逻辑

    ```java
    /**
     * TODO 模拟登录校验逻辑
     * @param exchange 提供网关内部的共享数据
     * @param chain 提供一种执行下一个过滤器的方式
     * @return 将回调的Mono
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 从请求头获取登录凭证
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        System.out.println(headers.keySet());
        // [..., authorization,...]决定就是你了 !
        // 登录凭证做校验
        if(false){
            System.out.println("你滴, 没滴登录滴干活");
            return null;
        }
        // 对于已登录的请求,将用户上下文放入请求头
        // headers.put("userContext", List.of("用户上下文"));

        // 放行
        return chain.filter(exchange);
    }
    ```

-   实现`Ordered`, 定义优先级

    ```java
    @Override
    public int getOrder() {
        // 提高此过滤器的优先级(比Netty高)
        return 0;
    }
    ```

#### 自定义`GatewayFilter`

-   使用继承**过滤器工厂**`AbstractGatewayFilterFactory`的方式实现自定义GatewayFilter

-   做配置

    ```yaml
    spring: 
      cloud: 
        gateway: 
          default-filters:
            - MyTest=arg1,arg2
            - MyNoArgTest
    ```

    因为参数不同, 每一个过滤器对象也不同

    工厂类读取配置创建一个定制的工厂类对象

-   过滤器工厂类的名字,如`MyTestGatewayFilterFactor`, 

    -   **必须以`GatewayFilterFactor`结尾**
    -   其剩余前缀将作为**配置的key(见上)**
    -   `MyNoArgTestGatewayFilterFactor`也是合法的

-   实现无参的GatewayFilter

    -   Java代码

        ```java
        @Component
        public class MyTestGatewayFilterFactory extends AbstractGatewayFilterFactory {
            @Override
            public GatewayFilter apply(Object config) {
                System.out.println("MyTestGatewayFilterFactory#apply");

                // OrderedGatewayFilter使用了装饰模式. 将GatewayFilter作为delegate(委托)
                return new OrderedGatewayFilter(
                        new GatewayFilter() {
                            @Override
                            public Mono<Void> filter(ServerWebExchange exchange,
                                                     GatewayFilterChain chain) {
                                System.out.println(
                                    "MyTestGatewayFilterFactory.GatewayFilter#filter");
                                return chain.filter(exchange);
                            }
                        },// 匿名内部类, 小子
                        -1);
            }

        }
        ```

    -   配置

        ```yaml
        gateway:
          routes:
        	- id...
          default-filters:
              - MyTest
        ```

    -   测试

        -   在启动时

            ![image-20240110170350953](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/微服务和分布式/spring-cloud/网关与配置管理/Day04-登录校验/image-20240110170350953.png)

            突然发现Nacos每次向我们请求都会调用好多次FilterFactory耶

        -   前端请求一下

            ```
            MyTestGatewayFilterFactory#apply
            MyTestGatewayFilterFactory#apply
            MyTestGatewayFilterFactory#apply
            MyTestGatewayFilterFactory#apply
            MyTestGatewayFilterFactory#apply
            MyTestGatewayFilterFactory.GatewayFilter#filter
            [Host, Connection, Accept, authorization, User-Agent, Sec-Fetch-Site, Sec-Fetch-Mode, Sec-Fetch-Dest, Referer, Accept-Encoding, Accept-Language, Cookie]
            authorization = null
            MyTestGatewayFilterFactory#apply
            MyTestGatewayFilterFactory#apply
            MyTestGatewayFilterFactory#apply
            MyTestGatewayFilterFactory#apply
            MyTestGatewayFilterFactory#apply
            ```

            工厂在Pre,Post逻辑中都各自调用五次(应该时需要注册的服务的数量,不包含Gateway)

            返回值只在FilterChain中Pre时执行一次

            Global直接执行

    -   实现有参的GatewayFilter

        -   Java代码

            ```java
            @Component
            public class MyTestGatewayFilterFactory extends 
                AbstractGatewayFilterFactory<MyTestGatewayFilterFactory.Config> {//改泛型
                /**
                 * 改参数类型
                 */
                @Override
                public GatewayFilter apply(Config config) {
                    ... 见上
                }

                /**
                 * 专门的属性类与参数进行匹配
                 * 内部类,小子
                 */
                @Getter
                @Setter
                public static class Config{
                    private String arg1;
                    private String arg2;
                    private String arg3;

                    @Override
                    public String toString() {
                        return "Config{" +
                                "arg1='" + arg1 + '\'' +
                                ", arg2='" + arg2 + '\'' +
                                ", arg3='" + arg3 + '\'' +
                                '}';
                    }
                }

                /**
                 * 参数和内部类属性的映射关系
                 * @return 有序集合list,实现一一对应
                 */
                @Override
                public List<String> shortcutFieldOrder() {
                    return List.of("arg1","arg2","arg3");
                }

                /**
                 * 父类将依据Config类解析yaml文件
                 */
                public MyTestGatewayFilterFactory(){
                    super(Config.class);
                }
            }
            ```

        -   配置

            ```yaml
            gateway:
              routes:
            	- id...
              default-filters:
                  - MyTest=
            ```

        -   测试

            ```
            Config{arg1='001', arg2='002', arg3='003'}
            MyTestGatewayFilterFactory#apply
            ```

### 登录校验逻辑

#### 代码

判断是否是需要排除的路径

```Java
@Resource
private AuthProperties authProperties;

// Spring提供路径解析器
private final AntPathMatcher antPathMatcher = new AntPathMatcher();
private boolean isExculdePath(String path) {
    for (String pattern : authProperties.getExcludePaths()) {
        if(antPathMatcher.match(pattern,path)){
            return true;
        }
    }
    return false;
}
```

是否登录校验

```java
@Resource
private JwtTool jwtTool;
/**
 * TODO 模拟登录校验逻辑
 * @param exchange 提供网关内部的共享数据
 * @param chain 提供一种执行下一个过滤器的方式
 * @return 将回调的Mono
 */
@Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    // 判断是否需要做登录拦截
    String path = request.getPath().toString();
    if(isExculdePath(path)){
        // 被排除, 就放行
        return chain.filter(exchange);
    }

    // 从请求头获取登录凭证
    List<String> tokens = request.getHeaders().get("authorization");
    String token = null;
    if (tokens != null && tokens.size() == 1){
        token = tokens.get(0);
    }

    // 登录凭证做校验
    // 2.校验token
    ServerHttpResponse response = exchange.getResponse();
    Long userId = null;
    try {
        userId = jwtTool.parseToken(token);// user!=null;
    } catch (Exception e) {
        // UNAUTHORIZED 401
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();// 停止
    }

    // TODO 对于已登录的请求,将用户上下文放入请求头

    // 放行
    return chain.filter(exchange);
}
```

#### 测试

![image-20240110200814038](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/微服务和分布式/spring-cloud/网关与配置管理/Day04-登录校验/image-20240110200814038.png)

401~(当然自动转到登录页面就是前端的事情啦)

![image-20240110200903666](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/微服务和分布式/spring-cloud/网关与配置管理/Day04-登录校验/image-20240110200903666.png)

-   登录之后变成404了

    ![image-20240110201012176](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/微服务和分布式/spring-cloud/网关与配置管理/Day04-登录校验/image-20240110201012176.png)

### 网关传递用户信息

1.  在网关把请求到的登录信息保存到请求头

    ```java
    // 对于已登录的请求,将用户上下文放入请求头
    ServerWebExchange newExchange = exchange
            .mutate()// 突变,改变
            .request(builder -> builder.header(
                Constant.USER_INFO_HEADER_NAME, 
                userId.toString()))
            .build();
    // 放行
    return chain.filter(newExchange);
    ```

2.  在工具模块中准备Interceptor

    ```java
    @Component
    public class LoginInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(
            HttpServletRequest request, 
            HttpServletResponse response, 
            Object handler) throws Exception {...}

        @Override
        public void afterCompletion(
            HttpServletRequest request, 
            HttpServletResponse response, 
            Object handler, Exception ex) throws Exception {...}
    }
    ```

    1.  在Interceptor从请求头解析请求头, 获取用户信息, 并保存到ThreadLocal

        ```java
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            // 1.获取请求头中的 userId
            String userId = request.getHeader(Constants.USER_INFO_HEADER_NAME);
            // 2.存入上下文
            try {
                Long.valueOf(userId);
                UserContext.setUser(Long.valueOf(userId));
            } catch (NumberFormatException ignored) {}
            // 3.放行
            return true;
    }
        ```

    2.  在返回Interceptor时把用户信息消除

        ```java
        @Override
        public void afterCompletion(
            HttpServletRequest request, 
            HttpServletResponse response, 
            Object handler, Exception ex) throws Exception {
            // 清理用户
            UserContext.removeUser();
    }
        ```

    3.  注册Interceptor

        ```java
        @Configuration
        @RequiredArgsConstructor
        public class MvcConfig implements WebMvcConfigurer {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                // 添加拦截器
                LoginInterceptor loginInterceptor = new LoginInterceptor();
                registry.addInterceptor(loginInterceptor);
            }
        }
        ```

    4.  不在同一个目录下, 如何让Spring自动加载Interceptor?

        ![image-20240111124045242](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/微服务和分布式/spring-cloud/网关与配置管理/Day04-登录校验/image-20240111124045242.png)

        配置自动加载

    5.  出现版本冲突

        ![image-20240111124158260](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/微服务和分布式/spring-cloud/网关与配置管理/Day04-登录校验/image-20240111124158260.png)

        原因是Gateway和WebMVC的依赖冲突, 但是Gateway又不需要Interceptor, 所以不需要WebMVC的依赖, Gateway只是需要工具模块下的几个方法罢了

        解决方法:**`@ConditionalOnClass(DispatcherServlet.class)`**注解MvcConfig

        ```Java
        @Configuration
        @RequiredArgsConstructor
        @ConditionalOnClass(DispatcherServlet.class)
        public class MvcConfig implements WebMvcConfigurer {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                // 添加拦截器
                LoginInterceptor loginInterceptor = new LoginInterceptor();
                registry.addInterceptor(loginInterceptor);
            }
        }
        ```

        为了识别Gateway和其他需要Interceptor的微服务的区别, 就在于其他微服务需要**DispatcherServlet**而Gateway不需要

### 微服务之间传递用户信息

>   基于OpenFeign

利用OpenFeign提供的一个**拦截器**接口, 所有由OpenFeign发起的请求都会**先调用拦截器**处理请求

```java
public class ClientConfig {
    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return new RequestInterceptor() {
            /**
             * 传递用户信息
             * @param template 有大量对请求做修改的api
             */
            @Override
            public void apply(RequestTemplate template) {
                String userId = String.valueOf(UserContext.getUser());
                if (userId != null && !userId.isEmpty()) {
                    template.header(Constants.USER_INFO_HEADER_NAME, userId);
                }
                /*
                解决一下长期以来对存取空字符串和null值的疑问
                取
                public String getHeader(String name) {
        			MessageBytes mh = this.getValue(name);
        			return mh != null ? mh.toString() : null;
    			}
    			存
    		  	public RequestTemplate header(String name, Iterable<String> values) {
    				if (name == null || name.isEmpty()) {
      					throw new IllegalArgumentException("name is required.");
    				}
    				if (values == null) {
      					values = Collections.emptyList();
    					}
    					return appendHeader(name, values);
  					}
                */
            }
        };
    }
}

```

当然在需要用到ClientConfig的块注册一下ClientConfig

```java
@EnableFeignClients(clients = {ItemClient.class},defaultConfiguration = ClientConfig.class)
```

```java
@EnableFeignClients(clients = {ItemClient.class},defaultConfiguration = ClientConfig.class)
@MapperScan("com.hmall.cart.mapper")
@SpringBootApplication
public class CartServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartServiceApplication.class, args);
    }
}
```


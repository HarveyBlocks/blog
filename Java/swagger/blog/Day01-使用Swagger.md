# Swagger使用

## 引入依赖

-   引入经过封装的SpringFox依赖

    ```xml
    <!--swagger-->
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger2</artifactId>
        <version>2.9.2</version>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger-ui</artifactId>
        <version>2.9.2</version>
    </dependency>
    ```

    没有被boot管理



## 启动注解

```java
@MapperScan("com.harvey.review_system.mapper")
@SpringBootApplication
@EnableSwagger2 // 扫描当前包及子包中所有类型中的,swagger相关注解
public class ReviewSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReviewSystemApplication.class, args);
    }
}
```



## 启动UI

[Swagger UI](http://localhost:8081/swagger-ui.html)

![image-20240117153200586](../assets/Day01-%E4%BD%BF%E7%94%A8Swagger/image-20240117153200586.png)

![image-20240117153407780](../assets/Day01-%E4%BD%BF%E7%94%A8Swagger/image-20240117153407780.png)

-   basic-error-controller是spring提供的

## 使用UI

Swagger扫描的是@Controller, 以及其@XxxMapping的方法. 然后解析注解的映射地址, 请求方式, 返回值, 参数等



## 基础配置

### 定制标题等

![image-20240117155718480](../assets/Day01-%E4%BD%BF%E7%94%A8Swagger/image-20240117155718480.png)





```java
@Configuration
public class SwaggerConfig {
    /**
     * 创建Docker对象
     *
     * @return Docket是Swagger中的全局配置对象
     */
    @Bean
    public Docket docket() {
        // 指定docket配置的是Springfox中整合的哪一个框架
        Docket docket = new Docket(DocumentationType.SWAGGER_2);
        // 配置

        // 配置swagger文档主体内容
        Contact contact = new Contact(
                "Harvey Block",// 作者
                "http://centos:9200",// url
                "harvey.blocks@outlook.com"// email
        );
        // 指定配置信息
        ApiInfo apiInfo = new ApiInfoBuilder()
                .contact(contact)
                .description("这是一个测评系统\n的swagger<br>接口文档")
                .version("1.9.19")//恶臭捏~(￣▽￣)~*
                .title("review-system")
                .build();
        docket.apiInfo(apiInfo);

        // 返回docket配置
        return docket;
    }
}
```

![image-20240117161401625](../assets/Day01-%E4%BD%BF%E7%94%A8Swagger/image-20240117161401625.png)

### 根据环境判断是否生成文档

```java
docket = docket.enable(environment.acceptsProfiles(Profiles.of("dev","test"))).build();
```

### 定制扫描包

`Predicate`规则

```java
String controllersPath = "com.harvey.review_system.controller";
// 获取Docket中的选择器ApiSelectorBuilder. 用于配置诸如扫描哪个包的注解
docket = docket.select()
        .apis(
                /*需要的参数是扫描哪些包(及其子包)的规则类型对象*/
                RequestHandlerSelectors.basePackage(controllersPath)
        ).build();// 重新构建对象
```

### 不生成文档

```java
// 获取Docket中的选择器ApiSelectorBuilder,选择哪些方法或控制器需要生成接口文档
docket = docket.select()
        // 配置扫描哪个包的注解
        .apis(...)
        // 配置不需要被生成文档的控制器/方法
        .apis(Predicates.not(
                        RequestHandlerSelectors.withMethodAnnotation(
                                IgnoreApi.class
                                // 自定义的注解(springfox提供了一个ApiIgnore)
                        )
                )
        ).build();
```



![image-20240117170303923](../assets/Day01-%E4%BD%BF%E7%94%A8Swagger/image-20240117170303923.png)

only two



-   使用and逻辑

    ```java
    docket = docket.select().apis(
            Predicates.and(
                    Predicates.not(
                            RequestHandlerSelectors.withMethodAnnotation(IgnoreApi.class)),
                    RequestHandlerSelectors.basePackage(controllersPath)))
            .build(); // 重新构建对象
    ```



### 路径指定文档生成

```java
docket = docket.select()
        .paths(// 使用正则表达式约束使用API文档的路径地址, /.*等于/**,只有符合规则的路径,才生成api文档
                PathSelectors.regex("/shop-type/.*"))
        .build(); // 重新构建对象
```


![image-20240117171420106](../assets/Day01-%E4%BD%BF%E7%94%A8Swagger/image-20240117171420106.png)



## 注解

### @ApiIgnore

可方法, 可类型, 不生成帮助文档

### @Api

```java
@Api(tags = {"测试Controller","测试Swagger"},description = "描述API")
public class HelloController {...}
```



![image-20240117190209102](../assets/Day01-%E4%BD%BF%E7%94%A8Swagger/image-20240117190209102.png)

-   就会有俩

### @ApiOperation

```java
@ApiOperation(value = "测试Post请求",notes = "post请求方法")
@PostMapping("/post")
public String post(){
    return "post";
}
```

![image-20240117190559637](../assets/Day01-%E4%BD%BF%E7%94%A8Swagger/image-20240117190559637.png)

### @ApiParam

```java
@ApiOperation("测试Get请求")
@GetMapping("/get")
public String get(
        @ApiParam(value = "a的value",
                name = "a的name",
                required = true/*请求参数是否有必要, 默认为假*/
                 ) String a,
        @ApiParam("这是b") String b) {
    return "get a=" + a + " & b=" + b;
}
```

![image-20240117191124313](../assets/Day01-%E4%BD%BF%E7%94%A8Swagger/image-20240117191124313.png)



### @ApiImplicitParam(s)

```java
@ApiOperation(value = "测试Post请求", notes = "post请求方法")
@PostMapping("/post")
@ApiImplicitParams({
        @ApiImplicitParam(name = "m",value = "一个字符串m",
                          type = "字符串",required =true,paramType = "键值对"),
        @ApiImplicitParam(name = "参数n"),
})
public String post(String m,String n) {
    return "post";
}
```

![image-20240117193525854](../assets/Day01-%E4%BD%BF%E7%94%A8Swagger/image-20240117193525854.png)

`@ApiImplicitParam(s)`和`@ApiParam`

### 实体注解

```java
@ApiModel(value = "Result实体", description = "返回结果")
// 当一个实体作为方法的返回值的时候, 该注解被解析
public class Result {
    @ApiModelProperty(
            value = "错误信息", name = "error message",
            required = true, notes = "这是标注",
            hidden = false, example = "发生了一个异常!")
    private String errorMsg;
    
    private Boolean success;
    private Object data;
    private Long total;
	... 
}
```

![image-20240117195829252](../assets/Day01-%E4%BD%BF%E7%94%A8Swagger/image-20240117195829252.png)

![image-20240117195921877](../assets/Day01-%E4%BD%BF%E7%94%A8Swagger/image-20240117195921877.png)


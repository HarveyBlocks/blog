## spring-mvc.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"

       xsi:schemaLocation=
               "http://www.springframework.org/schema/beans
                http://www.springframework.org/schema/beans/spring-beans.xsd
                http://www.springframework.org/schema/context
                http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/mvc https://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <!--组件扫描-->
    <context:component-scan base-package="com.harvey.controller"/>

    <!--文件上传的请求的接收,解析和文件拷贝-->
    <bean id="multipartResolver"
          class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
        <!--        <property name="defaultEncoding" value="UTF-8"/>&lt;!&ndash;文件的编码格式 默认是ISO8859-1&ndash;&gt;-->
        <!--        <property name="maxUploadSizePerFile" value="1048576"/>&lt;!&ndash;上传文件的大小限制,单位字节&ndash;&gt;-->
        <!--        <property name="maxUploadSize" value="3145728"/>&lt;!&ndash;上传文件的总大小&ndash;&gt;-->
        <!--        <property name="maxInMemorySize" value="1048576"/>&lt;!&ndash;上传文件的缓存大小&ndash;&gt;-->
    </bean>

    <mvc:annotation-driven/>

    <mvc:interceptors>
        <mvc:interceptor>
            <!--对请求的路径进行拦截-->
            <mvc:mapping path="/**"/>
            <bean class="com.harvey.interceptor.MyInterceptor0"/>
        </mvc:interceptor>

        <mvc:interceptor>
            <!--对请求的路径进行拦截-->
            <mvc:mapping path="/**"/>
            <bean class="com.harvey.interceptor.MyInterceptor1"/>
        </mvc:interceptor>
        <mvc:interceptor>
            <!--对请求的路径进行拦截-->
            <mvc:mapping path="/**"/>
            <bean class="com.harvey.interceptor.MyInterceptor2"/>
        </mvc:interceptor>
    </mvc:interceptors>
</beans>
```

### 自定义Bean(Controller)的配置

>   包扫描

```java
package com.harvey.config;

import ...

/**
 * ...
 */
@Configuration
@ComponentScan("com.harvey.controller")
public class SpringMVCConfig {
}
```

### 第三方Bean(CommonsMultipartResolver)的配置

>   @Bean注解方法,返回值为该类型

```java
package com.harvey.config;

import ...

/**
 * ...
 */
@Configuration
@ComponentScan("com.harvey.controller")
@PropertySource("classpath:resolver.properties")
public class SpringMVCConfig {

    @Bean
    public CommonsMultipartResolver multipartResolver(
            // 介于@Bean如果不命名是以方法名为Bean的名字的,这个方法名一定要相当注意
            // 因为这个网络文件请求工具的Bean的名字一定钥匙multipartResolver(还记得吗?)
            @Value("${resolver.defaultEncoding}") String charset,
            @Value("${resolver.maxUploadSizePerFile}") long maxUploadSizePerFile,
            @Value("${resolver.maxUploadSize}") long maxUploadSize,
            @Value("${resolver.maxInMemorySize}") int maxInMemorySize
    ) {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        //文件的编码格式 默认是ISO8859-1
        resolver.setDefaultEncoding(charset);
        //上传文件的大小限制,单位字节
        resolver.setMaxUploadSizePerFile(maxUploadSizePerFile);
        //上传文件的总大小
        resolver.setMaxUploadSize(maxUploadSize);
        //上传文件的缓存大小
        resolver.setMaxInMemorySize(maxInMemorySize);
        return resolver;
    }

}
```

### 非Bean的配置

#### `<mvc:annotation-driven/>`的配置

>   @EnableWebMvc注解配置类

**@EnableWebMvc的源码:**

![image-20231129232248290](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC全注解开发/Day05-消除spring-mvc.xml/image-20231129232248290.png)

**DelegatingWebMvcConfiguration的源码:**

![image-20231129232343443](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC全注解开发/Day05-消除spring-mvc.xml/image-20231129232343443.png)

**WebMvcConfigurationSupport的源码:**

![image-20231129232517689](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC全注解开发/Day05-消除spring-mvc.xml/image-20231129232517689.png)

-   资源映射器,get

![image-20231129232432189](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC全注解开发/Day05-消除spring-mvc.xml/image-20231129232432189.png)

-   视图解析器,get

#### 拦截器的配置

**DelegatingWebMvcConfiguration的源码:**

![image-20231129233036748](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC全注解开发/Day05-消除spring-mvc.xml/image-20231129233036748.png)

-   附:@Autowire知识点

```java
/*
*  要求容器找UserDao的Bean,有几个找几个
* */
@Autowired void yyy(List<UserDao> userDaoList222){
    System.out.println("yyy:"+userDaoList222);
}
```

输出:`yyy:[UserDaoImpl{}, UserDaoImpl2{}, UserDaoImpl3{}]`

**WebMvcConfigurer的源码:**

![image-20231129233141469](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC全注解开发/Day05-消除spring-mvc.xml/image-20231129233141469.png)

![image-20231129233223082](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC全注解开发/Day05-消除spring-mvc.xml/image-20231129233223082.png)

>   实现WebMvcConfigurer接口

#### `<mvc:default-servlet-handler/>`的配置

**WebMvcConfigurer的源码:**

![image-20231129233640118](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC全注解开发/Day05-消除spring-mvc.xml/image-20231129233640118.png)

#### 配置非Bean

1.  注解@EnableWebMvc解决`<mvc:annotation-driven/>`的配置

    ```java
    @Configuration
    @ComponentScan({"com.harvey.controller"})
    @PropertySource("classpath:resolver.properties")
    @EnableWebMvc
    public class SpringMVCConfig {...}
    ```

2.  编写MyWebMvcConfig,注册Interceptor,开启默认的Servlet处理器

    ```java
    public class MyWebMvcConfig implements WebMvcConfigurer {
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            // 加入Interceptor
            InterceptorRegistration interceptorRegistration = registry.addInterceptor(new MyInterceptor0());
            // 配置拦截路径规则
            interceptorRegistration.addPathPatterns("/**");
            registry.addInterceptor(new MyInterceptor1()).addPathPatterns("/**");
            // 先注册先执行,后注册后执行s
        }

        @Override
        public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
            // 开启默认的Servlet处理器
            configurer.enable();
        }
    }
    ```

3.  将MyWebMvcConfig放入核心配置类

    这里我想了半天: 这是一个类,放在config包下,config包下的类大都是不能被实例化为Bean的,那么又该怎么把MyWebMcConfig放入Spring容器呢?

    ```java
    @Configuration
    @ComponentScan({"com.harvey.controller"})
    @PropertySource("classpath:resolver.properties")
    @Import(MyWebMvcConfig.class)
    @EnableWebMvc
    public class SpringMVCConfig {...}
    ```

    这个故事告诉我们**基础不牢,地动山摇**

4.  最后也要像spring.xml一样,把spring-mvc.xml注册到**AnnotationConfigWebApplicationContext**(实质上是他的子类)离去

    ```java
    public class MyAnnotationConfigWebApplicationContext extends 
        AnnotationConfigWebApplicationContext {
        public MyAnnotationConfigWebApplicationContext() {
            super();//爱写不写,老知识点了
            super.register(SpringConfig.class);
            super.register(SpringMVCConfig.class);//this,super都一样,继承关系之后,子类不重写父类方法就会调用父类方法
        }
    }
    ```

    理由是为了使用核心配置类的`<param-name>contextClass</param-name>`,需要**AnnotationConfigWebApplicationContext**

5.  把**AnnotationConfigWebApplicationContext**的子类配置到web.xml

    ```xml
    <servlet>
        <servlet-name>DispatcherServlet</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <!--初始化时,依据核心配置类,创建容器-->
        <init-param>
            <param-name>contextClass</param-name>
            <param-value>com.harvey.config.MyAnnotationConfigWebApplicationContext</param-value>
        </init-param>
    </servlet>
    ```

![image-20231130004343898](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC全注解开发/Day05-消除spring-mvc.xml/image-20231130004343898.png)

-   删了俩


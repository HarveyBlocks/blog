本文件错误,数据全部丢失,只剩下一张图片



##spring.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"

       xsi:schemaLocation=
               "http://www.springframework.org/schema/beans
                http://www.springframework.org/schema/beans/spring-beans.xsd
                http://www.springframework.org/schema/context
                http://www.springframework.org/schema/context/spring-context.xsd">
    <!--组件扫描-->
    <context:component-scan base-package="com.harvey.service"/>

</beans>
```

-   简单

```java
package com.harvey.config;

import ...

/**
 * @description 配置类
 */
@Configuration
@ComponentScan("com.harvey.service")
public class SpringConfig{

}
```

```java
public class MyAnnotationConfigWebApplicationContext extends AnnotationConfigWebApplicationContext {
    public MyAnnotationConfigWebApplicationContext() {
        super();//爱写不写,老知识点了
        this.register(SpringConfig.class);
    }
}
```

-   web.xml

```xml
<!--配置ContextLoaderListener的初始化参数-->
<context-param>
    <param-name>contextClass</param-name>
    <param-value>com.harvey.config.MyAnnotationConfigWebApplicationContext</param-value>
</context-param>
```




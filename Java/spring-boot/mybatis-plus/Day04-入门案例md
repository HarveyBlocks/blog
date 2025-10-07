# MyBatisPlus

>   ***TO BE THE BEST PARTNER OF MYBATIS***

```xml
<dependency>
  <groupId>com.baomidou</groupId>
  <artifactId>mybatis-plus-boot-starter</artifactId>
  <version>3.5.3.1</version>
</dependency>
```





```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>


<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.3.2</version>
</dependency>


<!--集成mysql数据库-->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.26</version>
</dependency>

<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.2.13</version>
</dependency>
```

集成了MyBatis和MyBatisPlus

注意要及时删除MyBatis和spring-mybatis的依赖以免八百年冲突

## 改造Mapper接口

为了让原来的Mapper接口使用MyBatisPlus的好用方法,就让我们的Mapper继承它的类

```java
public interface UserMapper extends BaseMapper<User>{
    ...
}
```



```java
@Configuration
public class MapperConfig {



    @Bean
    public DataSource dataSource(){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setPassword("123456");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setUrl("jdbc:mysql://localhost:3306/company");
        return dataSource;
    }



    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource)
            throws Exception {
        final MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        return sessionFactory.getObject();
    }
}
```

## 映射表名

###默认匹配方法

类名的驼峰转下划线为表名

| 类名                          | 表名                |
| ----------------------------- | ------------------- |
| User                          | user                |
| UserInfo                      | user_info           |
| updateTime                    | update_time         |
| isMarried(is 开头且是Boolean) | married(会吧is去掉) |
| order                         | 关键字?(叫这名吗?)  |
|                               |                     |

### 自定义表名

-   `@TableName`->指定表名

-   `@TableId`->指定主键字段名

    -   属性type = (enum IdType)

        设置策略如下:

        ```java
        public enum IdType {
            AUTO(0),//自增长
            NONE(1),
            INPUT(2),//自行输入
            ASSIGN_ID(3),//雪花算法分配ID,默认,一定要记得改成自增长
            ASSIGN_UUID(4);
        
            private final int key;
        
            private IdType(int key) {
                this.key = key;
            }
        
            public int getKey() {
                return this.key;
            }
        }
        
        ```

-   `@TableField`->指定表中普通字段的信息

    -   `@TableField("is_married")`

    -   对于是关键字的字段,加("\`order\`")这样,不是关键字也可以加

    -   类中的字段表中不存在的

        `@TableField(exist=false)`



##实体类的属性

类型应该是包装类方便反射

## 常用配置

```yaml
# MP常用配置
mybatis-plus:
  # 扫包别名
  type-aliases-package: com.harvey.mybatis.plus.pojo
  # mapper目录,及其子目录下的所有xml文件都将作为mapper文件,默认如下:
  # 经测试失败
  mapper-locations: "classpath*:/mapper/**/*.xml"
  configuration:
    # 是否开启下划线和驼峰的映射,默认为true
    map-underscore-to-camel-case: true
    # 是否开启二级缓存,默认为false
    cache-enabled: false
  global-config:
    db-config:
      # 全局雪花(默认)生成id
      id-type: assign_id
      # 更新策略: 只更新非空字段(默认). 实体类里有哪些字段, 就更新哪些字段,字段为null就不更新
      update-strategy: not_null
```

-   注解的优先级更高
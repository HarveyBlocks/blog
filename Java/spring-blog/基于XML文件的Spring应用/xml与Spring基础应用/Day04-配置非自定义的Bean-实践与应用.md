# 配置非自定义的Bean

-   第三方Jar包中的对象配成Bean

想想实例化方式
-   构造方法
    -   有参构造
    -   无参构造
-   工厂方式
    -   静态工厂方式
    -   实例工厂方式
    -   FactoryBean规范延迟实例化Bean

是否需要必要的注入

##  配实例化对象-以Druid为例

-   DruidTest.java

    ```java
    @Test
    public void druidTest(){
        TestLogger.info("==================自己写的====================");
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        TestLogger.info(dataSource);

        TestLogger.info("==============Spring帮我们管理的================");
        DruidDataSource druidDataSource =
                (DruidDataSource)
                        new ClassPathXmlApplicationContext(
                                "DruidBeans.xml"
                        ).getBean(
                                "druidDataSource"
                        );
        TestLogger.info(druidDataSource);
    }
    ```

-   DruidBeans.xml

    ```xml
    <!--配置数据源-->
    <bean id="druidDataSource" name="DruidDataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306"/>
        <property name="username" value="root"/>
        <property name="password" value="123456"/>
    </bean>
    ```

## 配静态工厂-以Connection为例

-   jarBeanTest.java

    ```java
    @Test
    public void connectTest() throws ClassNotFoundException, SQLException {
        Class<?> aClass = Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn =
                DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/company",
                        "root",
                        "123456"
                );//类似静态工厂

        TestLogger.info(conn);
        conn.close();
        TestLogger.LOGGER.info("==========================");

        Connection conn2 =
                (Connection) new ClassPathXmlApplicationContext("JarBeans.xml")
                        .getBean("connection");

        TestLogger.info(conn2);
        conn2.close();
    }
    ```

-   jarBeans.xml

    ```xml
    <bean id="clazz" class="java.lang.Class" factory-method="forName">
        <constructor-arg name="className" value="com.mysql.cj.jdbc.Driver"/>
    </bean>
    <bean id="connection" class="java.sql.DriverManager" factory-method="getConnection" scope="prototype">
        <constructor-arg name="url" value="jdbc:mysql://localhost:3306/company"/>
        <constructor-arg name="user" value="root"/>
        <constructor-arg name="password" value="123456"/>
    </bean>
    ```

## 配实例工厂方法-以格式化Date为例

-   使用Spring管理Date

-   jarBeanTest.java

    ```java
    @Test
    public void testData() throws ParseException {
        //目标:将一个时间的字符串转化成Date交给Spring来管理
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//类似实例工厂
        Date parse = sdf.parse("2023-11-2 12:00:00");//类似实例工厂方法
        TestLogger.info(parse);
        Date date  =
                (Date) new ClassPathXmlApplicationContext("JarBeans.xml")
                        .getBean("date");
        TestLogger.info(date);

    }
    ```

-   jarBeans.xml

    ```xml
    <bean id="sdf" class="java.text.SimpleDateFormat" >
        <constructor-arg name="pattern" value="yyyy-MM-dd HH:mm:ss"/>
    </bean>
    <bean id="date" factory-bean="sdf" factory-method="parse">
        <constructor-arg name="source" value="2023-11-2 12:00:00"/>
    </bean>
    ```

## 综合使用:以配置MyBatis为例

-   MyBatis很多东西要写,很无语

-   jarBeanTest.java

    ```java
    @Test
    public void testMybatis() throws IOException {
        //静态工厂方式
        InputStream resource = Resources.getResourceAsStream("mybatis-config.xml");

        //无参构造实例化
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();

        //实例工厂方法
        SqlSessionFactory factory = builder.build(resource);
        //实例工厂方法
        SqlSession session = factory.openSession();
        //实例工厂方法
        UserMapper userMapper = session.getMapper(
                //resource
                UserMapper.class
        );

        TestLogger.info(userMapper.selectAll());

        TestLogger.LOGGER.info("================================");

        try (ClassPathXmlApplicationContext applicationContext = 
                        new ClassPathXmlApplicationContext("JarBeans.xml")) {
            UserMapper userMapper1 = (UserMapper)
                    applicationContext.getBean("userMapper");
            TestLogger.info(userMapper1.selectAll());
        }

    }
    ```

-   jarBeans.xml

    ```xml
    <!--接下来要配置Mybatis-->
    <!--
        //静态工厂方式
        InputStream resource = Resources.getResourceAsStream("mybatis-config.xml");
    -->
    <bean id="resource"
          class="org.apache.ibatis.io.Resources"
          factory-method="getResourceAsStream"
          lazy-init="true">
        <constructor-arg name="resource" value="mybatis-config.xml"/>
    </bean>
    <!--
        // 无参构造实例化
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
    -->
    <bean id="builder"
          class="org.apache.ibatis.session.SqlSessionFactoryBuilder"
          lazy-init="true"/>
    <!--
        // 实例工厂方法
        SqlSessionFactory factory = builder.build(resource);
        SqlSession session = factory.openSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
    -->
    <bean id="factory"
          factory-bean="builder"
          factory-method="build"
          lazy-init="true">
        <constructor-arg name="inputStream" ref="resource"/>
    </bean>

    <bean id="sqlSession"
          factory-bean="factory"
          factory-method="openSession"
          lazy-init="true"
          destroy-method="close"/>
         <!--优雅!实在是太优雅了!-->

    <bean id="userMapperClazz"
          class="java.lang.Class"
          factory-method="forName"
          lazy-init="true">
        <constructor-arg
                name="className"
                value="com.harvey.mapper.UserMapper"/>
    </bean>

    <bean id="userMapper"
          factory-bean="sqlSession"
          factory-method="getMapper"
          lazy-init="true">
        <constructor-arg name="type" ref="userMapperClazz"/>
    </bean>
    ```


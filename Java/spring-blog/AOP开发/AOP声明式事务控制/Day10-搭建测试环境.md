# 搭建转账环境

![image-20231111005627345](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/AOP开发/AOP声明式事务控制/Day10-搭建测试环境/image-20231111005627345.png)

## 前期准备

### 数据库搭建

```mysql
create table tb_account (
    id int primary key auto_increment comment '主键ID',
    account_name varchar(20) ,
    money int
);
insert into tb_account(account_name, money)
value ('tom',5000),
('lucy',5000);
select * from tb_account;
```

### Properties文件

```properties
jdbc.driverClassName=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/company
jdbc.username=root
jdbc.password=123456
```

### Dao层(Mapper接口)

-   转入钱(incrMoney)的方法
-   转出钱(decrMoney)的方法

```java
package com.harvey.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface AccountMapper {
    //加钱,你要搞BigDecimal,但你又不去银行...
    @Update("update tb_account set money=money+#{money} where account_name=#{accountName}")
    public void incrMoney(
            @Param("accountName") String accountName,
            @Param("money") int money);

    //减钱
    @Update("update tb_account set money=money-#{money} where account_name=#{accountName}")
    public void decrMoney(
            @Param("accountName") String accountName,
            @Param("money") int money);
}
```

### Service层

-   事物(transferMoney)方法
    -   其中必须有转入和转出事物的方法,共同构成一个事物

```java
package com.harvey.service.impl;

import com.harvey.mapper.AccountMapper;
import com.harvey.service.AccountService;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;
    @Override
    public void transMoney(String outAccountName, String inAccountName, int money) {
            accountMapper.decrMoney(outAccountName,money);
            accountMapper.incrMoney(inAccountName,money);
    }
}
```

### Bean配置-配置类

```java
package com.harvey.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration
@MapperScan("com.harvey.mapper")
@ComponentScan("com.harvey")
@PropertySource("classpath:jdbc.properties")
public class SpringConfig {
    private DruidDataSource dataSource;
    @Bean
    public DataSource dataSource(
            @Value("${jdbc.driverClassName}") String driverClassName,
            @Value("${jdbc.url}") String url,
            @Value("${jdbc.username}") String username,
            @Value("${jdbc.password}") String password
    ) {
        this.dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        return sqlSessionFactoryBean;
    }
}
```

### 测试

```java
package com.harvey;

import com.harvey.config.SpringConfig;
import com.harvey.service.AccountService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {
        ApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(SpringConfig.class);
        AccountService accountService =(AccountService) applicationContext.getBean("accountService");
        accountService.transMoney("tom","lucy",10);
    }
}
```

![image-20231111014434520](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/AOP开发/AOP声明式事务控制/Day10-搭建测试环境/image-20231111014434520.png)

![image-20231111014803189](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-blog/AOP开发/AOP声明式事务控制/Day10-搭建测试环境/image-20231111014803189.png)

成功


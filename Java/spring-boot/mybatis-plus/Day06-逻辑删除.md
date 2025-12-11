# 逻辑删除

>   status=0

-   用户界面使用订单删除
    -   用户界面使用删除
-   服务器上不删除->为了以后做统计等
    -   很重要的
-   从此,对增删改查之后应该都要考虑`status!=0`

## 配置逻辑删除

>   打开逻辑删除,MP将对所有的增删改查操作考虑`status!=0`

```yaml
mybatis-plus:
  # 扫包别名
  type-aliases-package: com.harvey.mybatis.plus.pojo
  # mapper目录,及其子目录下的所有xml文件都将作为mapper文件
  # mapper-locations: 'classpath:/mapper/*.xml'
  configuration:
    # 是否开启下划线和驼峰的映射
    map-underscore-to-camel-case: true
    # 是否开启二级缓存
    cache-enabled: false
  global-config:
    db-config:
      logic-delete-field: deleted # 全局逻辑删除的实体字段名,字段类型可以是boolean,Integer
      logic-delete-value: 1 # 逻辑已删除值(默认为1)
      logic-not-delete-value: 0 #逻辑未删除值(默认为0)
```

从此,拼字符串也有了变化:

-   删`deleteById()`->

    ```mysql
    UPDATE 表名 SET deleted = 1 where id=? AND delete=0;
    ```

-   查`selectById()`->

    ```mysql
    SELECT id,age,gender,deleted from user where id=? and deleted=0;
    ```

    咱就不能做成不等于吗?

## 逻辑删除的问题

1.  **占空间**
2.  每次访问都要做判断,**影响性能**
3.  你看这个变量是全局的,**是对所有含有delete字段的表和实体类设置了逻辑删除**

### 解决方案

将删除的数据移到另一个表中


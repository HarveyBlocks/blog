# 自定义SQL

我们利用MyBatisPlus的**Wrapper来构建复杂的Where条件,然后自己定义SQL语句中剩下的部分**

## 原因

>   更新id为1, 2, 4 的用户的年龄**加5**

-   这一条应该是Service层做的事情, 破坏了三层架构
-   MyBatisPlus利用条件构造器非常**擅长对条件的语句的编写**, 方便
-   MyBatisPlus对非条件语句的千变万化的语句, **心有余而立不足**

## 自定义SQL的方式

1.  基于Wrapper构建where条件

    ```java
    List<Long> ids = List.of(1L,2L,4L);
    LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
    lambdaQueryWrapper.in(User::getId,ids);
    // 与原来一致

    int update = userMapper.updateAgeByDelta(lambdaQueryWrapper, -5);
    ```

2.  在mapper接口中自定义方法

    ```java
    /**
     * 自增加或减少age
     *
     * @param wrapper 传递的条件构造器
     * @param deltaAge 年龄变化量
     * @return 返回更新影响的个数
     */
    int updateAgeByDelta(
            //@Param的参数必须是"ew",或常数Constants.WRAPPER
            @Param(Constants.WRAPPER) Wrapper<User> wrapper,
            @Param("deltaAge") int deltaAge);
    ```

3.  自定义SQL, 并使用Wrapper条件

    ```xml
    <update id="updateAgeByDelta">
        update tb_user set age = age + #{deltaAge} ${ew.customSqlSegment}
    </update>
    ```

### 测试结果

```java
/**
 * 自定义SQL
 * */
@Test
public void customize(){
    List<Long> ids = List.of(1L,2L,4L);
    LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
    lambdaQueryWrapper.in(User::getId,ids);
    int update = userMapper.updateAgeByDelta(lambdaQueryWrapper, -5);
    System.out.println(update);//3
}
```

==>  Preparing: update tb_user set age = age + ? WHERE (id IN (?,?,?)) 
==> Parameters: -5(Integer), 1(Long), 2(Long), 4(Long) 
<\==    Updates: 3 


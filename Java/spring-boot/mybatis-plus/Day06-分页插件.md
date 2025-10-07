# MP分页插件



## 基本使用

1.  定义配置类

    -   插件都放到配置类里,创建Bean之后放入容器

2.  创建Bean`MyBatisPlusIntervepter`

    ```java
    public class MyBatisConfig {
    
    
        /**
         * 插件
         * @return 拦截器
         */
        @Bean
        public MybatisPlusInterceptor mybatisPlusInterceptor(){
            // 初始化核心插件
            MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
            // 添加分页插件
            PaginationInnerInterceptor pageInterceptor = new PaginationInnerInterceptor();
            // 配置分页插件
            pageInterceptor.setDbType(DbType.MYSQL);// 设置数据库
            pageInterceptor.setMaxLimit(1000L);// 分页上限,防止超过上限,没办法请求
            pageInterceptor.setOverflow(false);// 设置了十页,十页之后还会再差,从第1(0)页开始查
            // 将分页插件注入核心插件()
            interceptor.addInnerInterceptor(pageInterceptor);
            return interceptor;
        }
    }
    ```

3.  编写业务层逻辑

    ```java
    @Override
    public List<User> pageWithOrder(int pageNo, int pageSize){
        // 准备分页条件
        Page<User> page = Page.of(pageNo,pageSize);
        // 准备排序条件
        page.addOrder(
                new OrderItem("age",false),// 降序
                new OrderItem("name",true) // 升序
        );//也可以用集合做参数
    
        //分页查询
        page = this.page(page);
    
        // 解析
        System.out.println("记录总条数 = "+page.getTotal());
        System.out.println("分页总页数 = "+page.getPages());
        return page.getRecords();
    }
    ```





## 通用分页实体

>   从后端返回前端的分页信息时,需要的信息包含:
>
>   	1. 总条数(为了形成分页条)
>    	2. 总页数(为了形成分页条)
>    	3. 这一页的数据集合

###编写分页实体

####总的分页请求

```java
public class PageQuery  {
    private Integer pageNo;// 页码
    private Integer pageSize;// 页码
    private String[] sortBy;// 排序字段
    private Boolean[] isAsc;// 排序方式
    
    Getter And Setter...
}
```

#####User的查询请求

-   继承分页请求

```java
public class UserQuery extends PageQuery{
    private Integer lowAge;
    private Integer highAge;
    private String name;
    
    Getter And Setter...
}
```

####分页结果

```
/**
 * 分页结果
 *
 * @author Harvey Blocks
 * @version 1.0
 * @className PageDto
 * @date 2023-12-10 00:58
 */
public class PageDto<T> {
    private Long total;// 总条数
    private Long pages;// 总页数
    private List<T> list;// 分页结果查询

    public PageDto(Long total, Long pages, List<T> list) {
        this.total = total;
        this.pages = pages;
        this.list = list;
    }
}
```

### 编写应用层编码

先写服务层代码

```java
@Override
public PageDto<User> pageWithOrder(int pageNo, int pageSize){
    // 准备分页条件
    Page<User> page = Page.of(pageNo,pageSize);
    // 准备排序条件
    page.addOrder(
            new OrderItem("age",false),// 降序
            new OrderItem("name",true) // 升序
    );//也可以用集合做参数
    
    
    
    //分页查询
    page = this.page(page);

    // 解析
    System.out.println("记录总条数 = "+page.getTotal());
    System.out.println("分页总页数 = "+page.getPages());
    List<User> users = page.getRecords();
    return new PageDto<>(
                (int) userPage.getTotal(),(int) userPage.getPages(),users
        );//long转int转Integer

    
}
```

Controller

```java
@Autowired
private UserService userService;
@GetMapping("/page")
public PageDto<User> queryWithPage(UserQuery userQuery){
    // 分页查询逻辑逻辑
    // 可以通过更改service层方法使用userQuery中的条件查询的信息做条件查询
    // 但我不做了
    // 封装,写到Service里去,为了得到页数和条数
    return userService.pageWithOrder(userQuery.getPageNo(), userQuery.getPageSize());
}
```

### 封装Page

-   这一段的逻辑相对独立

```int
// 准备分页条件
Page<User> page = Page.of(pageNo,pageSize);
// 准备排序条件
page.addOrder(
        new OrderItem("age",false),// 降序
        new OrderItem("name",true) // 升序
);//也可以用集合做参数
```
这个也抽象出来放入PageQuery

```java
private long pageNo = 5;//依靠Setter改值
private long pageSize = 2;//设置默认值
private final String DEFAULT_ORDER_BY = "update_time";//设置默认值
public <T> Page<T> toMpPage(OrderItem... items){
    Page<T> page = Page.of(pageNo,pageSize);
    if(items!=null) page.addOrder(DEFAULT_ORDER_BY);//使用默认值
    else page.addOrder(items);//使用它的参数
    return page;
}
```

大概就是这么个结构,其他逻辑自己加

-   这一段也应该封装Page

     List<User> users = page.getRecords();
    return new PageDto<User>(
                (int) userPage.getTotal(),(int) userPage.getPages(),users
        );//long转int转Integer
考 虑直接把Page作为参数,直接把Page封装进进PageDto

```java
public PageDto(Page<T> page) {
    this.total = page.getTotal();
    this.pages = page.getPages();
    this.list = page.getRecords();
}
```


# Redis和数据共享

## 为解决Session缺点的必要条件

-   数据共享
-   内存存储, 满足高并发的需求
-   键值对

**Redis: 你直接报我身份证算了**

## 用Redis改进短信登录分析

### 需要解决的问题

-   用什么key确保会话有,且对不同会话唯一
-   使用哪种类型作为value

### 发送短信验证码

原:

```java
session.setAttribute(CODE_SESSION_KEY,code);
session.setAttribute(PHONE_SESSION_KEY,phone);
```

**从完全平替Session的角度想**

干脆`JSESSIONID+":"+CODE_SESSION_KEY`拼字符串算了,刚好有层级结构这种东西

[获取SessionID](..\..\..\..\spring-mvc\SpringMVC请求与响应\Day03-接收请求头信息.md)

```javascript
@GetMapping(value = "/cookie")
public String getCookie(@CookieValue("JSESSIONID") String jSessionId){
    System.out.println(jSessionId);
    return "/index.jsp";
}
```



但是, 如果**用手机号用key, code作为value**

如果用手机号获取验证码, 换了一台设备(更换了SessionID),这个手机号能否依据这个验证码登录?

答案是能的, 也应该是能的

在换一台设备之后, Session能用原来的手机号和验证码登录吗? 

不行. 

而且, SessionID会变成新的, 会创建一个新的Session,且原来的Session不会消失, 会造成冗余, 消耗更多的内存

这么说来, 用Session来存数据的问题由多了几个





所以这里选择用手机号作为key, code作为value

code类型就直接字符串

```bash
phone:18292038255 = "188320"
```

### 短信验证码登录注册

```
Object phoneCache = session.getAttribute(PHONE_SESSION_KEY);
Object codeCache = session.getAttribute(CODE_SESSION_KEY);
session.removeAttribute(PHONE_SESSION_KEY);
session.removeAttribute(CODE_SESSION_KEY);
session.setAttribute(Constants.USER_SESSION_KEY,
new UserDTO(user.getId(),user.getNickName(),user.getIcon()));
```

存入user, ID是主键, 不用担心重复

ID做key, userDTO做键,类型Hash

以随机token(即随机字符串)为key

把生成的token返回给客户端

这里我不禁要问了: 为什么不用SessionID, 这里总可以用SessionID了吧

SessionID和客户端绑定啊

SessionID还是可以在服务器之间通用的吧

SessionID完全没问题啊



但奈何本能项目的前端准备了名为`"authorization"`的token

```
sessionStorage 的有效期是页面会话持续，如果页面会话（session）结束（关闭窗口或标签页），sessionStorage 就会消失。(和Session每区别)
sessionStorage存储在客户端，Session在服务器端(用来存ID的话还是没区别, 话没说到点子上)
存储在sessionStorage中的ID只在当前浏览器窗口中有效，在不同窗口或标签页之间不共享。而sessionID则可以用于不同窗口或标签页之间共享会话状态。 (那SessionStorage的有效期是page?那不是和Sesison的有效期不一样嘛,和第一条矛盾了, 由于第一条自相矛盾, 所以我选择信任这一条)
```

所以天生反骨的我选择使用SessionID作为Token的值(但是由于页面会话改变, SessionStorage会变,而SessionID不变. 是否导致效果不同)

## 修改代码

### 准备RedisTemplate

```java
@Autowired
private StringRedisTemplate stringRedisTemplate;
```

存入code

```java
// 记得设置有效期
valueOperations.set(
    RedisConstants.LOGIN_CODE_KEY+phone,code,
    RedisConstants.LOGIN_CODE_TTL, TimeUnit.MINUTES);
```

取出code

```java
String codeCache = valueOperations.get(LOGIN_CODE_KEY + phone);
```



存入user

```java
// 将用户DTO存入Redis
// 生成随机Token,hutool工具包
String uuid = UUID.randomUUID().toString(true);//true表示不带中划线
UserDTO userDTO = new UserDTO(user.getId(), user.getNickName(), user.getIcon());
String tokenKey = RedisConstants.LOGIN_USER_KEY + uuid;
hashOperations.putAll(tokenKey,
        new HashMap<>(Map.of(
                "id", userDTO.getId().toString(),
                "nickName", userDTO.getNickName(),
                "icon", userDTO.getIcon())
        )
);//减少请求次数
// 设置有效期
stringRedisTemplate.expire(tokenKey,RedisConstants.CACHE_USER_TTL,TimeUnit.MINUTES)
// 返回token

return Result.ok(uuid);
```

### 修改拦截器

-   没法注入stringRedisTemplate

```java
private StringRedisTemplate stringRedisTemplate;

public LoginInterceptor(StringRedisTemplate stringRedisTemplate) {
    this.stringRedisTemplate = stringRedisTemplate;
}
```

-   stringRedisTemplate从何而来,在拦截器配置类里注入,然后传参

```java
@Override
public boolean preHandle(HttpServletRequest request,
                         HttpServletResponse response,
                         Object handler)
        throws Exception {
    // 进入controller之前进行登录校验
    
    // 获取请求头中的token
    String token = request.getHeader("authorization");//依据前端的信息
    if (token==null||token.isEmpty() ){
        response.setStatus(401);
        return false;
    }
    HttpSession session = request.getSession();
    // 获取user数据
    String tokenKey = RedisConstants.LOGIN_USER_KEY + token;
    Map<Object, Object> userFieldMap = stringRedisTemplate.opsForHash().entries(tokenKey);
    if (userFieldMap.isEmpty()) {// entries不会返回null
        response.setStatus(401);
        return false;
    }
    // 第三个参数: 是否忽略转换过程中产生的异常
    UserDTO user = BeanUtil.fillBeanWithMap(userFieldMap,new UserDTO(),false);
    // 更新时间
    stringRedisTemplate.expire(tokenKey,RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);

    // 保存到ThreadLocal
    UserHolder.saveUser(user);
    return true;
}
```

### 对有效期的优化

对于用户的刷新, 如果用户一直在不会被拦截的界面操作, 超过了时间, 依然会丢失用户的数据

该如何优化

增加拦截器

-   拦截一切路径
-   只要存在token,就刷新

刷新拦截器

```java
public class ExpireInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public ExpireInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler)
            throws Exception {
        // 进入controller之前进行登录校验
        
        // 获取请求头中的token
        String token = request.getHeader("authorization");//依据前端的信息
        if (token==null||token.isEmpty() ){
            return true;
        }
        // 获取user数据
        String tokenKey = RedisConstants.LOGIN_USER_KEY + token;
        Map<Object, Object> userFieldMap = stringRedisTemplate.opsForHash().entries(tokenKey);
        if (userFieldMap.isEmpty()) {// entries不会返回null
            return true;
        }
        // 第三个参数: 是否忽略转换过程中产生的异常
        UserDTO user = BeanUtil.fillBeanWithMap(userFieldMap,new UserDTO(),false);
        // 更新时间
        stringRedisTemplate.expire(tokenKey,RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);

        // 保存到ThreadLocal
        UserHolder.saveUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex)
            throws Exception {
        // 完成Controller之后移除UserHolder, 以防下一次用这条线程的请求获取到不属于它的用户信息
        UserHolder.removeUser();
    }
}
```

登录拦截器

```java
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        if (UserHolder.getUser()==null){
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
```



配置

```java
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new ExpireInterceptor(stringRedisTemplate));
    // 默认拦截所有请求
    registry.addInterceptor(new LoginInterceptor())
            .excludePathPatterns(// 排除不需要拦截的路径
                    "/user/code",// 发送验证码
                    "/user/login",// 登录
                    "/blog/hot",//热点
                    "/shop/**",//店铺相关
                    "/shop-type/**",// 店铺信息
                    "/voucher/**",// 优惠券信息的查询
                    "/upload/**"// 上传,为了测试就放行吧
                    );
}
```


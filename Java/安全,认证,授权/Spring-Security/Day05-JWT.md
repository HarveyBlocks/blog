# 基于JWT的实现Token的处理

## [JWT](https://jwt.io/)

>JSON Web Tokens
>
>基于RFC 7519,可以安全传输的JSON对象
>
>允许解码, 认证, 生成JWT

### 组成与结构

-   Header

    ```json
    {
      "alg": "HS256",
      "typ": "JWT"
    }
    ```

-   Payload  

    ```json
    {
      "sub": "1234567890",
      "name": "John Doe",
      "admin": true
    }
    ```

-   Verify Signature  校验签名

    -   以Header和Payload生成
    -   一旦Header和Payload被修改, 验证将失败

    ```json
    HMACSHA256(
      base64UrlEncode(header) + "." +
      base64UrlEncode(payload),
      secret)
    ```

-   以上三段编码后, 以 **`.`** 隔开

## Token认证流程

1.  用户名携带用户名和密码发起请求
2.  服务器返回生成后的JWT
3.  浏览器会把Token存在本地
4.  下次请求携带JWT的请求头`"authorization"`
5.  服务器得到JWT去验证
6.  验证成功就将资源返回给用户

## Spring Security整合JWT

### 导入依赖

```xml
<!--JWT 登录支持-->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
```

### 初期准备

#### 添加JWT配置

```yaml
# jwt配置 自定义
jwt:
  # jwt 存储的请求头, 自定义
  tokenHeader: Authorization
  # jwt 加密使用的密钥,自定义
  secret: your-256-bit-secret
  # jwt 期限时间 60*60*24, 自定义
  expiration: 604800
  # jwt负载中拿到开头,用在与其他语言的交互
  tokenHead: 'Bearer '
```

#### 添加JWT Token工具类

-   io.jsonwebtoken

```Java
package com.harvey.security.boot.util;

import com.harvey.security.boot.pojo.entity.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-01-11 20:12
 */
public class JwtTokens {
    // 用户名称的key
    public static final String CLAIM_KEY_USERNAME = "sub";

    // jwt创建时间
    private static final String CLAIM_KEY_CREATED = "created";

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.expiration}")
    private Integer expiration;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    /**
     * 根据用户信息生成token
     * @param user 用户信息
     * @return token
     */
    public String generateToken(UserDTO user){
        Map<String,Object> claims = new HashMap<>(16);
        claims.put(CLAIM_KEY_USERNAME,user.getUsername());
        claims.put(CLAIM_KEY_CREATED,new Date());
        return generateToken(claims);
    }

    /**
     * TODO
     * 生成JWT
     * @param claims 负载
     * @return Jwt
     */
    private String generateToken(Map<String,Object> claims){
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS256,secret)
                .compressWith(CompressionCodecs.DEFLATE)
                .compact();
    }

    /**
     * 获取当前时间
     * @return 当前时间
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis()+expiration*1000);
    }

    public String getUserNameFromToken(String token){
        String username;
        try{
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        }catch (Exception e){
            username = null;
        }
        return username;
    }

    public boolean validateToken(String token , UserDTO userDTO){
        String username = getUserNameFromToken(token);
        return username.equals(userDTO.getUsername())&&!isTokenExpired(token);
    }

    /**
     * 从token中获取其jwt的负载
     * @param token 口令
     * @return 负载
     */
    private Claims getClaimsFromToken(String token ){
        Claims claims = null;
        try{
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        }catch (Exception e){
            System.err.printf("JWT格式验证失败:%s",token);
        }
        return claims;
    }

    /**
     * 是否过期
     * @param token 口令
     * @return 过期为true
     */
    private boolean isTokenExpired(String token){
         java.util.Date expiredDate = getExpiredDateFromToken(token);
        return expiredDate.before(new Date());
    }

    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }
}
```

-   cn.hutool

```java
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.hmall.common.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTool {
    private final JWTSigner jwtSigner;

    public JwtTool(KeyPair keyPair) {
        this.jwtSigner = JWTSignerUtil.createSigner("rs256", keyPair);
    }

    /**
     * 创建 access-token
     *
     * @param userId 用户信息
     * @return access-token
     */
    public String createToken(Long userId, Duration ttl) {
        // 1.生成jws
        return JWT.create()
                .setPayload("user", userId)
                .setExpiresAt(new Date(System.currentTimeMillis() + ttl.toMillis()))
                .setSigner(jwtSigner)
                .sign();
    }

    /**
     * 解析token
     *
     * @param token token
     * @return 解析刷新token得到的用户信息
     */
    public Long parseToken(String token) {
        // 1.校验token是否为空
        if (token == null) {
            throw new UnauthorizedException("未登录");
        }
        // 2.校验并解析jwt
        JWT jwt;
        try {
            jwt = JWT.of(token).setSigner(jwtSigner);
        } catch (Exception e) {
            throw new UnauthorizedException("无效的token", e);
        }
        // 2.校验jwt是否有效
        if (!jwt.verify()) {
            // 验证失败
            throw new UnauthorizedException("无效的token");
        }
        // 3.校验是否过期
        try {
            JWTValidator.of(jwt).validateDate();
        } catch (ValidateException e) {
            throw new UnauthorizedException("token已经过期");
        }
        // 4.数据格式校验
        Object userPayload = jwt.getPayload("user");
        if (userPayload == null) {
            // 数据为空
            throw new UnauthorizedException("无效的token");
        }

        // 5.数据解析
        try {
           return Long.valueOf(userPayload.toString());
        } catch (RuntimeException e) {
            // 数据格式有误
            throw new UnauthorizedException("无效的token");
        }
    }
}
```

#### JWT登录授权过滤器

```java
@Component
public class JwrAuthenticationTokenFilter extends OncePerRequestFilter {
    // OncePerRequestFilter , SpringMVC中的过滤器,每次请求都会执行

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokens jwtTokens;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // 获取请求头
        String authHeader = request.getHeader(tokenHeader);
        // 判断
        if(authHeader!=null && authHeader.startsWith(this.tokenHead)){
            //"Bearer " 负载之后的部分, 也就是我们的token
            String authToken = authHeader.substring(tokenHead.length());
            // 从token中获取登录用户名
            String username = jwtTokens.getUserNameFromToken(authToken);
            System.out.println("username = "+username);
            if(username!=null&& SecurityContextHolder.getContext().getAuthentication()==null){
                // 用于通过用户名获取用户数据, 返回UserDto对象, 标识用户的核心信息
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if(jwtTokens.validateToken(authToken,userDetails)){
                    // 装载用户密码和权限, 获取Authentication
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails.getUsername(),userDetails.getPassword()
                            );

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource()
                                                   .buildDetails(request));
                    System.out.println("authenticated username = "+username);
                    SecurityContextHolder
                        .getContext()
                        .setAuthentication(authenticationToken);
                }
            }
        }
    }
}
```

#### 添加安全路径白名单

```yaml
secure:
  ignored:
    urls:
      - /**/*.html
      - /**/*.js
      - /**/*.css
      - /**/*.png
      - /security/login
      - /security/login-view
      - /security/login-success
```

```java
@ConfigurationProperties(prefix = "secure.ignored")
public class IgnoreUrlsConfig {

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    private List<String> urls = new ArrayList<>();
}
```

#### 配置Bean对象

```java
@Bean
public JwtTokens jwtTokens(){
    return new JwtTokens();
}
@Bean
public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter(){
    return new JwtAuthenticationTokenFilter();
}
@Bean
public IgnoreUrlsConfig ignoreUrlsConfig(){
    return new IgnoreUrlsConfig();
}
```

#### 修改Security配置

```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)//注解在任何配置类上边,开启@PreAuthorize
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    /**
     * 安全拦截机制(怎么拦截, 怎么授权)
     *
     * @param http 设置拦截机制
     * @throws Exception 请求时可能出现的异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        for (String url : ignoreUrlsConfig.getUrls()) {
            // 允许他们访问
            http.authorizeRequests().antMatchers(url);
        }

        // 允许跨域请求的Option
        http.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll();
        // .antMatchers("/resource/**").authenticated()//目录/resource下的所有资源都必须认证通过
        http.authorizeRequests().anyRequest().authenticated();

        http.csrf().disable()// 取消对CSRF的保护
                .sessionManagement()
                .sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS//关闭
                        /*SessionCreationPolicy.IF_REQUIRED启用*/
                )//配置会话机制
                .and()
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
                //添加过滤器. jwt在username-password之前;

        http.authorizeRequests()
                /*.and()
                // 配置资源页面和URL
                .formLogin()//允许表单登录
                .loginPage("/login-view")//自定义登录页面
                .loginProcessingUrl("/login")//指定登录处理的URL
                .successForwardUrl("/login-success")// 自定义登录成功的页面地址
                .and()
                .logout().logoutUrl("/logout")//自定义登出处理的URL, 默认使Session无效,清理上下文
                .logoutSuccessUrl("/logout-view?logout")//自定义登出成功的端点
                */
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(myAuthenticationEntryPoint)
                .accessDeniedHandler(myAccessDeniedHandler)
        ;

    }

    @Autowired
    private MyAuthenticationEntryPoint myAuthenticationEntryPoint;
    @Autowired
    private MyAccessDeniedHandler myAccessDeniedHandler;

}
```

### 登录获取token

####


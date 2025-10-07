# **多**Realm

##原理

-   Shiro 的 ModularRealmAuthentication ( 模块化Realm认证 ) 和AuthenticationStrategy (认证策略)组件判断认证成功还是失败
-   AuthenticationStrategy 
    -   无状态组件
    -   在**身份认证尝试**中被询问四次
    -   四次交互所需的任何必要状态将被作为方法的参数
    -   AuthenticationStrategy 的四次调用
        1.  所有Realm调用之前
        2.  调用Realm的getAuthenticationInfo之前
        3.  调用Realm的getAuthenticationInfo之后
        4.  在所有Realm调用之后

###三种认证策略

| AuthenticationStrategy      | 描述                                 |
| --------------------------- | ------------------------------------ |
| AtleastOneSecessfulStratege | (**缺省**) 只要有一个Realm成功就成功 |
| FirstSecessfulStratege      | 第一个Realm成功,就成功, 后续忽略     |
| AllSecessfulStratege        | 所有Realm成功, 才成功                |

-   GPT Q&A
    -   Q: 那么,AtleastOneSecessfulStratege 前面的认证成功,后面还会继续认证吗?
        -   A: 后续会忽略
    -   FirstSecessfulStratege  的应用场景是什么,如果第一个不成功,就不成功了吗? 
        -   A:  如果第一个不成功，后面的会被忽略，因为在这种策略下，认证的决定权已经被第一个成功的Realm所决定了. 
    -   FirstSecessfulStratege第一个后面的会被忽略,为什么不直接删去后面的Realm,而是要使用FirstSecessfulStratege?
        -   A: 在使用 FirstSecessfulStratege 的情况下，可能是出于安全性或者性能方面的考虑，保留后面的Realm可能是为了备用或其他目的。

-   测试结果
    -   Q: 那么,AtleastOneSecessfulStratege 前面的认证成功,后面还会继续认证吗?
        -   A: ==无法测试, 也无意义==
    -   FirstSecessfulStratege  的应用场景是什么,如果第一个不成功,就不成功了吗? 
        -   A:  如果第一个不成功，后面的==不会被忽略==



### 代码

```java
/**
 * 初始化获取SecurityManager
 *
 * @return securityManager
 */
@Bean
public DefaultWebSecurityManager defaultWebSecurityManager(){
    // 1. 创建defaultWebSecurityManager对象

    /*
     * IniSecurityManagerFactory factory =
     *                 new IniSecurityManagerFactory("classpath:users.ini");
     * SecurityManager securityManager = factory.getInstance();
     */
    DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

    // 2. 创建加密对象, 设置相关属性

    HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
    // 使用md5加密,盐是salt(在myRealm中配置),迭代加密 1 次
    matcher.setHashAlgorithmName("MD5");//md5Matcher=org.apache.shiro.authc.credential.Md5CredentialsMatcher
    matcher.setHashIterations(1);//md5Matcher.hashIterations=1


    // 创建认证对象
    ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
    // 设置认证策略
    authenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
    // 把认证对象存入securityManager
    securityManager.setAuthenticator(authenticator);

    // 3. 将加密对象存储到myRealm
    myRealm.setCredentialsMatcher(matcher);//myRealm.credentialsMatcher=$md5Matcher



    myRealm0.setCredentialsMatcher(matcher);
    myRealm1.setCredentialsMatcher(matcher);
    myRealm2.setCredentialsMatcher(matcher);
    // 封装Realms
    List<Realm>realms = new ArrayList<>();
    realms.add(myRealm1);
    realms.add(myRealm2);
    realms.add(myRealm0);//顺序是封装的顺序呢
    // 将Realms存入securityManager
    securityManager.setRealms(realms);

    // 4. 将myRealm存入securityManager
    // securityManager.setRealm(myRealm);//securityManager.realms=$myRealm

    return securityManager;
}

@Autowired
private MyRealm0 myRealm0;
@Autowired
private MyRealm1 myRealm1;//手脚
@Autowired
private MyRealm2 myRealm2;
```

知乎乱象:

-   体验文学(...是一种什么样的体验)
-   到底文学(...到底是什么)
-   一文文学(一文搞懂...../....看这一篇就够了! )
-   彻底文学(彻底搞懂....)
-   详解文学(详解....../.....详解)
-   评价文学(如何评价....)
-   统计文学(百分之99的人都...../只有百分之一的人......)
-   面试文学(面试官:......),常常联系统计文学(面试官:......,只有百分之一的人......)
-   否定文学
    -   没用文学(学....没用/ ....已经过时了/别学.....了)
    -   后悔文学(学习....是我最后悔的一件事)
    -   中招文学(....., 你中招了吗? ),常常联系统计文学(百分之99的人都.....你中招了吗?)
    -   千万文学(千万不要......)


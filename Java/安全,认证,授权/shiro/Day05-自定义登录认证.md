# 自定义登录认证

Shiro自带的登录认证是不带加密的

想要加密, 还得**自定义登录认证**,自定义Realm

可创建自定义 的Realm类, 继承AuthenticatingRealm类实现doGetAuthenticationInfo()方法

```java
public class MyRealm extends AuthenticatingRealm {
    /**
     *
     * <span style="color:red">
     * <b>自定义登录认证方法</b><br><br>
     * </span>
     * 这是一个依据输入的信息从数据库中查找通principle(在这里是用户名)的信息,然后返回封装的正确信息的<br>
     * 完成用户的查找,不会完成密码的比对和权限的赋予<br>
     * 需要配置自定义的Realm生效(<br>
     * &emsp;1. 在ini文件中配置<br>
     * &emsp;2. 在Spring boot 中进行配置<br>
     * )<br>
     *
     * @param token the authentication token containing the user's principal and credentials.
     * @return 对比信息
     * @throws AuthenticationException 认证异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) 
        throws AuthenticationException {
        // 1. 获取身份信息
        Object principal = token.getPrincipal();
        System.out.println("输入的身份用户名信息: " + principal);
    /*
        获取凭证信息,不需要,只是打印在控制台方便观察

        Object credentials = token.getCredentials();
        String password = new String((char[]) credentials);

        System.out.println("输入的身份密码信息  : " + password);//zhangsan 可以
        System.out.println("输入的身份密码信息  : " + credentials.toString());//[C@293a5bf6 不可以
        //原因看源码, getCredentials在UserPasswordToken中是以password的形式封装的,password类型是char[]
    */



        // 伪造数据库
        Map<String, String> users = new HashMap<>();
        users.put("zhangsan", cryptography("zhangsan", salt));
        users.put("lisi", cryptography("lisi", salt));
        users.put("数据库里的用户名", "对应的密码,数据库里存的应该是加密后的密文");

        // 验证密码
        String salt = "salt";
        String pwd;

        
        AuthenticationInfo info = null;
        
        
        if ((pwd = users.get(principal.toString())/*从数据库中取信息*/) != null) {//如果用户存在

            // 3. 创建封装校验逻辑的对象,封装数据返回
            info = new SimpleAuthenticationInfo(
                    principal,//输入的身份信息, 不是字符串
                    pwd,//加密后的密文密码,即正确输入的密码加密后,若输入正确应该是pwd
                    ByteSource.Util.bytes(salt),
                //设置盐的相关信息,加密的方法在配置文件中配置,用来把用户输入信息加密后与pwd比较
                    "你好"//这个参数意义不明 有人说填principal.toString()
            );

        }


        return info;//如返回null则表示用户不存在(shiro也会这么认为)
    }

    /**
     * 伪造数据库中的pwd需要是加密的, 这个方法产生伪造数据库中的密文
     *
     * @param password 正确密码明文
     * @param salt     盐
     * @return 密文
     */
    private String cryptography(String password, String salt) {
        Md5Hash md5Hash = new Md5Hash(password, salt);
        return md5Hash.toHex();
    }

}
```



```ini
[main]
;key名随意
md5Matcher=org.apache.shiro.authc.credential.Md5CredentialsMatcher
;设置加密迭代次数,缺省为1
md5Matcher.hashIterations=1

;key名随意
myRealm=com.harvey.security.shiro.MyRealm
myRealm.credentialsMatcher=$md5Matcher

;配置SecurityManager的Realm
securityManager.realms=$myRealm

;[users]
;使用密文存储密码
;zhangsan=4f4141fc3d265c1d16b99cd8e3edc338,role1,role2
;lisi=18b335a01042b89d4fd45ab8b7da4e17,role2
;
;[roles]
;role1=user:insert,user:query
;role2=user:query
```


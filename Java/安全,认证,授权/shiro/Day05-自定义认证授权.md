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
```

1.  继承的类改变AuthorizingRealm
2.  覆写的方法增加doGetAuthorizationInfo();

```java
public class MyRealm extends AuthorizingRealm {
    /**
     *
     * <span style="color:red">
     * <b>自定义登录认证方法</b><br><br>
     * </span>
     * 这是一个依据输入的信息从数据库中查找通principle(在这里是用户名)的信息,然后返回封装的正确信息的<br>
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



        // 2. 伪造数据库
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
                    ByteSource.Util.bytes(salt),//设置盐的相关信息,加密的方法在配置文件中配置,用来把用户输入信息加密后与pwd比较
                    "这是一个Realm"//这个参数是配置多Realm时的唯一标识
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


    /**
     * 赋予权限的方法, 在checkRole(),hasRole(),checkPermission()等方法被调用的时候调用
     * @param principals the primary identifying principals of the AuthorizationInfo that should be retrieved.
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println(principals.getRealmNames());//用来观察测试

        // 伪造数据库
        Map<String, String> rolesMap = new HashMap<>();
        rolesMap.put("zhangsan", "role1");
        rolesMap.put("lisi", "role2,role1");
        Map<String, String> permissionsMap = new HashMap<>();
        permissionsMap.put("zhangsan", "user:insert,user:query");
        permissionsMap.put("lisi", "user:query");
        // 授权
        String primaryPrincipal =(String) principals.getPrimaryPrincipal();
        HashSet<String> roles = new HashSet<>(
                Arrays.asList(rolesMap.get(primaryPrincipal).split(","))
        );
        HashSet<String> permissions = new HashSet<>(
                Arrays.asList(permissionsMap.get(primaryPrincipal).split(","))
        );
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRoles(roles);
        info.addStringPermissions(permissions);
        return info;
    }
}
```


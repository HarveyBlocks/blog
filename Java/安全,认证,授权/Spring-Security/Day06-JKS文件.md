# JKS文件

## 创建

在装有JDK, 且配置了环境变量的控制台: 

```shell
keytool -genkey -alias mydomain -keyalg RSA -keystore mydomain.jks -
```

-   -alias 别名

-   -keypass 指定生成密钥的密码

-   -keyalg 指定密钥使用的加密算法（如 RSA）

    -   `RSA` 加密算法

        也可以使用`DSA`

    <img src="..\..\typora-user-images\Day06-JKS文件\image-20240423170006269.png" alt="image-20240423170006269" style="zoom:33%;" />

-   -keysize 密钥大小

-   -validity 过期时间，单位：天

-   -keystore 指定存储密钥的 密钥库的生成路径、名称。

-   -storepass 指定访问密钥库的密码。

    

然后根据提示走, 不确定的就直接回车也没关系

最终确认后按`Y`键并回车

## 验证

```shell
keytool -list [-v -keystore mydomain.jks
```


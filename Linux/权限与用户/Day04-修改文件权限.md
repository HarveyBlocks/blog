# chmod命令修改文件(夹)权限信息

- 只有root用户可以使用

## 语法

```Linux
chmod [-R] 权限 文件(夹)路径
```

- -R 对文件夹内全部内容进行想要同样的操作

## 示例

```Linux
chmod u=rwx,g=rx,o=x test.txt
```

- u 用户(user)
- g 用户组(group)
- o 其他(other)用户

![image-20231002000937523](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Linux/权限与用户/Day04-修改文件权限/image-20231002000937523.png)

- ugo之间用**逗号**
- ugo顺序随便
- rwx顺序随便
- 这个**绿色**表示这个文件不安全

## 权限的简易写法

```Linux
chmod 751 test.txt
```

1. 权限可以用三位数字表示
2. 每一位都是[0,7]中的一个(因为三位二进制数)
3. 第一位数字表示用户权限
4. 第二位数字表示用户组权限
5. 第三位数字表示其他用户权限

   ![image-20231002001842820](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Linux/权限与用户/Day04-修改文件权限/image-20231002001842820.png)

- 就纯纯一二进制,高到低rwx嘛


# Root管理员



## Root管理员和普通用户的切换

```linux
su -root 然后输入密码,进入Root管理员
exit     退出Root管理员,变回普通用户,Ctrl+D一样效果
```

## 权限

- root管理员具有最大的操作权限
- 普通用户一般在HOME目录内不设限
- 一旦出了HOME目录,普通用户仅有只读和执行权限,无修改权限

## su命令详解

### 语法

```Linux
su [-][用户名]
```

- -符号是可选的,表示在切换用户后加载环境变量**建议带上**
- 缺省用户名,**默认root**
- 普通用户在切换到其他用户需要输密码
- root不需要密码

## sudo 临时拥有root权限命令

### 语法

``` LInux 
sudo 其他命令
```

- 仅为这条命令赋予root授权
- 只有被**授予sudo认证的用户**才有权使用sudo命令

#### 授予sudo命令

1. 切换到root用户

2. 执行命令:

   ```linux
   visudo
   ```

   或

   ```linux
   vi /etc/sudoers
   ```

   通过vi编辑器打开/etc/sudoers

3. 在/etc/sudoers文件最后添加:

   ```linux
   用户名 ALL=(ALL)     NOPASSWORD=ALL
   ```

4. 用wq保存


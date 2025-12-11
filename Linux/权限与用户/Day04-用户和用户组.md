# 用户和用户组

![image-20231001225929808](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Linux/权限与用户/Day04-用户和用户组/image-20231001225929808.png)

- **以下命令皆仅root用户可执行**

## 用户组管理

### groupadd命令创建用户组

#### 语法

```linux
groupadd 用户组名
```

### groupdel命令删除用户组

#### 语法

```linux
groupdel 用户组名
```

## 用户管理

### useradd命令创建用户

#### 语法

```linux
useradd [-g 组名][-d 路径] 用户名
```

- -g 指定用户的组,缺省则默认创建与**用户名同名的组**并**自动加入**
- -d 指定用户HOME路径,不指定,HOME路径默认在/home/用户名

### userdel命令删除用户

#### 语法

```linux
userdel [-r] 用户名
```

- -r不指定保留该用户HOME目录
- -r指定,连带删除该用户HOME目录

### id命令查看用户所属组

#### 语法

```linux
id [用户名]
```

- 缺省用户名,则查询当前用户的组

### usermod修改用户所属组(加入更多的组)

#### 语法

```linux
usermod -a -G 用户名 用户组 
```

## getent命令

### 查看系统中用户信息语法

```Linux
getent passwd
```

返回的信息:

```Linux
用户名:密码(x):用户ID:组ID:描述信息(无用)
```

- 密码不会以明文显示,所以用x显示给你

### 查看系统中用户组信息语法

```Linux
getent group
```

返回的信息:

```Linux
用户组名:组认证(x):组ID
```

- 组认证不会以明文显示,所以用x显示给你


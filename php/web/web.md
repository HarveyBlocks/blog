# php on web

## 会话跟踪

会话是某个浏览器与服务器之间的一系列HTTP请求和响应

会话期间，客户端和服务器之间可能有些数据需要保存下来

两种实现方式：Cookie 和 Session

Cookie 将信息存储在客户端

Session 将信息存储在服务器端

## Cookie

Cookie 使用的是 HTTP 头部报文中的两个字段

- 响应报文中的 Set-Cookie 字段
- 请求报文中的 Cookie 字段

php中使用`setcookie`来创建

```php
setcookie("user", "Alex Porter", time() + 3600/*到期时间*/);
```

**cookie 必须在所有程序输出前发送**

echo 或 `<?php … ?>` 之外的 html 代码都是输出

因为 Set-Cookie 是响应报文头部，而输出是放到实体主体中

## Session

1. 当用户初次登陆网站的时候，服务器为其生成一个随机的字符串，叫做 session id，并将其传递给客户端
	例如：eadc453415011bac07c29523b64
2. 服务器端程序在服务器的临时文件夹中创建了一个文件保存与该 session id 相关的信息
3. 客户端再次访问访问网站时，提交 session id，服务器就知道客户又来了，从文件取出相应信息

服务器和客户端交换 session id 的方式

通过 Cookie
- `PHPSESSID=eadc453415011bac07c29523b6461f5f`
- `JSESSIONID=FB9D15A4DB677AC8039329F89FB5D1EA`

通过 URL 改写方式传递

- `http://...../xyz.php?PHPSESSID=eadc453415011bac07c29523b6461f5f`

PHP 默认使用 Cookie 方式

1. 在所有页面的开头执行 `session_start();`

2. 如果会话 ID 不存在，它会创建一个; 如果会话 ID 已存在，则载入存在的会话

3. 创建会话变量：
	```php
	$_SESSION['user'] = 'admin';
	```
	
4. 使用会话变量：
	```php
	if ( isset($_SESSION['user'])) { ...... }
	```
	
5. 销毁一个`Session` 变量

   ```php
   unset( $_SESSION['user'] );
   ```

6. 销毁会话

   ```php
   session_destroy();
   ```

   

在客户端, 会话ID 保存在以 PHPSESSID 为名字的 cookie 中

在服务器上, 会话数据保存为形如/tmp/sess_fcc17f071 的临时文件

使用 session_save_path 函数找到(改变)会话数据存放的文件夹

会话数据可以保存在SQL数据库(或其它目标路径)中而不是使用文件

## 表单处理

POST 可以传输大量的数据，所以在上传文件时只能使用 POST

URL 的长度是有限的(\<1024) 字符

### Content-Type

`application/x-www-form-urlencoded`

报文中的数据同样需被 URL 编码（浏览器自动完成）



`multipart/form-data`

用于传输大量二进制数据或者非 ASCII 字符的文本



### PHP 超全局变量数组

`$_GET`

经由 URL 请求提交至脚本的变量

`$_POST`

经由 HTTP POST 方法提交至脚本的变量

`$_COOKIE`

经由 HTTP Cookies 方法提交至脚本的变量

`$_REQUEST`

经由 GET，POST 和 COOKIE 机制提交至脚本的变量



当表单提交时，表单中的字段会自动成为

PHP 脚本中超全局变量数组的成员

```html
<form action="foo.php" method="post">
	<!--在imput的label里添加属性name-->
    Name: <input type="text" name="username" />
    Email: <input type="text" name="email" /></p>
    <input type="submit" name="submit" value="Submit me!" />
</form>
```

### URL 编码

某些字符, 例如空格, "/", "=", "&" 符 以及中文等

- 在传递参数前, 浏览器会自动对它们进行编码
- PHP的 `$_REQUEST` 数组会自动对它们解码
- 但有时会出现编码后的版本 (例如在 Firebug)



### 文件上传

```html
<!--必须设定表单的enctype 属性-->
<form enctype="multipart/form-data" action="upload.php" method="post">
    <input type="hidden" name="MAX_FILE_SIZE" value="30000" />
    Send this file:
    <input name="user_file" type="file" />
    <input type="submit" value="Send File" />
</form>
```

上传的文件放在全局数组 `$_FILES` , 而不是`$_REQUEST`

`$_FILES` 里每一个元素自身是一个关联数组, 含有以下键

- `name `: 用户所上传的本地文件名

- `type `: 上传数据的MIME类型, 例如image/jpeg

- `size `: 文件大小, 以byte 为单位

- `tmp_name `: 存储在服务器的临时副本的文件名

  为了永久保存这个文件, 需要从这个临时位置移动到其它地方

假设用户上传了`borat.jpg`

- `$_FILES["user_file"]["name"]` 会是"borat.jpg"
- `$_FILES["user_file"]["type"]` 会是"image/jpeg"
- `$_FILES["user_file"]["tmp_name"]` 会是类似 "/var/tmp/phpZtR4TI" 的东西

PHP 处理上传的文件

```php
$uploaddir = '/var/www/uploads/';
// basename 可以提取路径中的文件名
$uploadfile = $uploaddir . basename($_FILES['user_file']['name']);
echo '<pre>';
if (is_uploaded_file($_FILES['userfile']['tmp_name']))) {
    // is_uploaded_file: 给定的文件名是由用户上传
    // move_uploaded_file, 移动位置
    move_uploaded_file($_FILES['userfile']['tmp_name'], $uploadfile);
    echo "File is valid, and was successfully uploaded.\n";
} else {
	echo "Possible file upload attack!\n";
}
echo 'Here is some more debugging info:';
print_r($_FILES);
print "</pre>";
```

相关配置

- `file_uploads` 是否开启PHP上传功能
- `upload_tmp_dir `上传文件临时存放目录
- `upload_max_filesize` 上传的最大文件大小
- `memory_limit` PHP 脚本的内存使用限制也要足够大




# if指令

```nginx
if (condition_expression) {
    # if 之后一定要有一个空格啊
}
```

## 位置

`server`

`location`



## Condition

###精准匹配

变量名, "0"或空字符串为假, 其余为真

```nginx
default_type text/plain ;
if ($args) {
    # $args 是URI里的参数/abc?name=yes
	return "args is $args" ;
}
return "args is empty"
```

`=`表示相等, `!=` 表示不相等

###匹配正则

正则表达式的匹配, 倘若没有`^`做开头, `$`做结尾, 就是"包含"的意思了

`~`区分大小写匹配正则

`~*`不区分大小写匹配

`!~` , 区分大小写匹配正则, 不匹配则返回真

`!~*` , 不区分大小写匹配正则, 不匹配则返回真

```Nginx
default_type text/plain ;
set $REGIX name ;
if ($args ~* $REGIX) {
	return "args is $args" ;
}
return "args don't have $REGIX"
```

### 资源文件是否存在

```nginx
default_type text/plain ;

if (-f $request_filename) {
	return "$request_filename is existed" ;
}
if (!-f $request_filename) {
	return 404 "NOT FOUND" ;
}
```

目录是否存在

`-d`/`!-d`

文件或陌路是否存在

`-e`/`!-e`

文件是否可执行

`-x`/`!-x`
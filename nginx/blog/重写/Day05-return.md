# return

return后的Nginx配置都无效

## 位置

`server`

`location`

`if`

## 语法

```nginx
return code [text] ;
```

```nginx
# 302 临时跳转
return [code] URL ;
```

```nginx
location /testreturn {
	return 302 www.baidu.com; # 不允许这么写
}
```


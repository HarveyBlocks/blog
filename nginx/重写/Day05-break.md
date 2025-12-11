# break

1.  中断相同作用域中后面的配置
2.  终止当前的匹配并把当前的URI在本`location`进行重定向访问?

## 位置

`server`

`location`

`if`

## 语法

```nginx
location /testbreak {
	default_type text/plain;
    set $param 0 ;
    if ($args){
        set $param 1 ;
        break ; # 重定向到 ?/html/testbreak/index.html
        # 老子就是不想重定向怎么搞?
		set	$param	2	;
    }
    return	200	$param ;
}
```


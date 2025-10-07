# JSON

## Json 字符串 转 对象

```js
let obj = JSON.parse(jsonString);
```

## 对象 转 Json 字符串

```js
let jsonString = JSON.stringify(obj);
```

-   obj中有日期的, 应当转为(服务器约定)字符串 or timestamp
-   stringify 会删除函数成员
# AJAX

>   **A**synchronous **J**avaScript **A**nd **X**ML

## XMLHttpRequest 

```js
variable = new XMLHttpRequest();
```

### 方法

| 方法                                      | 描述                                                         |
| :---------------------------------------- | :----------------------------------------------------------- |
| abort()                                   | 取消当前请求                                                 |
| open(*'method', 'url', async, user, psw*) | 规定请求<br/>method:请求方法<br/>url:文件位置<br/>async:true（异步）或 false（同步）<br/>user:可选的用户名称<br/>psw:可选的密码 |
| send()                                    | 将请求发送到服务器                                           |
| send(*'request body'*)                    | 将请求发送到服务器, ==RequestBody==                          |
| setRequestHeader()                        | 向要发送的报头添加标签/值对                                  |
| getAllResponseHeaders()                   | 返回头部信息                                                 |
| getResponseHeader(*'key'*)                | 返回特定的头部信息                                           |



### 属性

| 属性               | 描述                                                         |
| :----------------- | :----------------------------------------------------------- |
| onreadystatechange | 定义当 readyState 属性发生变化时被调用的函数                 |
| readyState         | 保存 XMLHttpRequest 的状态。<br>0:请求未初始化<br/>1:服务器连接已建立<br/>2:请求已收到<br/>3:正在处理请求<br/>4:请求已完成且响应已就绪 |
| responseText       | 以字符串返回响应数据, ==ResponseBody==                       |
| responseXML        | 以 XML 数据返回响应数据                                      |
| status             | 返回HTTP响应状态码                                           |
| statusText         | 返回状态文本（比如 "OK" 或 "Not Found"）                     |

## 请求

| 属性                                      | 描述                                                         |
| :---------------------------------------- | :----------------------------------------------------------- |
| abort()                                   | 取消当前请求                                                 |
| open(*'method', 'url', async, user, psw*) | 规定请求<br/>method:请求方法<br/>url:文件位置<br/>async:true（异步）或 false（同步）<br/>user:可选的用户名称<br/>psw:可选的密码 |
| send()                                    | 将请求发送到服务器                                           |
| send(*'request body'*)                    | 将请求发送到服务器, ==RequestBody==                          |
| setRequestHeader()                        | 向要发送的报头添加标签/值对                                  |

## 响应

| 属性                       | 描述                                                         |
| :------------------------- | :----------------------------------------------------------- |
| onreadystatechange         | 定义当 readyState 属性发生变化时被调用的函数                 |
| readyState                 | 保存 XMLHttpRequest 的状态。<br>0:请求未初始化<br/>1:服务器连接已建立<br/>2:请求已收到<br/>3:正在处理请求<br/>4:请求已完成且响应已就绪 |
| responseText               | 以字符串返回响应数据, ==ResponseBody==                       |
| responseXML                | 以 XML 数据返回响应数据                                      |
| status                     | 返回HTTP响应状态码                                           |
| statusText                 | 返回状态文本（比如 "OK" 或 "Not Found"）                     |
| getAllResponseHeaders()    | 返回头部信息                                                 |
| getResponseHeader(*'key'*) | 返回特定的头部信息                                           |

## Fetch API

-   基于 **Promises**，代码更加简洁和易读。
-   更好的错误处理机制：只在网络错误（如无法连接服务器）时返回 `catch`，而非状态码错误。
-   支持多种数据格式（JSON、文本、二进制等）。
-   可以处理跨域请求，通过 `CORS` 机制配置。

### 语法



```js
fetch('https://api.example.com/data', {
  method: 'POST', // 指定请求方法
  headers: {
    'Content-Type': 'application/json'
  }, body: JSON.stringify({
    key: 'value'
  })
}).then(response => {
  if (!response.ok) {
    throw Error('bad request');
  }
  console.log(response.status)
  console.log(response.statusText)
  console.log(response.headers);
  return response.json();
})
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
```

### 跨域请求

在服务器端，设置 CORS（Cross-Origin Resource Sharing）

在前端，也可以通过 credentials 选项来指定是否发送 cookies 等凭据。

```js
fetch('https://example.com/api', {
    method: 'GET',
    credentials: 'include' // 允许跨域请求时携带 cookie
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```


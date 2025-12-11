# Axios

>   对原生AJAX进行封装,简化书写

## 引入axios的js文件

```html
<script src="js/axios=0.18.0.js"></script>
```

## 使用Axios

-   发送请求并获得响应结果

```html
<script src="js/axios-0.18.0.js">
    // get
    axios({
        method:"get",
        url:"http://localhost/axiosServlet?username=zhangsan"
    }).then(function(response) {
        alert(response.data);
    });
    // post
    axios({
        method:"post",
        url:"http://localhost/axiosServlet"
        data:"username=zhangsan"
    }).then(function(response) {
        alert(response.data);
    });
</script>
```

```html
<script src="js/axios-0.18.0.js">
    // get
    axios.get("http://localhost/axiosServlet?username=zhangsan")
    	.then(function(response) {
        alert(response.data);
    });
    // post
    axios.get("http://localhost/axiosServlet","username=zhangsan")
    	.then(function(response) {
        alert(response.data);
    });
</script>
```


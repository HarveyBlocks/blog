# axios

## 开始

axios直接将对象转换为json

```vue
import axios from 'axios'
```

```javascript
axios.get('/api/data')
  .then(response => console.log(response))
  .catch(error => {
    // 404、500 等 HTTP 错误会直接进入 catch
    console.log('错误信息：', error.response.data);
    console.log('状态码：', error.response.status);
  });
```

```js
// 定义请求体数据（普适性的用户注册信息示例）
const requestData = {
  username: 'john_doe_123',
  email: 'john.doe@example.com',
  password: 'SecurePass123!',
  profile: {
    fullName: 'John Doe',
    age: 30,
    interests: ['programming', 'reading', 'hiking']
  },
  agreeTerms: true
};

// 发送POST请求
axios.post('https://api.example.com/register', requestData).then(response => {
  // 打印状态码
  console.log('状态码 (statusCode):', response.status);

  // 打印状态信息
  console.log('状态信息 (statusMessage):', response.statusText);

  // 打印响应体
  console.log('响应体 (response body):', response.data);

  // 打印响应头
  console.log('响应头 (response headers):', response.headers);
}).catch(error => {
  // 错误处理（包括HTTP错误状态码的情况）
  if (error.response) {
    // 服务器返回了响应，但状态码不是2xx
    console.log('错误状态码:', error.response.status);
    console.log('错误状态信息:', error.response.statusText);
    console.log('错误响应体:', error.response.data);
    console.log('错误响应头:', error.response.headers);
  } else if (error.request) {
    // 请求已发出，但没有收到响应
    console.log('没有收到响应:', error.request);
  } else {
    // 其他错误（如请求配置错误）
    console.log('请求错误:', error.message);
  }
});
```

## 配置

1.   接口基地址
2.   接口超时时间
3.   请求拦截器
4.   响应拦截器

`apis/axios-commons.js`

```js
// 对axios的基础封装
import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: 'http://pcapi-xiaotuxian-front-devtest.itheima.net',/*base url 示例*/
  timeout: 5000, /*ms, 5s超时*/
});

// 拦截器
// 请求拦截器
axiosInstance.interceptors.request.use(config => config, e => Promise.reject(e));
// 相应拦截器
axiosInstance.interceptors.response.use(res => res.data, e => Promise.reject(e), e => Promise.reject(e));

export default axiosInstance;
```

## api示例

```js
import axiosInstance from "@/apis/axios-commons.js";

export function getCategory(){
  return axiosInstance({
    url: 'home/category/head'
  });
}
```

使用

```js
function logCategory(){
  getCategory().then(
      res=> console.log(res)
  );
}
```

![image-20250826160247330](../../assets/Day04-axios/image-20250826160247330.png)

## 渲染

异步请求服务器资源的时候, 可能资源还没请求到, 前端页面就开始渲染.

当渲染的数据涉及到服务器的请求资源的时候, 就会undefine, 而发生TypeError

为了避免这种情况, 应该==使用`v-if`判断是否加载完成==, 加载完成之后再渲染


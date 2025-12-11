## temp

## 应用

## 创建应用

```js
import { createApp } from 'vue'

const app = createApp(组件对象)
```

## 应用配置

使用app的config属性进行配置

例如配置错误处理

```js
// 构建app对象
const app = createApp(root);
// 进行配置
app.config.errorHandler = (err) => {
  window.alert(`检测到错误了: ${err}`);
}
app.mount('#root');
```

## 多个应用

Vue支持多个应用, 每个应用独立

对于复杂多变的大型页面, 建议多拆分几个应用


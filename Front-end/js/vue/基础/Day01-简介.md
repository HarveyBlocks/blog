# Vue

-   用于构建用户界面的 JavaScript 框架
-   基于标准 HTML、CSS 和 JavaScript 构建
-   提供了一套*声明式*的、*组件化*的编程模型

## 模板

### 单文件组件

>   SFC    **S**ingle-**F**ile **C**omponents

一种以`.vue`作后缀的文件

会将一个组件的逻辑 (JavaScript-`<script>`)，模板 (HTML-`<template>`) 和样式 (CSS-`<style>`) 封装在同一个文件里

```vue
<script setup>
import { ref } from 'vue'
const count = ref(0)
</script>

<template>
  <button @click="count++">Count is: {{ count }}</button>
</template>

<style scoped>
button {
  font-weight: bold;
}
</style>
```

### 作用域样式 scoped

 CSS 只会影响当前组件的元素

```vue
<style scoped>
.example {
  color: red;
}
</style>

<template>
  <div class="example">hi</div>
</template>
```



-   不加scope, style都是全局的, 也同样会影响加了scope的组件

-   父组件加了scope, 父组件的样式将不会渗透到子组件中

-   子组件加了scope, 不会影响父组件的样式

-   子组件的根节点会同时被父组件的作用域样式和子组件的作用域样式影响

    为了让父组件可以从布局的角度出发，调整其子组件根元素的样式



## API 风格

-   选项式API
-   组合式API

### 选项式 Option

用包含多个选项的对象来描述组件的逻辑，例如 `data`、`methods` 和 `mounted`

```vue
<script>
export default {
  // data() 返回的属性将会成为响应式的状态
  // 并且暴露在 `this` 上
  data() {
    // 可编写代码
    // 一些对成员的预处理
    return {
      count: 0
    }
  },

  // methods 是一些用来更改状态与触发更新的函数
  // 它们可以在模板中作为事件处理器绑定
  methods: {
    // 自定义函数
    increment() {
      this.count++;
    }
  },

  // 生命周期钩子会在组件生命周期的各个不同阶段被调用
  // 例如这个函数就会在组件挂载完成后被调用
  mounted() {
    console.log(`The initial count is ${this.count}.`)
  }
}
</script>

<template>
  <button @click="increment">Count is: {{ count }}</button>
</template>
```

-   `data()` 返回对象含有成员函数, 此函数不会被解析
-   `methods`里有成员, 不会被解析, 可以被调用, 但是不建议
-   有不被定义(非vue生命周期)的成员, 不会被解析, 不建议



### 组合式 Setup

在`script` 标签内加入`setup`属性, 标注脚本内容需要被作预处理

```vue
<script setup>
import { ref, onMounted } from 'vue'

// 响应式状态
const count = ref(0)

// 用来修改状态、触发更新的函数
function increment() {
  // 不用this
  count.value++
}

// 生命周期钩子
onMounted(() => {
  console.log(`The initial count is ${count.value}.`)
})
</script>

<template>
  <button @click="increment">Count is: {{ count }}</button>
</template>
```

在脚本中的'全局'(只是看似全局, 其实质上不是全局)上声明的变量, 会被vue解析成成员

'全局'的函数, 会被vue解析成成员

在'全局'上注册声明周期的钩子方法

原型是(大概, 不太严谨, 仅用于简单理解)

```vue
<script>
import { ref } from 'vue'

export default {
  setup() {
    const count = ref(0)

    function increment() {
      // 在 JavaScript 中需要 .value
      count.value++
    }
    // 生命周期钩子
    onMounted(() => {
      console.log(`The initial count is ${count.value}.`)
    })
    return {
      count,
      increment
    }
  }
}
</script>
```

用setup简化了

### 风格选择

选项式 API 是在组合式 API 的基础上实现的

-   选项式 API 以“组件实例”的概念为中心 (即上述例子中的 `this`)
    -   vue会将方法中的`this` 的成员指向用户定义的成员属性和成员函数
-   直接在函数作用域内定义响应式状态变量, 并将从多个函数中得到的状态组合起来处理复杂问题
    -   函数中使用count, 而不用this. 此时的count, 就是在increment外面的作用域被访问到的我



由于选项式API的括号层数太多, 故选择组合式API

## 示例

使用了WebStream, 都装好了

构建一个Vue项目

### index.html

```html
<!DOCTYPE html>
<html lang="">
<head>
    <meta charset="UTF-8">
    <link rel="icon" href="/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vite App</title>
    <style>
        * {
            box-sizing: border-box;
        }
    </style>
</head>
<body>
<div id="app"></div>
<script type="module" src="/src/main.js"></script>
</body>
</html>

```

-   引用/src/main.js



### /src/main.js

```js
import './assets/main.css'

import {createApp} from 'vue'
import App from './App.vue'

// 在App对象上挂载到id='app'的标签上
createApp(App).mount('#app')
```

引用了`./assets/main.css`

### /src/assets/main.css

空文件

### /src/App.vue

```vue
<script setup>
import {onMounted, ref} from 'vue'

// 响应式状态
const count = ref(0)

// 用来修改状态、触发更新的函数
function increment() {
  count.value++
}

// 生命周期钩子
onMounted(() => {
  console.log(`The initial count is ${count.value}.`)
})
</script>

<template>
  <button @click="increment">Count is: {{ count }}</button>
</template>

```

### 测试

![image-20250813002021477](../../../assetss/Day01-简介/image-20250813002021477.png)

完成计数器按钮


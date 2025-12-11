# router

[Vue Router](https://router.vuejs.org/zh/guide/)

## 示例

![image-20250819030256048](../../../assetss/Day7-router/image-20250819030256048.png)

这是w3shools的网页, 框出来的部分是每个教程文档不变的部分

![image-20250819030328384](../../../assetss/Day7-router/image-20250819030328384.png)

但是每一个教程后面的网页是会改变的

也就是说, 多个URL资源上的网页, 有一部分是不变的, 这部分用于导航到每个URL上变化的部分

而变化的部分又和URL上一一对应

## 语法

准备几个资源和导航页

资源一 Node1.vue

```vue
<template>
  <h2>资源1</h2>
</template>
```

资源二 Node2.vue

```vue
<template>
  <h2>资源2</h2>
</template>
```

注册资源和路由 main.js

```js
import './assets/main.css';
import {createRouter, createWebHistory} from 'vue-router';//需要下载
import {createApp} from 'vue';
import root from '@/Root.vue';
import Node1 from '@/components/Node1.vue';
import Node2 from '@/components/Node2.vue';

// 创建路由
const router = createRouter({
  history: createWebHistory(),
  routes: [
    {path: '/node1', component: Node1},
    {path: '/node2', component: Node2},
  ]
});
const app = createApp(root);
app.use(router); // 使用路由
app.mount('#root');
```

-   `history` 控制了路由和 URL 路径是如何双向映射的

-   `createWebHistory()` 
    -   `createWebHistory()`
    -   `createWebHashHistory()`
    -   `createMemoryHistory()`会完全忽略浏览器的 URL 而使用其自己内部的 URL
    
-   `app#use` 注册为插件

-   path中的路径值, 使用`:`表示接下来是占位符, 例如

    ```js
    const router = createRouter({
      history: createWebHistory(),
      routes: [
        {
            path: '/node1', 
         	component: Node1,
         	children:{
            	path: '/:id',
            	component: Node1Child
        	}
        },
        {path: '/node2', component: Node2},
      ]
    });
    ```

    此时在`<router-link>`中配置`<router-link :to="'/'+id" >`即可



1.  全局注册 `RouterView` 和 `RouterLink` 组件。
2.  添加全局 `$router` 和 `$route` 属性。
3.  启用 `useRouter()` 和 `useRoute()` 组合式函数。
4.  触发路由器解析初始路由



绘制导航页, Root.vue, 样式略 

```vue
<template>
  <h2>标题</h2>
  <div style="float: left;">
    <ul id="router-list">
      <li>
        <router-link to="/node1">node1</router-link>
      </li>
      <li>
        <!--路由跳转连接-->
        <router-link to="/node2">node2</router-link>
      </li>
    </ul>
  </div>
  <div id="node-place">
    <!--路由目标插入区-->
    <router-view></router-view>
  </div>
</template>
```

效果

<video src="../../../assets/Day7-router/router演示.mp4" style="border: 2px dashed"></video>

## 原理

在`<router-link>`的地方会转为`<a>`, 然后超链接(路由)到目标地址(`target="_self"`)

目标地址对应着的资源, 也是html文件, 只不过将`<router-view>`以外的内容原样拷贝不变

而`<router-view>`被vue替换成了资源的`<template>`内的内容

组件 RouterView 和 RouterLink 都是全局注册的，因此它们不需要在组件模板中导入, 或者

```js
import { RouterLink } from 'vue-router'
```





## 访问路由器

-   router 路由器
-   route 当前路由

Optional 使用属性`$router`

```js
export default {
  methods: {
    goToAbout() {
      this.$router.push('/about')
    },
  },
}
```

`push`使用了编程式导航

setup 不能使用this, 于是使用组合式函数

```vue
<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()

const search = computed({
  get() {
    return route.query.search ?? ''
  },
  set(search) {
    router.replace({ query: { search } })
  },
})
</script>
```

## 监听路由变化

路由改变, router-view会缓存原先路由到的组件的资源, 而不会及时更新

-   使用router-view的属性`key`, 每次更新, key都更新, 则会强制删除组件缓存

-   在添加事件`onBeforeRouterUpdate`钩子, 哪些资源进行了改变, 手动进行更改

    -   `onBeforeRouterUpdate`钩子写在哪? 写在`router-view`所在的组件吗?

        哪里是`BeforeRouterUpdate`事件发生的组件, 就在哪里创建事件钩子

    -   

## 路由滚动行为

路由的组件进行切换时, 滚动条依旧在原处, 而不会向上滚动到原始位置

```js
import {scrollBehavior} from "vue-router";
const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routers: [...],
    scrollBehavior(){
    	return {top: 0};
	}
});
```


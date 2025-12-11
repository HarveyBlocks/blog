# 插件

Pinia 插件是一个自定义函数，可以选择性地返回要添加到 store 的属性

```js
export function piniaPlugin(context) {
  context.pinia // 用 `createPinia()` 创建的 pinia。
  context.app // 用 `createApp()` 创建的当前应用(仅 Vue 3)。
  context.store // 该插件想扩展的 store
  context.options // 定义传给 `defineStore()` 的 store 的可选对象。
  // ...
}
```

为pinia安装插件

```js
// 创建的每个 store 中都会被插件影响
const pinia = createPinia()
```

插件会应用于**在 `pinia` 传递给app后**创建的 store

## 添加属性

插件返回值返回一个对象, 对象的键就是每个store会增加的属性

```js
pinia.use(() => ({ hello: 'world' }))
```

或者使用上下文来设置

```js
pinia.use(context => {
  context.store.hello = 'world'
})
```

由于store使用reactive包装, 因此会解包新属性的Ref(`ref()`,`completed()`,...)

```js
const sharedRef = ref('shared')
pinia.use(context => {
  const store = context.store;
  store.hello = ref('secret')
  // 它会被自动解包
  console.log(store.hello); // 'secret'

  // 所有的 store 都共享 `shared` 属性的值
  store.shared = sharedRef
  console.log(store.shared);
})
```

### 添加对象

当添加一个复杂对象的时候, 使用`toRaw`包装, 将一个对象标记为不可被转为代理, 返回该对象本身

```js
import { markRaw } from 'vue'
// 根据你的路由器的位置来调整
import { router } from '@/router'

pinia.use(context => {
  context.store.router = markRaw(router)
})
```



## state

### 添加新的state

在 `store.$state` 上添加`state`，然后你才可以在 devtools 中使用它，并且，**在 SSR 时被正确序列化**

使用ref()保证使用`store.member` 和`store.$state.member`总是一致

```js
import { toRef, ref } from 'vue'

pinia.use(context => {
  const store = context.store;
  // 为了正确地处理 SSR，确保不重写一个现有的值
  if (!store.$state.hasOwnProperty('hasError')) {
    // 在插件中定义 hasError, 保证每个 store 都有各自的 hasError 的 state
    // 在 `$state` 上设置变量，允许它在 SSR 期间被序列化
    store.$state.hasError = ref(false);
  }
  // 将 ref 从 state 转移到 store
  // store.hasError 和 store.$state.hasError 都可以访问并且共享的是同一个变量
  store.hasError = toRef(store.$state, 'hasError')
})
```

-   最好不要返回 `hasError`, 因为它将被显示在 devtools 的 `state` 部分. 如果返回，devtools 上将显示两次
-   state 变更或添加(包括调用 `store.$patch()`) 发生在 store 被激活之前，**因此不会触发任何订阅函数**

### reset state

默认情况下，`$reset()` 不会重置插件添加的 state

通过重写`$reset`来重置添加的state

```js
import { toRef, ref } from 'vue'

pinia.use(context => {
  const store = context.store;
  if (!store.$state.hasOwnProperty('hasError')) {
    store.$state.hasError = ref(false)
  }
  store.hasError = toRef(store.$state, 'hasError')

  // 将 this 设置为 store
  const originalReset = store.$reset.bind(store)

  // 覆写其 $reset 函数
  return {
    $reset() {
      originalReset()
      store.hasError = false
    },
  }
})
```

## subscribe 和 onAction

```js
pinia.use(context => {
  const store = context.store;
  store.$subscribe(() => {
    // 响应 store 变化
  })
  store.$onAction(() => {
    // 响应 store actions
  })
})
```

## 选项

依据选项过滤store的成员, 然后增强 

例如自定义选项

```js
import {defineStore} from 'pinia'

export const useCounterStore = defineStore('counter', () => {
  const count = ref(0);
  function increment = ()=>count.value++;
  return {count,increment};
}, {
  myOptionOnType: {
    increment: 'unsigned'
  },
});
```

添加对应的增强插件

```js
import {createApp} from 'vue'
import App from './App.vue'
import {createPinia} from 'pinia'

const pinia = createPinia();

function adviceOnType(action, type) {
  // k
  if (type !== 'unsigned') {
    return action;
  }
  return () => {
    let result = action();
    if (result < 0) {
      console.warn("not unsigned");
    }
    return result;
  }
}

pinia.use(context => {
  const options = context.options;
  if (!options.myOptionOnType) {
    return {};
  }
  let store = context.store;
  // 用增强后的 members 来覆盖 老members
  return Object.keys(options.myOptionOnType).reduce((newActions, actionName) => {
    newActions[actionName] = adviceOnType(store[actionName], options.myOptionOnType[actionName])
    return newActions
  }, {});/*初始值是空对象*/
});
createApp(App).use(pinia).mount('#app')

```


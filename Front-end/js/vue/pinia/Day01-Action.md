# Action

使用setup钩子, 以此使用Store中的Action

或者使用mapActions

## 订阅

通过 `store.$onAction()` 来监听 action 和它们的结果

```vue
<script setup>
import {useCounterStore} from "@/stores/counter.js";

const counterStore = useCounterStore();
const counterAdvice = (actionMessage) => {
  const name = actionMessage.name; // action 名称
  const store = actionMessage.store; // store 实例，类似 `someStore`
  const args = actionMessage.args; // 传递给 action 的参数数组
  const after = actionMessage.after; // 在 action 返回或解决后的钩子
  const onError = actionMessage.onError; // action 抛出或拒绝的钩子
  // 在执行 "store" 的 action 之前触发
  console.log(`before: ${store}.${name}(${args})`);
  after((result) => {
    // action 成功运行后触发
    console.log("after: " + result);
  });
  onError((error) => {
    // action 抛出异常后触发
    console.warn("error: " + error)
  });
}
// 返回值用于手动接触订阅
const unsubscribe = counterStore.$onAction(counterAdvice);
</script>

<template>
  <button @click="counterStore.increment">incr</button>
  {{ counterStore.count }}
</template>
```

用`$onAction()`的返回值主动接触订阅

```js
unsubscribe()
```

一般的监听是挂载在组件上的, 如果希望组件卸载后监听依然存在, 可以在`$onAction`的第二个参数上配置

```js
counterStore.$onAction(counterAdvice,true);
```


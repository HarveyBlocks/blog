# State

## reset

```js
const store = useStore()

store.$reset()
```

在option store中, $reset内部会调用`state()`函数创建一个新的状态对象那个, 用于替换当前状态

**在setup store中需要自己创建`$reset()`方法**

```js
export const useCounterStore = defineStore('counter', () => {
  const count = ref(0)

  function $reset() {
    count.value = 0
  }

  return { count, $reset }
})
```

## patch

传入一个对象, 用于对数据进行修改

```js
store.$patch({
  count: store.count + 1
})
```

如果要修改的state是数据集合, 则需要重新创建数组, 这不好, 因此允许传入函数

```js
store.$patch((state) => {
  state.items.push({ name: 'shoes', quantity: 1 });
})
```



## 监听

比起的 `watch()`，使用 `$subscribe()` 的好处是 *subscriptions* 在 *patch* 后只触发一次

例如当一次patch中, state修改了多次, 算作一次

```vue
<script setup lang="ts">
import {useCounterStore} from '@/stores/counter'
import {MutationType} from 'pinia'

const counterStore = useCounterStore();
counterStore.$subscribe((mutation: MutationType, state) => {
  console.log(mutation.type); // 'direct' | 'patch object' | 'patch function'
  // 等价于 counterStore.$id 一样.
  console.log(mutation.storeId); // 'cart'
  if (mutation.type === 'patch object') {
    // 只有 mutation.type === 'patch object' 的情况下才可用
    console.log(mutation.payload); // 传递给 counterStore.$patch() 的补丁对象。
  }

  // 展示state
  console.log(`counter: ${JSON.stringify(state)}`);
});
</script>
```

-   第二个参数可以传入与 `watch()` 相同的选项

-   

-   订阅默认会绑定到组件上, 组件卸载也会自动删除

    如果想在组件卸载后保留此订阅, 使用`detached` 选项及那个此订阅从当前组件分离

    ```vue
    <script setup>
    const someStore = useSomeStore()
    // 此订阅器即便在组件卸载之后仍会被保留
    someStore.$subscribe(callback, { detached: true })
    </script>
    ```

下面是传统watch, 也是可以正常运行的

```js
watch(
  pinia.state,
  (state) => {
    console.log(state);
  },
  { deep: true }
)
```


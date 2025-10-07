# Pinia

`setup` 函数内部、`getter` 和 `action` 的顶部调用你定义的 `useStore()`时, 是可以正常创建store的

但在以上例子之外, 直接调用`useStore()` 函数实际上早于Pinia被挂载到App上, 因此会无法构建

因此, 这种情况下, 将pinia对象作为参数传入, `useStore(pinia)`来保证构建

```js
const pinia = createPinia()
const app = createApp(App)

app.use(router)
app.use(pinia)

router.beforeEach((to) => {
  // 确保了正确的 store 被用于当前正在运行的app
  const main = useMainStore(pinia);
  return main.path;
})
```

Pinia 会将自己作为 `$pinia` 添加到app中

可以在`serverPrefetch()`中使用`this.$pinia`获取实例

```js
export default {
  serverPrefetch() {
    const store = useStore(this.$pinia)
  },
}
```

## State 激活


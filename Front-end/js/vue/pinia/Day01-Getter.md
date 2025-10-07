# Getter

Getter完全等同于computed

## 访问其他Getter

对于Optional API, 如果使用**函数简写**+this, this将表示**整个store实例**

此时必须指定函数类型

```js
export const useCounterStore = defineStore('counter', {
  state: () => ({
    count: 0,
  }),
  getters: {
    // 自动推断出返回类型是一个 number
    double(state) {
      return state.count * 2
    },
    /**
     * @returns {number} 返回类型必须明确设置
     */
    doublePlusOne() {
      //  store
      return this.doubleCount + 1
    },
  },
})
```

或者使用TypeScript的返回值类型声明

## 访问其他Store的Getter

直接使用即可

```js
import { useOtherStore } from './other-store'

export const useStore = defineStore('main', {
  state: () => ({
    // ...
  }),
  getters: {
    otherGetter(state) {
      const otherStore = useOtherStore()
      return state.localData + otherStore.data
    },
  },
})
```

## Optional API 使用Getter

用mapState, 像使用State一样使用Getter

或者使用setup钩子, 

```vue
<script>
import { useCounterStore } from '../stores/counter'
export default defineComponent({
  setup() {
    const counterStore = useCounterStore()
    return { counterStore }
  },
  methods: {
    printDouble() {
      console.log('New Count:', this.counterStore.double);
    },
  },
})
</script>
```


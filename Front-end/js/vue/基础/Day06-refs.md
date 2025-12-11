# 参考

在不得不访问底层DOM元素的时候在标签上使用`ref`属性

## 语法

### 选项式

```vue
<script>
export default {
  mounted() {
    this.$refs.input.focus()
  }
}
</script>

<template>
  <input ref="input" />
</template>
```



### 组合式

```vue
<script setup>
import { useTemplateRef, onMounted } from 'vue'

// 第一个参数必须与模板中的 ref 值匹配
const input = useTemplateRef('my-input')

onMounted(() => {
  input.value.focus()
})
</script>

<template>
  <input ref="my-input" />
</template>
```

## 组件上的ref

如果在组件标签上使用ref属性, 引用中获得的值是组件实例

```vue
<script setup>
import { useTemplateRef, onMounted } from 'vue'
import Child from './Child.vue'

const childRef = useTemplateRef('child')

</script>

<template>
  <Child ref="child" />
</template>
```

## v-for中使用ref

 ref 中包含的值是一个数组

```vue
<li v-for="item in list" ref="items">
  {{ item }}
</li>
```



## 引用函数

ref属性的值可以是一个函数, 每次组件更新时都被调用

函数会收到元素引用作为其第一个参数

```vue
<input :ref="(element) => { /* code */ }">
```


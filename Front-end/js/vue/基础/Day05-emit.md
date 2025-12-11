# emit

用于子组件自定义事件, 父组件在这个事件上添加回调函数

事件发生在子组件

```vue
<ChildNode @self-event="alertHello"/>
```

## 语法

Root.vue

```vue
<script setup>

import ChildNode from "@/components/ChildNode.vue";

function alertHello(msg) {
  window.alert('hello ' + msg);
}
</script>

<template>
  <ChildNode @self-event="alertHello"/>
</template>
```

### 选项式

ChildNode.vue

```vue
<script>
export default {
  methods: {
    selfEvent() {
      // 带参数触发, 参数可选
      this.$emit('self-event', 'child');
    }
  }
};
</script>
<template>
  <button @click="selfEvent">button</button>
</template>
```



### 组合式

ChildNode.vue

```vue
<script setup>
let emit = defineEmits(['self-event']);
function selfEvent() {
  // 带参数触发, 参数可选
  emit('self-event', 'child')
}
</script>
<template>
  <button @click="selfEvent">button</button>
</template>
```



不仅可以在子组件触发事件时触发父组件的事件, 也可以在某值变化时, 需要看需求了


# Provide-Inject

一种可以把数据直接传递给任意层后代节点(不论深度的后代)的方法

在祖先层provide, 在后代inject

单向的, 不允许从后代inject到祖先

缺点, 祖先组件必须要看后代的源码, 才知道后代需要注入什么数据, 而且无法跟踪注入的数据会被如何使用

## 语法

### 组合式

RootNode.vue, provide

```vue
<script setup>
import {provide} from "vue";
import ChildNode from "@/components/ChildNode.vue";

provide('key', {a: 12, b: 21})

</script>

<template>
  <ChildNode/>
</template>
```

ChildNode.vue. inject

```vue
<script setup>
import {inject} from "vue";

const value = inject('key');

function show() {
  value.a = 111;
  return value;
}
</script>

<template>
  {{ show() }}
</template>
```

### 选项式

RootNode.vue, provide

```vue
<script>
import ChildNode from "@/components/ChildNode.vue";

export default {
  components: {
    ChildNode
  },
  provide: {
    injectKey: {a: 12, b: 21}
  }
}
</script>

<template>
  <ChildNode/>
</template>
```

ChildNode.vue. inject

```vue
<script>

export default {
  inject: ['injectKey'],
  methods: {
    show() {
      this.injectKey.a = 111;
      return this.injectKey;
    }
  }
}
</script>

<template>
  {{ show() }}
</template>
```
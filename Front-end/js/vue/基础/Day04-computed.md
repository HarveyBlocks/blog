# computed

>   计算属性

计算属性提供一个getter

自动追踪响应式依赖

vue会检测到 getter 依赖于 某一属性，当 被依赖是属性 改变时，任何依赖于 getter 的绑定都会同时更新

对于文本插值进步的点在于, 可以在函数里进行复杂的运算

## 语法

做一个按钮, 按钮上显示on和off

### 选项式

```vue
<script>
export default {
  data() {
    return {
      on: false
    }
  },
  computed: {
    onStr() {
      return this.on ? 'on' : 'off';
    }
  }
}

</script>

<template>
  <button @click="on=!on">{{ onStr }}</button>
</template>
```

### 组合式

```vue
<script setup>
import {computed, ref} from "vue";

const on = ref(false);
const onStr = computed(() => {
  return on.value ? 'on' : 'off';
});

</script>

<template>
  <button @click="on=!on">{{ onStr }}</button>
</template>
```

## 序列显示

```vue
<script setup>
import {computed, ref} from "vue";

let start = ref(0);
let end = ref(0);
let step = ref(0);
let range = []
// 返回一个计算属性ref
const rangeObject = computed(() => {
  if (end.value === start.value) {
    return {range: range, msg: '不产生序列'};// 返回上一次的结果
  }
  if (step.value * (end.value - start.value) <= 0) {
    // 会产生无穷多range的情况
    return {range: range, msg: '产生无穷多序列'};// 返回上一次的结果
  }
  range = [];
  for (let i = start.value; (i - end.value)*step.value < 0; i += step.value) {
    range.push(i);
  }
  return {range: range, msg: ''};
});

</script>

<template>
  start: <input v-model="start" type="number"><br>
  end: <input v-model="end" type="number"><br>
  step: <input v-model="step" type="number"><br>
  <div v-show="rangeObject.msg!==''">{{ rangeObject.msg }}</div>
  <ul>
    <li v-for="(i) in rangeObject.range">{{ i }}</li>
  </ul>
</template>
```


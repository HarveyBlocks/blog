# watch

>   观察者

## 语法

其实观察者, 如果new和old的值是一样的话, 那是不会触发的

### 选项式



```vue
<script>
export default {
  data() {
    return {
      watchSource: ''
    }
  },
  watch: {
    // 每当 question 改变时，这个函数就会执行
    watchTarget(newSource, oldSource) {
      console.log(newSource);
      console.log(oldSource);
    }
  }
}
</script>
<template>
  <div>
    <input type="text" v-model="watchSource">
    {{watchSource}}
  </div>
</template>
```

### 组合式

```vue
<script setup>
import {ref, watch} from "vue";

const watchSource = ref('');
watch(watchSource,(newSource,oldSource)=>{
  console.log(newSource);
  console.log(oldSource);
})
</script>
<template>
  <div>
    <input type="text" v-model="watchSource">
    {{watchSource}}
  </div>
</template>
```

## 有回退的文本编辑器

CTRL+Z 回退

CTRL+Y 再返回

```vue
<script setup>
import {ref, watch} from "vue";

const text = ref('');
let stack = [];
let flag = false
let top = -1; // 指向当前显示的语句
watch(text, (newSource, oldSource) => {
  if (flag){
    // 是有意改变
    flag = false;
    return;
  }
  // 文本改变了
  // 那么stack的top之后都要删除了
  stack[++top] = newSource;
  if (top + 1 < stack.length) {
    // 不是最后. 后面全部清理
    stack = stack.slice(0, top + 1);
  }
})

function back() {
  if (top !== -1) {
    flag = true;
    text.value = stack[--top];
  }
}

function fore() {
  if (top + 1 !== stack.length) {
    flag = true;
    text.value = stack[++top];
  }
}

</script>

<template>
  <textarea v-model="text"
            @keydown.ctrl.z.prevent="back"
            @keydown.ctrl.y.prevent="fore"
  ></textarea>
</template>
```

-   Q: **watch监听的变化, 对象属性的变化能否监听到?** 

    A: 不行, 由于ref只代理最外层

-   Q: **watch**里面发生监听对象的变化, 会不会触发watch?

    A: 会

-   Q: 改变newSource和oldSource会不会触发watch?

    A: 不会

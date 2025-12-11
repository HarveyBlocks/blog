# props

>   选项

组件为了提高复用性, 应当能从外界接收一些"参数", 这些"参数"被称为选线/props



## 语法



Props 属性用破折号 `-` 编写（kebab-case）

需要在 JavaScript 中将属性名称编写为驼峰命名法，Vue 会**自动解析**



```vue
<ItemEntry attribute = "value" />
```

-   attribute的是可以选择加前缀指令`v-bind`的

### 选项式

```vue
<script>
  export default {
    props: {
      attribute: {
        /**
         * @type  {import('vue').PropType<{
         *   flag: boolean;
         *   value: string;
         * }>}
         */
        type: Object,
        required: true // 是否必传, 可选
      }
    }
  }
</script>
```

其实`props`可以直接是一个**字符串数组**

```vue
<script>
  export default {
    props: ['attribute']
  }
</script>
```

也可以是一个简单的对象, 用于只是类型

```vue
<script>
  export default {
    props: {
        attribute: Object
    }
  }
</script>
```

但这样可以给IDE提供的信息太少了, 不推荐

<img src="../../../assetss/Day05-props 选项/image-20250817215122384.png" alt="image-20250817215122384" style="zoom:50%;" />

### 组合式

```vue
<script setup>
//  defineProps() 是一个编译时宏，不需要导入
// 返回值props, 非必须, 用于在脚本中调用参数
const props = defineProps({
  attribute: {
    /**
     * @type  {import('vue').PropType<{
     *   flag: boolean;
     *   value: string;
     * }>}
     */
    type: Object,
    required: true // 根据需要添加是否必传
  }
});
</script>
```

## `default` 默认值

不必须的值加上一个默认值, 有助于代码的运行

```vue
<script setup>

const props = defineProps({
  attribute: {
    /**
     * @type  ...
     */
    type: Object,
    // required: required默认false,
    default: {value: "",flag: false}
  }
});

</script>
```

如果既没有default, 也不是required, 会是什么结果?

```js
onMounted(()=>{
  console.log(props.attribute);
})
```

是==undefined==

## `validator` 验证器函数

-   验证器函数必须返回 true 或 false, false for 无效
-   无效的props会在控制台中生成警告



```vue
<script setup>

const props = defineProps({
  attribute: {
    /**
     * @type  ...
     */
    type: Object,
    validator: function(value) {
      // code...
    }
  }
});

</script>
```

## 改变props的值

由于props是只读的, 改变props的值是不可能的, 而且还会报错

有些改变, 我们希望外界可见, 有些改变, 希望外界不可见

外界不可见的, 可以在组件的对象里重新定义一个data, 将props作为初值赋值给data

而希望外界可变的, 可以将目标作为对象封装, 而后传给子组件

### 封装对象

但是可以将目标封装在对象里, 然后作为props传递, 这样目标就可以改动了

#### ChildNode.vue

```vue
<script setup>
defineProps({
  obj: {
    /**
     * @type  {import('vue').PropType<{
     *   value: number;
     * }>}
     */
    type: Object,
    required: true,
  }
});

</script>

<template>
  <button @click="obj.value++">+1</button>
</template>


```



#### Root.vue

```vue
<script setup>

import {ref} from "vue";
import ChildNode from "@/components/ChildNode.vue";

const counter = ref({value: 0});
</script>

<template>
  <ChildNode :obj="counter"/>
  {{counter.value}}
</template>
```


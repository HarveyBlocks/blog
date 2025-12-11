# 动态组件

## `<component>`标签

-   属性 `is`, `is`指定标签名, 这个component就是这个标签了

```vue
<component is="div"></component>
```

那么这个component的位置就是div了

如果在is前加上`v-bind`(`:`), 就可以动态地指定用组件了

```vue
<script setup>
import ChildNode from "@/components/ChildNode.vue";
import {ref} from "vue";

const choose = ref(false);
</script>

<template>
  <p>
    <button @click="choose=!choose">switch</button>
  </p>
  <p>
    <component :is="choose?ChildNode:'div'"></component>
  </p>
</template>
```

可以在组件ChildNode和一个div之间转换

## keep-alive

### 保持`<component>`的数据

`<component>`在动态转换的时候, 会**重新加载组件**

那么, 上面的例子, 按下两次`switch`按钮之后, 原本在ChildNode中存在的数据就会被刷新掉

<video src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/vue/基础/Day06-动态组件/组件转换刷新导致的问题.mp4" style="border:2px solid black"></video>

使用`<keep-alive>`标签包裹`<component>`来解决

```vue
<keep-alive>
  <component :is="choose?ChildNode:'div'"></component>
</keep-alive>
```

### 筛选要保持活动的组件

"include"或"exclude"属性来仅定义一些要保持活动的组件

```vue
<keep-alive exclude="ChildNode">
  <component :is="choose?ChildNode:'div'">AAA</component>
</keep-alive>
```

如果要指定多个, 就用`,`分割

```vue
<keep-alive exclude="ChildNode1,ChildNode2">
  <component :is="choose?ChildNode:'div'">AAA</component>
</keep-alive>
```

#### exclude的值

exclude不用v-bind修饰也能识别组件, 为何? 它识别的是什么==component.name==

1.   exclude 在使用v-bind修饰之后, 允许字符串数组, 允许字符串, 但如果数组元素是compent对象, 并不会被识别

2.   component.name是vue自动设置的

     在vue中, component.name是vue自动设置的, 就是文件名去掉vue后缀

     也就是说, 和import的关系不大, import的是componet这个对象的alias, 其name还是`'ChildNode'`

     ```js
     import cn from "@/components/ChildNode.vue";
     ```

3.   `<componet>`动态加载多个组件, 如果是同名组件, 会使用同一片内存

     使用`<component>`标签来动态加载的component, 如果name一样, 那么这些component用的是同一片内存, 一个component改变了内容, 其他会产生改变

     ```vue
     <script setup>
     import ChildNode1 from "@/components/ChildNode.vue";
     import ChildNode2 from "@/components/ChildNode.vue";
     import {ref} from "vue";

     const choose = ref(false);
     </script>

     <template>
       <p>
         <button @click="choose=!choose">switch</button>
       </p>
       <p>
           <component :is="choose?ChildNode1:ChildNode2"></component>
       </p>
     </template>
     ```

     但下面这样是用两片内存:

     ```vue
     <component :is="ChildNode"></component>
     <component :is="ChildNode"></component>
     ```

4.   可以使用`defineComponent`来让自己定义组件名, 同时避免componet使用同一片内存

     defineComponent的具体使用此处略

     defineComponent本来用来定义完整组件

     ```js
     import {defineComponent, ref} from "vue";

     export default defineComponent({
       name: "ChildComponent",
       props: {
         initialCount: {
           type: Number,
           default: 0
         }
       },
       setup(props){
         const count = ref(props.initialCount);

         const increment = () => {
           count.value++;
         };

         return {
           count,
           increment
         };
       },
       template: `
         <div>
           <p>Count: {{ count }}</p>
           <button @click="increment">Increment</button>
         </div>
       `
     });
     ```

     因此我认为使用defineComponent,来创建两个新组件的方式, 来区分并不合适, 因为这样并不是"单文本组件"了. 在无形之中增加了两个组件....

     ```vue
     <script setup>
     import ChildNode from "@/components/ChildNode.vue";
     import {defineComponent, ref} from "vue";

     const ChildNode1 = defineComponent({
       name: 'cn1',
       extends: ChildNode
     });
     const ChildNode2 = defineComponent({
       name: 'cn2',
       extends: ChildNode
     });
     const choose = ref(false);
     </script>

     <template>
       <p>
         <button @click="choose=!choose">switch</button>
       </p>
       <p>
         <keep-alive :exclude="['cn1','cn2']">
           <component :is="choose?ChildNode1:ChildNode2"></component>
         </keep-alive>
       </p>
     </template>
     ```

### 'max' 属性

限制浏览器需要记住其状态的组件数量

```vue
<keep-alive  :max="1">
    <component :is="choose?ChildNode:'div'"></component>
</keep-alive>
```

语法如此, 生效机制就不明白了


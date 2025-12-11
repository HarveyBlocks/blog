# 组件

## 根组件

每个应用都需要一个“根组件”

其他组件将作为其子组件

```js
import { createApp } from 'vue'
// 从一个单文件组件中导入根组件
import App from './App.vue'

const app = createApp(App)
```

如果是组合式, 那就是单文件组件, 从这个单文件import的就是组件

如果是选项式, 可以自定义一个文件中`export`几个组件, 而ipmort的哪个组件需要自己选择

## 挂载

把组件挂载到Html元素上, 就是把对Html的设置(这些设置封装在组件里), 然后经过Vue编译后转为传统的JS+HTML+CSS

组件里封装CSS样式, JS脚本逻辑, Html 内部的元素, 以及嵌套的组件

用`mount`方法对App进行挂载

该方法接收一个 '容器' 参数, 可以是实际的DOM-Element 或者是一个CSS 选择器字符串

```html
<div id="app"></div>
```

在html准备容器

```js
createApp(App).mount(document.getElementById('app'));
```

参数是DOM的Element

```js
createApp(App).mount('#app');
```

参数是CSS 选择器

## 组件引入

### 引入另一组件

#### 选项式

```vue
<script>
// 导入组件, 取名
import ItemEntry from "@/components/ItemEntry.vue";

export default {
  components: {
    ItemEntry
  }
}
</script>

<template>
   <!--使用组件-->
   <ItemEntry />
</template>
```

#### 组合式

```vue
<script setup>
// 导入组件, 取名
import ItemEntry from "@/components/ItemEntry.vue";
</script>

<template>
   <!--使用组件-->
   <ItemEntry />
</template>
```

### 多组件示例

样式暂略

[原型](Day03-v-model.md#购物清单, 但是双击更新)

#### index.html

模板, 不变

```html
<!DOCTYPE html>
<html lang="">
<head>
    <meta charset="UTF-8">
    <link rel="icon" href="/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vite App</title>
    <style>
        * {
            box-sizing: border-box;
        }

    </style>
</head>
<body>
<div id="root"></div>
<script type="module" src="/src/main.js"></script>
</body>
</html>
```

#### main.js

模板, 不变

```vue
import './assets/main.css'

import {createApp} from 'vue'
import root from './Root.vue'

createApp(root).mount('#root');
```

#### Root.vue

父组件

```vue
<script setup>

import {ref} from "vue";
import ItemEntry from "@/components/ItemEntry.vue";
const newItemName = ref('');
const newItemCount = ref(0);
const items = ref(new Map());
let id = 0;

function deleteItem(targetId) {
  items.value.delete(targetId);
}

function addItem() {
  let newItem = {
    id: id++,
    name: {value: ref(newItemName.value), update: false},
    count: {value: ref(newItemCount.value), update: false},
  };
  newItemName.value = '';
  newItemCount.value = 0;
  items.value.set(newItem.id, newItem);
}
</script>

<template>
  <form @submit.prevent="addItem">
    <input type="text" placeholder="item name..." v-model="newItemName" required>
    <input type="number" placeholder="item count..." v-model="newItemCount" min="0">
    <input type="submit" value="新增">
  </form>
  <div>
    <ul>
      <li v-for="([key,item],_) in items">
        <span v-for="(updatable,index) in [item.name,item.count]">
          <!--在非第一个元素之前加入分隔符-->
          <span v-show="index!==0">,</span>
          <ItemEntry :updatable = "updatable" />
        </span>
        <button @click="deleteItem(key)">X</button>
      </li>
    </ul>
  </div>
</template>
```

#### ItemEntry.vue

子组件

defineProps 解释见其他文档

```vue
<script setup>

function changeToUpdate(updatable) {
  updatable.updating = true;
}

function completeUpdate(event, updatable) {
  const isValid = event.target.checkValidity();
  if (isValid) {
    updatable.updating = false;
  } else {
    // 验证失败，触发浏览器默认的验证提示
    event.target.reportValidity();
  }
}

//  defineProps() 是一个编译时宏，不需要导入
const props = defineProps({
  updatable: {
    /**
     * @type  {import('vue').PropType<{
     *   updating: boolean;
     *   value: string;
     * }>}*/
    type: Object,
    required: true // 根据需要添加是否必传
  }
});

</script>

<template>
  <span v-show="!updatable.updating"
        @dblclick="changeToUpdate(updatable)">
            {{ updatable.value }}
          </span>
  <input v-show="updatable.updating"
         v-model="updatable.value"
         required
         type="text"
         @focusout="completeUpdate($event,updatable)"
         @keyup.enter="completeUpdate($event,updatable)">

</template>
```

## Fallthrough 属性

>   ckass 和 stlye 的合并

如果在父组件里为子组件定义了`class`或者`style`属性, 而子组件里也定义了`class`或者`style`属性, 那么两种属性将被Vue合并

Root.vue

```vue
<template>
  <ChildNode style="padding: 2px;"/>
</template>
```

ChildNode.vue

```vue
<template>
  <li style="margin: 5px 0;">{{ itemName }}</li>
</template>
```

最终都会合并到这个`<li>`标签上


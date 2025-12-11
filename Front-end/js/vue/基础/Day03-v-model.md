# v-model

## 输入事件监听的简化

传统的监听文本输入, 每次输入都要调用方法, 然后还要从event里取值, 比较繁琐

```vue
<script setup>

import {ref} from "vue";

const textInput = ref('');

function inputText(event) {
  textInput.value = event.target.value;
}

</script>
<template>
  <input type="text" @input="inputText">
  <div>
    {{ textInput }}
  </div>
</template>
```

改用v-model监听, 简化这一步骤

```vue
<script setup>

import {ref} from "vue";

const textInput = ref('');

</script>
<template>
  <input type="text" v-model="textInput">
  <div>
    {{ textInput }}
  </div>
</template>
```

和原来的有一点点区别, 用中文输入法的时候, 拼音的部分不会改变model

## 购物清单

```vue
<script setup>

import {ref} from "vue";

const newItemName = ref('');
const newItemCount = ref(0);
const items = ref([]);
let id = 0;

function addItem() {
  items.value.push({name: newItemName.value, count: newItemCount.value, id: id++});
  newItemName.value = '';
  newItemCount.value = 0;
}

function deleteItem(targetId) {
  items.value = items.value.filter(item => item.id !== targetId);
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
      <li v-for="item in items">{{ item.name }}, {{ item.count }}
        <button @click="deleteItem(item.id)">X</button>
      </li>
    </ul>
  </div>
</template>
```

-   为什么在submit按钮上`@click.prevent="addItem"`不可行?

    为什么要监听的事件是submit, 而不是click?

    因为`required`的检查

    1.   click
    2.   检查required
    3.   submit的默认逻辑

-   为什么在按钮上监听是不好的, 要在form上监听?

    因为在文本框里回车也会指向submit, 而在按钮上监听是管不到文本框里的回车的

![image-20250816003114114](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/vue/基础/Day03-v-model/image-20250816003114114.png)

### 标记找到

一条记录被点击后, 进入另一个列表, 表示购物清单的item被找到和未被找到

这种行为不同于删除, 不具备持久性

下面是不需要在Javascript中构建两个list的方法

```vue
<ul id="ulToFind">
  <li 
    v-for="item in items" 
    v-on:click="item.found=!item.found"
    v-show="!item.found">
      {{ item.name }}, {{ item.count}}
  </li>
</ul>
<ul id="ulFound">
  <li 
    v-for="item in items" 
    v-on:click="item.found=!item.found"
    v-show="item.found">
      {{ item.name }}, {{ item.count}}
  </li>
</ul>
```

## 购物清单, 但是双击更新

```vue
<script setup>

import {ref} from "vue";

const newItemName = ref('');
const newItemCount = ref(0);
const items = ref(new Map());
let id = 0;

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

function deleteItem(targetId) {
  items.value.delete(targetId);
}

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
        </span>
        <button @click="deleteItem(key)">X</button>
      </li>
    </ul>
  </div>
</template>
```


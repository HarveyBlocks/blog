# 表单

## v-model的双向绑定

在表单输入上使用 `v-model` 会创建双向绑定

如果 Vue 数据实例发生变化，输入 `value` 属性也会发生变化。

### 复选框

多个复选框的`v-model`选择同一个list, 这几个复选框就会往这一个list里面填值

```vue
<template>
  <form @submit.prevent="registerAnswer">
    <label>
      <input type="checkbox" v-model="likeFoods" value="Pizza"> Pizza
    </label>
    <label>
      <input type="checkbox" v-model="likeFoods" value="Rice"> Rice
    </label>
    <label>
      <input type="checkbox" v-model="likeFoods" value="Fish"> Fish
    </label>
    <label>
      <input type="checkbox" v-model="likeFoods" value="Salad"> Salad
    </label>
    <button type="submit">Submit</button>
  </form>
  <div>
    <h3>实时:</h3>
    <p >{{ likeFoods }}</p>
  </div>
  <div>
    <h3>提交后:</h3>
    <p>{{ inpValSubmitted }}</p>
  </div>
</template>

<script setup>
import {ref} from "vue";

const likeFoods = ref([]);
const inpValSubmitted = ref('等待提交');

function registerAnswer() {
  inpValSubmitted.value = likeFoods.value.toString();
}
</script>
```

### 下拉列表

下拉列表是用string类型的变量接收数据

```vue
<template>
  <form @submit.prevent="registerAnswer">
    <p>
      <select  v-model="carSelected">
        <option disabled value="">....</option>
        <option>A</option>
        <option>B</option>
        <option>C</option>
        <option>D</option>
      </select>
    </p>
    <input type="submit" value="Submit">
  </form>
  <div>
    <p>{{ inpValSubmitted }}</p>
  </div>
</template>

<script setup>
import {ref} from "vue";

const UNSELECT_MESSAGE = '未选择';
const carSelected = ref('');
const inpValSubmitted = ref(UNSELECT_MESSAGE);

function registerAnswer() {
  if (carSelected.value===''){
    inpValSubmitted.value = UNSELECT_MESSAGE;
    return;
  }
  inpValSubmitted.value = carSelected.value;
}
</script>
```

如果用`multiple`属性修饰下拉菜单标签`<select>`, 那么需要用array来接收数据

`multiple`如果要复选, 需要长按ctrl键

```vue
<template>
  <form @submit.prevent="registerAnswer">
    <p>
      <select  v-model="carSelected" id="cars" multiple>
        ...
      </select>
    </p>
    <input type="submit" value="Submit">
  </form>
  <div>
    <p>{{ inpValSubmitted }}</p>
  </div>
</template>

<script setup>
import {ref} from "vue";

const UNSELECT_MESSAGE = '未选择';
const carSelected = ref([]);
const inpValSubmitted = ref(UNSELECT_MESSAGE);

function registerAnswer() {
  if (carSelected.value.length===0){
    inpValSubmitted.value = UNSELECT_MESSAGE;
    return;
  }
  inpValSubmitted.value = carSelected.value.toString();
}
</script>
```

## 只读表单

例如`<input type=file>`

value属性无法从Vue更改, 所以不能使用`v-model`

需要使用监听`change`事件来更新Vue数据

```vue
<script setup>
import {ref} from "vue";

const fileInput = ref({});

function updateVal(event) {
  fileInput.value = event.target.value;
}
</script>

<template>
  <input @change="updateVal" type="file">
  {{ fileInput}}
</template>
```

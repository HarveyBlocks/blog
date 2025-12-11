# 事件

## 事件对象和回调方法

### 事件对象

在事件的函数里引入`event`作为参数, 可以获取到一些基本信息

#### 指针位置

例如监听进入`div`的鼠标指针的位置

```vue
<script setup>

import {ref} from "vue";

const xPos = ref(0);
const yPos = ref(0);

function mousePos(event) {
  xPos.value = event.offsetX
  yPos.value = event.offsetY
}

</script>
<template>
  <div id="listen-mouse-pos" @mousemove="mousePos">
    <span>xPos: {{ xPos }}</span><br>
    <span>yPos: {{ yPos }}</span>
  </div>
</template>
```

测试发现, 边框和padding范围内会被监听, margin不会被监听

padding的**左上角**是(0,0), 左上的Border会是负值

#### 监听发起调用的标签

```vue
console.log(event.target.parentElement.id);
```

#### 监听输入内容

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

### 传递参数

```vue
<button @click="add(5)">+5</button>
```

### 传递参数和事件对象

```vue
<button v-on:click="add($event, 5)">+5</button>
```

## 事件修饰符

事件修饰符将事件类型进一步细分

事件修饰符**相互组合使用**

语法

```vue
v-on:[event-type].[event-修饰符]="function|object|line script"
```



### prevent阻止默认事件

使用prevent阻止右键出现菜单

```vue
<script setup>
function notAllow(msg) {
  alert(msg);
}
</script>
<template>
  <textarea v-on:contextmenu.prevent="notAllow('无法换出菜单')"></textarea>
</template>
```

类似的还有

-   `.stop`- 调用`event.stopPropagation()`
-   `.prevent`- 调用`event.preventDefault()`

### 键盘事件

-   `keydown` 用户按下按键时触发, 按下不放, 不断触发
-   `keypress` 用户敲击按钮时触发, 按下不放, 不会影响, 总共触发一次
-   `keyup` 用户释放按键时触发
-   详见HTMl参考手册

`event.key` 获取此次键入的值

```vue
<script setup>
getKey(event) {
    keyValue.value = event.key
}
</script>
```



| 按键修饰符               | 详细信息                                                     |
| :----------------------- | :----------------------------------------------------------- |
| 常见的键都有自己的别名   | `.enter` `.tab` `.delete` `.esc` `.space` `.up` `.down` `.left` `.right` |
| `.[letter]`例如 `.s`     | 指定按下该键时出现的字母                                     |
| `.[system modifier key]` | `.alt`、`.ctrl`、`.shift` 或 `.meta`。 这些键可以与其他键**结合使用**，或者与鼠标单击结合使用。 |



### 鼠标触发

在事件`click`后添加事件修饰符

-   `.left`- 只当点击鼠标左键时触发
-   `.right`- 只当点击鼠标右键时触发
-   `.middle`- 只当点击鼠标中键时触发

控制字体大小, 左键放大, 右键减小, 中键重置

```vue
<script setup>
import {ref} from "vue";

const DEFAULT_FONT_SIZE = 16;
const fontSize = ref(DEFAULT_FONT_SIZE);

function increment() {
  fontSize.value = fontSize.value + 1;
}

function decrement() {
  fontSize.value = fontSize.value - 1;
}

function reset() {
  fontSize.value = DEFAULT_FONT_SIZE;
}
</script>
<template>
  <button @click.left="increment"
          @click.right.prevent="decrement"
          @click.middle.prevent="reset">控制
  </button>
  <div :style="{fontSize: fontSize+'px'}">
    文本展示
  </div>
</template>
```

### 组合事件修饰符

ctrl+c时发出警告

```vue
<script setup>
function notAllow(msg) {
  alert(msg + ' is not allowed.');
}
</script>
<template>
  <textarea v-on:keydown.ctrl.c.prevent="notAllow('ctrl+c')"></textarea>
</template>
```

ctrl+左键进入链接

```vue
<script setup>
function notAllow(event) {
  let targetUrl = event.target.href;
  window.open('https://www.myweb.com/redirect?target='+targetUrl); // 先过自己的服务器, 警告用户
}
</script>
<template>
  <a href="https://www.baidu.com" @click.prevent.left.ctrl="notAllow">百度</a>
  <!--似乎有顺序上的区别, prevent在前, 表示阻止点击进入链接, 放在left后面就阻止不了-->
</template>
```

### `.once`

- `.once` 只触发一次回调。


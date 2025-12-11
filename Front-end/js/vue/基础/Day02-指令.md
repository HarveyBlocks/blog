# 指令

在InnerHtml中, 可以使用`{{}}`进行文本插值, 写简单的JS代码

但是属性的值不支持`{{}}`进行文本插值

于是, 在属性前加上指令

```vue
<script setup>
import {ref} from 'vue'

const vueClass = ref("pinkBG")

</script>

<template>
  <!--绑定属性, 属性值是JS代码-->
  <!--vueClass从简单的字符串变成了JS的变量-->
  <!--解析的时候, 就不会认为属性值是vueClass了, 而是会解析变量, 认为属性值是`pinkBG`-->
  <div v-bind:class="vueClass">文本</div>
</template>

<style>
.pinkBG {
  background-color: lightpink;
}
</style>
```

## v-bind

### 绑定样式

一个带有 CSS 属性和值 就可以以 JavaScript 对象的形式填写了

把值作为字符串变量, 可以进行运算等控制

```vue
<script setup>
import {ref} from 'vue'

let sizeNumber = 16;
const size = ref(sizeNumber + 'px');

function magnify() {
  size.value = sizeNumber++ + 'px';
}

function shrink() {
  size.value = sizeNumber-- + 'px';
}
</script>

<template>
  当前字体大小是: {{ size }}
  <button v-on:click="magnify">放大</button>
  <button v-on:click="shrink">缩小</button>
  <div v-bind:style="{ fontSize: size  }">文本</div>
</template>
```

也可以设计成把单位分开的样式, 更专注于值的变化

```vue
<script setup>
import {ref} from 'vue'

const size = ref(16);

</script>

<template>
  当前字体大小是: {{ size }} px
  <button v-on:click="size++">放大</button>
  <button v-on:click="size--">缩小</button>
  <!--样式可以作为值进行改变-->
  <div v-bind:style="{ fontSize: size +'px' }">文本</div>
</template>
```

可以把用引号+连字符的方式直接使用CSS语法, 但是不建议

```vue
<div v-bind:style="{ 'font-size': size +'px' }">文本</div>
```

### 绑定类

#### 绑定变量

`v-bind:class` 的值可以是一个变量

```vue
<div v-bind:class="className">
  The class is set with Vue
</div>
```

className就是一个变量

```vue
<script setup>
import {ref} from "vue";

const className = ref('myClass');
</script>

<template>
  <div v-bind:class="className">
    The class is set with Vue
  </div>
</template>

<style>
.myClass {
  color: #f0e180;
}
</style>
```

#### 绑定对象

```vue
<div v-bind:class="{ importantClass: isImportant }">
  The class is set conditionally to change the background color
</div>
```

-   isImportant bool 一个自定义变量, 被vue管理的追踪, 当值为true时, `v-bind:class` 采用值`'importantClass'`

#### 绑定数组

```vue
<div v-bind:class="[{ impClass: isImportant }, 'yelClass' ]"></div>
```

-   `yelClass`永远被绑定到上面, 相当于永远为true

### 简写

`v-bind`指令非常常用, 于是有一个简写形式`:`

```vue
<div :class="className">
  The class is set conditionally to change the background color
</div>
```

其中`:class`就等价于`v-bind:class`了

### 绑定属性和原属性

绑定不是在原属性上增加, 也不会覆盖原属性, 两者可以共存

```html
<div v-bind:class="className" class="importantClass">
  改变背景颜色
</div>
```

同时具有 `class=""` 和 `v-bind:class=""` 的 HTML 标签时，Vue 会**合并这些类**

包含一个或多个类的对象会被分配 `v-bind:class="{}"`。 在对象内部，可以打开或关闭一个或多个类。

但是这些类的有关样式有重复定义, 到底是取决于哪个类, 难以论说!(多继承是这样的...)

因此, 一个标签附带多个类的时候, 样式的属性不要有重复的

## 条件指令

-   `v-if`
-   `v-else-if` 必须在 `v-if` 或另一个 `v-else-if` 之后使用
-   `v-else`  必须在 `v-if` 或 `v-else-if` 之后使用
-   condition, bool 表达式, 返回值是bool即可(变量 or 比较运算 or 函数返回值)

```vue
<p v-if="condition1">A</p>
<p v-else-if="condition2">B</p>
<p v-else>C</p>
```

逻辑是和传统的分支结构一致的, 当分支被匹配的时候, 该标签被渲染, 否则标签不渲染

## v-show

和`v-if`一样用**Bool 表达式**决定标签是否渲染

-   `v-show` 浏览器能更容易做到, 响应更快
-   **v-if的显示是加载/删除; v-show是修改display样式**
-   `v-if` 能和`v-else-if` 或者`v-else` 一起使用, 更灵活

```vue
<p v-show="condition">A</p>
```

## 循环指令

>   `v-for` 指令

### 遍历数组

```vue
<script setup>
import {ref} from "vue";

const images = ref([
  {path: "A.png", name: "A", description: "a"},
  {path: "B.png", name: "B", description: "b"},
  {path: "C.png", name: "C", description: "c"},
  {path: "D.png", name: "D", description: "d"},
]);
</script>

<template>
  <div>
    <div v-for="image in images">
      文件名: {{ image.name }} <img :src="image.path" :alt="image.description">
    </div>
  </div>
</template>

```

![image-20250814210605833](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/vue/基础/Day02-指令/image-20250814210605833.png)

### 遍历对象

```vue
<script setup>
import {ref} from "vue";

const image = ref(
    {path: "D.png", name: "D", description: "d"}
);
</script>

<template>
  <div>
    <div v-for="msg in image">
      信息: {{ msg }}
    </div>
  </div>
</template>
```

![image-20250814211243697](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/vue/基础/Day02-指令/image-20250814211243697.png)

### 带索引的遍历

索引是广义的, key or index

```vue
<div v-for="(value,key) in image">
  {{key}}: {{ value }}
</div>
```

下面是key-value的遍历

```vue
<script setup>
import {ref} from "vue";

const image = ref(
    {path: "D.png", name: "D", description: "d"}
);
</script>

<template>
  <div>
    <div v-for="(value,key) in image">
      {{key}}: {{ value }}
    </div>
  </div>
</template>
```

![image-20250814211345220](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/vue/基础/Day02-指令/image-20250814211345220.png)

下面是index-element的遍历

```vue
<script setup>
import {ref} from "vue";

const images = ref([
  {path: "A.png", name: "A", description: "a"},
  {path: "B.png", name: "B", description: "b"},
  {path: "C.png", name: "C", description: "c"},
  {path: "D.png", name: "D", description: "d"},
]);
</script>

<template>
  <div>
    <div v-for="(image,index) in images">
      第{{index}}个文件: {{ image.name }} <img :src="image.path" :alt="image.description">
    </div>
  </div>
</template>
```

![image-20250814211655189](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/vue/基础/Day02-指令/image-20250814211655189.png)

### Generator的遍历

```vue
<script setup>

function* range(start, end, step) {
  for (let i = start; i < end; i += step) {
    yield i; // 不是 yield ref(i);? 
    // 不是ref(i)是ok的, 因为我们只关注值, 而不关注这个值在后续的变化
    // 也就是说, 我们不需要跟踪这个值的变化
    // 但是经过测试, 发现, 即使返回ref(i), 似乎也无法跟踪
  }
}
</script>

<template>
  <div>
    <div v-for="(image,index) in range(1,10,2)">
      第{{ index }}个值是: {{ image }}
    </div>
  </div>
</template>
```

![image-20250814212109448](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/vue/基础/Day02-指令/image-20250814212109448.png)

### `key` 属性

#### 存在问题

在v-for中当列表发生改变(主要是删除) 时, v-for为了节约资源, 会重用元素

例如在删除过程中, 删除数组的一个元素, 数组的后面的元素会往前挪动

这个过程vue不会销毁原来的元素, 然后把后面的元素深拷贝到前一个位置

而是简单的, 重新填入props

如果这个元素是一个组件, 而组件中含有data, 那么data的值是不会发生挪动的

这样, 被删除元素后面的props都往前填入了一个组件里, 这个组件内的data值是不会被刷新的

<video src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/vue/基础/Day02-指令/v-for存在问题演示.mp4" style="border: 2px solid"></video>

每次删除第二行的out的内容, 但实际上out删除的却不是第二行, 而是最后一行

实验代码如下:

-   Root.vue

    ```vue
    <script setup>

    import {ref} from "vue";
    import ChildNode from "@/components/ChildNode.vue";

    const range = ref(["A", "B", "C", "D", "E", "F"]);

    function remove(index) {
      range.value.splice(index,index);
    }
    </script>

    <template>
      <button @click="remove(1)">remove 1</button>
      <div >
        <ChildNode v-for="i in range" :value="i"/>
      </div>
    </template>
    ```

-   ChildNode.vue

    ```vue
    <script setup>
    import {ref} from "vue";

    let props = defineProps({
      value: {
        /**
         * @type  {import('vue').PropType<string>}
         */
        type: String,
        required: true,
      }
    });

    const data = ref(props.value);
    </script>

    <template>
      <div>
        out: {{value}}; in: {{ data }}
      </div>
    </template>

    ```

#### 解决

Root.vue

1.   给数组成员加上id

     ```js
     let id = 0;
     const range = ref([
       {msg: "A", id: id++},
       {msg: "B", id: id++},
       {msg: "C", id: id++},
       {msg: "D", id: id++},
       {msg: "E", id: id++},
       {msg: "F", id: id++}
     ]);
     ```

2.   给v-for一起使用key

     ```vue
     <ChildNode v-for="i in range" :value="i.msg" :key="i.id"/>
     ```

## v-on

>   告诉浏览器要监听什么**事件**，以及当该事件发生时要做什么

想在事件发生时运行更复杂的代码，可以将代码放在 Vue 方法中并从 HTML 属性引用该方法

```vue
<script setup>
import {ref} from "vue";

const contentColor = ref('0');

function changeColor(event) {
  let random = Math.random() * 65536;
  let number = Math.floor(random);
  contentColor.value = number.toString(16).toUpperCase();
}
</script>

<template>
  <div :style="{backgroundColor: '#'+contentColor}">
    当前颜色为: #{{ contentColor }}
    <button v-on:click="changeColor">点击随机变化</button>
  </div>
</template>
```

### 传统与Vue冲突

如果用传统的`onclock`呢?

```html
<button onclick="changeColor">点击随机变化</button>
```

会出现错误, 因为在vue中定义的方法都被vue框架代理了, 因此, 原生的onclick是找不到想要的`changeColor`方法的

### click

按下按钮开关灯

```vue
<script setup>
import {ref} from "vue";
const lightOn = ref(false);
</script>

<template>
  <div id="lightDiv">
    <div v-show="lightOn"></div>
    <img src="https://www.w3ccoo.com/vue/img_lightBulb.svg" alt="空心灯泡图片">
  </div>
  <button v-on:click="lightOn = !lightOn">灯的开关</button>
</template>
<style>
#lightDiv {
  position: relative;/*子元素有absolute, 父不应该是static*/
  width: 10%;
  aspect-ratio: 1; /* 宽高比1:1，*/
  border: dashed black 1px;
}

#lightDiv > img {
  width: 100%;
  height: 100%;
}
#lightDiv > div {
  z-index: -1;
  position: absolute;/*absolute脱离文档流, 使得div和img能够重合*/
  /*画圆*/
  width: 70%;
  aspect-ratio: 1;
  border-radius: 50%;
  /*移动位置*/
  top: 15%;
  left: 15%;
  /*着色*/
  background-color: yellow;
}
</style>
```

![image-20250814223239007](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/vue/基础/Day02-指令/image-20250814223239007.png)

### input

元素获得输入（例如在文本字段中击键）时执行操作

```vue
<script setup>
import {ref} from "vue";

const inputText = ref('');

function redInput(e) {
  inputText.value = e.target.value;
}
</script>

<template>
  <input type="text" v-on:input="redInput">
  <p style="margin-top: 100px">{{ inputText }}</p>
</template>
```

![image-20250814225210497](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/vue/基础/Day02-指令/image-20250814225210497.png)

### mousemove

鼠标指针移动到元素上时执行操作`

```vue
<div v-on:mousemove="lightOn = !lightOn">灯的开关</div>
```

### 简写

>   @

简写形式就是"`@`"

```vue
<div @:mousemove="lightOn = !lightOn">灯的开关</div>
<div @mousemove="lightOn = !lightOn">灯的开关</div>
```

## v-on和v-for联合使用

一个对象数组, 每一个element的对象作一个按钮, 然后点击这个按钮之后显示对应的图片

```vue
<script setup>
import {ref} from "vue";

const images = ref([
  {path: "https://www.w3ccoo.com/vue/img_burrito.svg", name: "A", description: "a"},
  {path: "https://www.w3ccoo.com/vue/img_salad.svg", name: "B", description: "b"},
  {path: "https://www.w3ccoo.com/vue/img_cake.svg", name: "C", description: "c"},
  {path: "https://www.w3ccoo.com/vue/img_Soup.svg", name: "D", description: "d"},
]);
const targetImagePath = ref('');
const targetImageDescription = ref('');

function showImage(image) {
  targetImagePath.value = image.path;
  targetImageDescription.value = image.description;
}
</script>

<template>
  <div>
    <div v-for="image in images" @click="showImage(image)">
      {{ image.name }}
    </div>
    <img v-show='targetImagePath.length!==0' :src="targetImagePath" :alt="targetImageDescription">
  </div>
</template>
<style>
#root > div {
  width: 20%;
  border: black solid 3px;
}

#root > div > div {
  width: 20%;
  margin: 5px;
  text-align: center;
  border: black solid 2px;
}

#root > div > div:hover {
  background: #bfbfbf;
}

div > img {
  width: 100%;
}
</style>
```


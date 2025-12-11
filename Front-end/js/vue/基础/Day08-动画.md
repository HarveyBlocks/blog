# 动画

>    `<transition>`

transition 可有以下属性的改变触发

-    `v-if` 
-    `v-show`
-    `<component>` 切换的动态组件
-   改变 `key` 属性

`<Transition>` **仅支持单个元素或组件**作为其内容。如果内容是一个组件，这个**组件必须仅有一个根元素**。

## 示例

```vue
<script setup>
import {ref} from "vue";

const show = ref(false);
</script>
<template>
  <button @click="show = !show">Toggle</button>
  <transition>
    <p v-show="show">hello</p>
  </transition>
</template>
<style scoped>
/*在加载进入(show转为true)时, 会动态产生 class="v-enter-from v-enter-to"*/
/*在加载离开(show转为false)时, 会动态产生 class="v-leave-from v-leave-to"*/
.v-enter-active, .v-leave-active {
  transition: opacity 0.5s ease;
}/*opacity t*/

.v-enter-from, .v-leave-to {
  opacity: 0;
}
</style>
```

## 转变流程和触发

1.  Vue 会自动检测目标元素是否应用了 CSS 过渡或动画, 并让一些CSS 过渡 class 会在适当的时机被添加和移除。
2.  如果有作为监听器的**JavaScript 钩子**，这些钩子函数会在适当时机被调用。
3.  如果没有探测到 CSS 过渡或动画、也没有提供 JavaScript 钩子，那么 DOM 的插入、删除操作将在浏览器的下一个动画帧后执行。

## 过渡 class

在不同阶段添加不同的class

class 添加之后, 可以被CSS触发

![过渡图示](../../../assetss/Day07-动画/transition-classes.DYG5-69l.png)

| class            | desciprtion                                | add timing                                                   | remove timing                |
| ---------------- | ------------------------------------------ | ------------------------------------------------------------ | ---------------------------- |
| `v-enter-from`   | 进入动画的起始状态                         | 元素插入之前                                                 | 在元素插入**完成后的下一帧** |
| `v-enter-active` | 进入动画的生效状态, 应用于整个进入动画阶段 | 元素被插入之前                                               | 过渡或动画完成之后           |
| `v-enter-to`     | 进入动画的结束状态                         | 元素插入**完成后的下一帧** <br>也就是 `v-enter-from` 被移除的同时 | 过渡或动画完成之后           |
| `v-leave-from`   | 离开动画的起始状态                         | 离开过渡效果被触发时立即添加                                 | **一帧**后被移除             |
| `v-leave-active` | 离开动画的生效状态, 应用于整个离开动画阶段 | 离开过渡效果被触发时立即添加                                 | 过渡或动画完成之后移除       |
| `v-leave-to`     | 离开动画的结束状态                         | 在一个离开动画被**触发后的下一帧**被添加<br/> 也就是 `v-leave-from` 被移除的同时 | 在过渡或动画完成之后移除     |



`v-enter-active` 和 `v-leave-active`  用于进入和离开动画==指定不同速度曲线==, 可以被用来定义进入动画的持续时间、延迟与速度曲线类型

## 命名过渡效果

使用`<transition>`的`name`属性

```vue
<transition name="fade">
  ...
</transition>
```

那么, 对应类型就会改变, 例如`v-leave-from` 变为`fade-leave-from`; `v-enter-active`变为`fade-enter-active`

## 自定义class名

在`<transition>`标签上指定属性, 来自定义class名

会覆盖相应阶段的默认 class 名

-   enter-from-class
-   enter-active-class
-   enter-to-class
-   leave-from-class
-   leave-active-class
-   leave-to-class

```html
template
<!-- 假设你已经在页面中引入了 Animate.css -->
<transition
  name="custom-classes"
  enter-active-class="my_define_class my_define_enter_class"
  leave-active-class="my_define_class my_define_leave_class"
>
  <p v-if="show">hello</p>
</transition>
```

## 事件

```html
<transition
  @before-enter="onBeforeEnter"
  @enter="onEnter"
  @after-enter="onAfterEnter"
  @enter-cancelled="onEnterCancelled"
  @before-leave="onBeforeLeave"
  @leave="onLeave"
  @after-leave="onAfterLeave"
  @leave-cancelled="onLeaveCancelled"
>
  <!-- ... -->
</transition>
```

##  属性

### 初始渲染

>   appear boolean

是否对初始渲染使用过渡

```vue
<transition appear>
  ...
</transition>
```

默认false



### 类名定义

-   name 用于自动生成CSS类目
-   enter-from-class
-   enter-active-class
-   enter-to-class
-   leave-from-class
-   leave-active-class
-   leave-to-class
-   appear-from-Class
-   appear-active-Class
-   appear-to-Class



### 模式

>   mode string

应用于互斥的元素之间进行的切换

-   'in-out'
-   'out-in' 
-   'default' 同时

```vue
<transition>
  <button v-if="docState === 'saved'"  @click="docState = 'edited'">Edit</button>
  <button v-else-if="docState === 'edited'" @click="docState = 'editing'">Save</button>
  <button v-else-if="docState === 'editing'" @click="docState = 'saved'">Cancel</button>
</transition>
```

按钮之间一个的交替, 要一个按钮out了, 另一个按钮才in, 这样才合适

```vue
<transition mode="out-in">
  <button v-if="docState === 'saved'"  @click="docState = 'edited'">Edit</button>
  <button v-else-if="docState === 'edited'" @click="docState = 'editing'">Save</button>
  <button v-else-if="docState === 'editing'" @click="docState = 'saved'">Cancel</button>
</transition>
```

### 关闭CSS过渡

当使用事件监听和**存JS脚本来管理动画**的时候使用

关闭用CSS演示动画可以提高一点性能, 也可以避免CSS动画和JS动画同时运行时造成的问题

```vue
<transition :css="false">
</transition>
```

### 同时使用 transition 和 animation

>   type

指定要等待的过渡事件类型,来确定过渡结束的时间。

默认情况下会自动检测, 持续时间较长的类型。

-   `transition`
-   `animation`



### 设置延迟

>   duration

默认情况下是等待过渡效果的根元素的第一个 `transitionend`或`animationend`事件

但如果第一个`transitionend`了, 还有`transition`未完成, 那么需要`duration`稍微延长一下

-    number 单位 ms
-   `{ enter: number; leave: number }` 对象

## TransitionGroup

>   `<transition-group>`

用于对 `v-for` 列表中的元素或组件的插入、移除和顺序改变添加动画效果。

-   列表中的每个元素都**必须**有一个独一无二的 `key` attribute
-   CSS 过渡 class 会被**应用在列表内的元素**上，**而不是**容器元素上

```vue
<ul>
  <TransitionGroup name="list">
    <li v-for="item in items" :key="item">
      {{ item }}
    </li>
  </TransitionGroup>
</ul>
```



### 属性

`<TransitionGroup>` 拥有与 `<Transition>` 除了 `mode` 以外所有的 props

`mode`不可用因为不再是在互斥的元素之间进行切换

-   tag

    ```vue
    <TransitionGroup tag="ul" name="list">
      <li v-for="item in items" :key="item">
        {{ item }}
      </li>
    </TransitionGroup>
    ```

    与上例一致

-   move-class

    自定义过渡中的元素的class名(类似于[transition的类名自定义](#类名定义))


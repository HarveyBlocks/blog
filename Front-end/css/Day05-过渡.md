# 过渡

>   transition

更改 CSS 属性时控制动画速度的方法

开始与结束这两个状态之间的过渡称为**隐式过渡**

目标

-   决定哪些属性发生动画效果
    -   通过*明确地列出这些属性*`transition-property`
-   决定开始时间
    -   通过设置延时`transition-delay`
-   决定持续时间
    -   设置时长`transition-duration`
-   如何动画
    -   定义*缓动函数*`transition-timing-function`，比如线性或先快后

## 动画性

为某些属性赋予动画无意义，称这些属性[*无动画性*](Day05-动画#动画性)

## 语法

| property                     | description                                                | detail                                                       |
| ---------------------------- | ---------------------------------------------------------- | ------------------------------------------------------------ |
| `transition-property`        | 指定哪个或哪些 CSS 属性用于过渡                            | 只有指定的属性才会在过渡中发生动画，其他属性仍如通常那样瞬间变化 |
| `transition-duration`        | 指定过渡的时长                                             | 可以为所有属性指定一个值，或者指定多个值，或者为每个属性指定不同的时长 |
| `transition-delay`           | 指定延迟                                                   | 属性开始变化时与过渡开始发生时之间的时长                     |
| `transition-timing-function` | 指定一个函数，定义属性值怎么变化。缓动函数定义属性如何计算 | 大多数缓动函数由四点定义一个立方贝塞尔曲线。也可以从 Easing Functions Cheat Sheet 选择缓动效果 |

简写语法

```css
div {
  transition: <property> <duration> <timing-function> <delay>;
}
```

## 一个属性过渡

对[变化文档](Day05-变化)上的三维属性变化的例子的*改变透视原点* 这一属性添加过渡

```css
.cube {
  width: 100%;
  height: 100%;
  backface-visibility: visible;
  transform-style: preserve-3d;
  transition: perspective-origin 1s;
}
```

<video src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/css/Day05-过渡/演示给透视原点属性增加过渡.mp4" style="border: 2px solid"></video>

调整透视深度, 没有过渡动画; 而调整透视原点会有过渡

## 多个属性过渡

对[变化文档](Day05-变化)上的三维属性变化的例子的*透视原点* 和*透视深度*两属性添加过渡

```css
.cube {
  width: 100%;
  height: 100%;
  backface-visibility: visible;
  transform-style: preserve-3d;
  transition: 
      perspective-origin 1s,
      perspective 2s;
}
```

<video src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/css/Day05-过渡/演示给多个属性增加过渡.mp4" style="border: 2px solid"></video>

## 属性值列表长度不一致

属性值列表更长

```css
div {
  transition-property: opacity, left, top, height;
  transition-duration: 3s, 5s;
}
```

那么有关过渡的其他配置将会重复以匹配长度

```css
div {
  transition-property: opacity, left, top, height;
  transition-duration: 3s, 5s, 3s, 5s;
}
```

如果属性列表不如其他配置长

```css
div {
  transition-property: opacity, left;
  transition-duration: 3s, 5s, 4s, 6s;
}
```

其他的配置将被阶段

```css
div {
  transition-property: opacity, left;
  transition-duration: 3s, 5s;
}
```

## JavaScript

使用transition, 来让JavaScript的变化更加流畅

将物体移动到鼠标点击处

### html

```html
<div id="follow"></div>
```

### Js脚本

```js
const followElement = document.getElementById("follow");
document.addEventListener(
  "click",
  (ev) => {
    followElement.style.transform = `translateY(${ev.clientY - 25}px)`+ 
        `translateX(${ev.clientX - 25}px)`;
  },
  false,
);
```

### CSS样式

```css
#follow {
  border-radius: 25px;
  width: 50px;
  height: 50px;
  background: #c00;
  position: absolute;
  top: 0;
  left: 0;
  transition: transform 1s;
}
```

## 监听渐变结束

-   transitionrun 在delay之前触发
-   transitionstart 在delay之后触发
-   transitionend 过渡结束时触发

参数 `TransitionEvent`对象, 有两个额外属性

-   `propertyName`
    -   string
    -   此过渡有关的 CSS 属性名
-   `elapsedTime`
    -   number 浮点数
    -   transition从开始运行到事件触发, 经过了多少秒
    -   此值不受 transition-delay 值的影响

```js
element.addEventListener("transitionrun", callback, true);
element.addEventListener("transitionstart ", callback, true);
element.addEventListener("transitionend ", callback, true);
```


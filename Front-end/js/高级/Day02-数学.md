# 数值

## 特殊数值

### NaN

```js
var x = 100 / "Apple";  // x 将是 NaN
```

```js
var x = 100 / "10";     // x 将是 10
```

`isNaN(number)` 来判断是否是NaN

在数学运算中使用了 NaN，则结果也将是 NaN

和字符串级联时, NaN 转为`"NaN"`

### Infinity

Infinity （或 -Infinity）是 JavaScript 在计算数时超出最大可能数范围时返回的值

除零产生Infinity

## 十六进制

0x 开头的解释为十六进制

前导0, 可能会解释为十进制, 可能会解释成八进制, 看浏览器

## 方法

-   toExponential(小数点精度) 返回科学表示法表示的数值字符串, 会进行四舍五入

-   toFixed(小数点精度)  

    ```js
    console.log((123).toFixed(6)); // 123.000000
    ```



## 数值属性

| 属性              | 描述                             |
| :---------------- | :------------------------------- |
| MAX_VALUE         | 返回 JavaScript 中可能的最大数。 |
| MIN_VALUE         | 返回 JavaScript 中可能的最小数。 |
| NEGATIVE_INFINITY | 表示负的无穷大（溢出返回）。     |
| NaN               | 表示非数字值（"Not-a-Number"）。 |
| POSITIVE_INFINITY | 表示无穷大（溢出返回）。         |

```js
console.log(Number.POSITIVE_INFINITY);
```



## 数学

| 方法 | 描述 |
| :--------- | :--------- |
| `abs(x)` | 返回 x 的绝对值 |
| `acos(x)` | 返回 x 的反余弦值，以弧度计 |
| `acosh(x)` | 返回 x 的双曲反余弦 |
| `asin(x)` | 返回 x 的反正弦值，以弧度计 |
| `asinh(x)` | 返回 x 的双曲反正弦 |
| `atan(x)` | 以介于 -PI/2 与 PI/2 弧度之间的数值来返回 x 的反正切值。 |
| `atan2(y, x)` | 返回从 x 轴到点 (x,y) 的角度 |
| `atanh(x)` | 返回 x 的双曲反正切 |
| `cbrt(x)` | 返回 x 的三次根 |
| `ceil(x)` | 对 x 进行上舍入 |
| `cos(x)` | 返回 x 的余弦 |
| `cosh(x)` | 返回 x 的双曲余弦 |
| `exp(x)` | 返回 Ex 的值 |
| `floor(x)` | 对 x 进行下舍入 |
| `log(x)` | 返回 x 的自然对数（底为e） |
| `max(x, y, z, ..., n)` | 返回最高值 |
| `min(x, y, z, ..., n)` | 返回最低值 |
| `pow(x, y)` | 返回 x 的 y 次幂 |
| `random()` | 返回 0 ~ 1 之间的随机数 |
| `round(x)` | 把 x 四舍五入为最接近的整数 |
| `sin(x)` | 返回 x（x 以角度计）的正弦 |
| `sinh(x)` | 返回 x 的双曲正弦 |
| `sqrt(x)` | 返回 x 的平方根 |
| `tan(x)` | 返回角的正切 |
| `tanh(x)` | 返回数字的双曲正切 |
| `trunc(x)` | 返回数字的整数部分 (x) |

# 表格

-   <abbr title="table raw">`<tr>`</abbr>
-   <abbr title="table data">`<td>`</abbr>
-   <abbr title="table header">`<th>`</abbr>
-   <abbr title="为整个表格定义标题">`<caption>`</abbr>
-   <abbr title="表格页脚,在表格底部定义摘要、统计信息等内容">`<tfoot>`</abbr>

## 表格示例

```html
<table>
  <thead>
    <tr>
      <th>列标题1</th>
      <th>列标题2</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>行1，列1</td>
      <td>行1，列2</td>
    </tr>
    <tr>
      <td>行2，列1</td>
      <td>行2，列2</td>
    </tr>
  </tbody>
</table>
```





<table>
  <thead>
    <tr>
      <th>列标题1</th>
      <th>列标题2</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>行1，列1</td>
      <td>行1，列2</td>
    </tr>
    <tr>
      <td>行2，列1</td>
      <td>行2，列2</td>
    </tr>
  </tbody>
</table>



## 边框

```html
<table border="2">
    <!--border 表格边框粗2像素, 非必须属性-->
  <thead>
    <tr>
      <th>列标题1</th>
      <th>列标题2</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>行1，列1</td>
      <td>行1，列2</td>
    </tr>
    <tr>
      <td>行2，列1</td>
      <td>行2，列2</td>
    </tr>
  </tbody>
</table>
```



<table border="2">
    <!--border 表格边框粗2像素, 非必须属性-->
  <thead>
    <tr>
      <th>列标题1</th>
      <th>列标题2</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>行1，列1</td>
      <td>行1，列2</td>
    </tr>
    <tr>
      <td>行2，列1</td>
      <td>行2，列2</td>
    </tr>
  </tbody>
</table>

## 标题和脚标

```html
<table>
  <caption>表格标题</caption> <!--可选-->
  <tfoot>表格脚注</tfoot> <!--可选-->
  <thead>
    <tr>
      <th>列标题1</th>
      <th>列标题2</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>行1，列1</td>
      <td>行1，列2</td>
    </tr>
    <tr>
      <td>行2，列1</td>
      <td>行2，列2</td>
    </tr>
  </tbody>
</table>
```



<table>
  <caption>表格标题</caption> <!--可选-->
  <tfoot>表格脚注</tfoot> <!--可选-->
  <thead>
    <tr>
      <th>列标题1</th>
      <th>列标题2</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>行1，列1</td>
      <td>行1，列2</td>
    </tr>
    <tr>
      <td>行2，列1</td>
      <td>行2，列2</td>
    </tr>
  </tbody>
</table>

## 垂直标题

```html
<table>
    <tr>
        <th>标题1</th>
        <td>内容1.1</td>
        <td>内容1.2</td>
    </tr>
    <tr>
        <th>标题2</th>
        <td>内容2.1</td>
        <td>内容2.2</td>
    </tr>
    <tr>
        <th>标题3</th>
        <td>内容3.1</td>
        <td>内容3.2</td>
    </tr>
</table>
```

<table>
    <tr>
        <th>标题1</th>
        <td>内容1.1</td>
        <td>内容1.2</td>
    </tr>
    <tr>
        <th>标题2</th>
        <td>内容2.1</td>
        <td>内容2.2</td>
    </tr>
    <tr>
        <th>标题3</th>
        <td>内容3.1</td>
        <td>内容3.2</td>
    </tr>
</table>



## 跨单元格

### 跨列

```html
<table>
  <thead>
    <tr>
      <th>列标题1</th>
      <th>列标题2</th>
      <th colspan="2">列标题3, 跨列</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>行1，列1</td>
      <td>行1，列2</td>
      <td>行1，列3.1</td>
      <td>行1，列3.2</td>
    </tr>
    <tr>
      <td>行2，列1</td>
      <td>行2，列2</td>
      <td>行2，列3.1</td>
      <td>行2，列3.2</td>
    </tr>
  </tbody>
</table>
```



<table>
  <thead>
    <tr>
      <th>列标题1</th>
      <th>列标题2</th>
      <th colspan="2">列标题3, 跨列</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>行1，列1</td>
      <td>行1，列2</td>
      <td>行1，列3.1</td>
      <td>行1，列3.2</td>
    </tr>
    <tr>
      <td>行2，列1</td>
      <td>行2，列2</td>
      <td>行2，列3.1</td>
      <td>行2，列3.2</td>
    </tr>
  </tbody>
</table>

### 跨行



```html
<table>
    <tr>
        <th>标题1</th>
        <td>内容1.1</td>
        <td>内容1.2</td>
    </tr>
    <tr>
        <th rowspan="4">标题2</th>
        <td>内容2.1</td>
        <td>内容2.2</td>
    </tr>
    <tr>
        <th rowspan="2">标题2.2</th>
        <td>内容2.2.1</td>
    </tr>
    <tr>
        <td>内容2.2.2</td>
    </tr>
    <tr>
        <td>内容2.3</td>
        <td>内容2.4</td>
    </tr>
    <tr>
        <th>标题3</th>
        <td>内容2.6</td>
        <td>内容2.7</td>
    </tr>
</table>
```

![image-20250730170536063](../assetss/Day03-表格/image-20250730170536063.png)

## 二维表

```html
<table border="1">
    <thead>
    <tr>
        <th>空</th>
        <th>列标题1</th>
        <th>列标题2</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <th>行标题2</th>
        <td>行1，列1</td>
        <td>行1，列2</td>
    </tr>
    <tr>
        <th>行标题3</th>
        <td>行2，列1</td>
        <td>行2，列2</td>
    </tr>
    </tbody>
</table>
```

<table border="1">
    <thead>
    <tr>
        <th>空</th>
        <th>列标题1</th>
        <th>列标题2</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <th>行标题2</th>
        <td>行1，列1</td>
        <td>行1，列2</td>
    </tr>
    <tr>
        <th>行标题3</th>
        <td>行2，列1</td>
        <td>行2，列2</td>
    </tr>
    </tbody>
</table>

## 复杂单元格

>   `<td>` 标签内可以嵌套复杂的语句

```html
<table border="4">
    <thead>
    <tr>
        <th>列标题1</th>
        <th>列标题2</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td>
            行1，列1<br>
            你可以嵌套任意文本<br>
            甚至一张图片:<br>
            <img src="..\assetss\Day01-基础\javascript.svg" alt="图片失效" >
        </td>
        <td>行1，列2的表格
            <table border="2">
                <thead>
                <tr>
                    <th>列标题1</th>
                    <th>列标题2</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>行1，列1</td>
                    <td>行1，列2</td>
                </tr>
                <tr>
                    <td>行2，列1</td>
                    <td>行2，列2</td>
                </tr>
                </tbody>
            </table>
        </td>
    </tr>
    <tr>
        <td>行2，列1的表格
            <table border="2">
                <thead>
                <tr>
                    <th>列标题1</th>
                    <th>列标题2</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>行1，列1</td>
                    <td>行1，列2</td>
                </tr>
                <tr>
                    <td>行2，列1</td>
                    <td>行2，列2</td>
                </tr>
                </tbody>
            </table>
        </td>
        <td>行2，列2的表格
            <table border="2">
                <thead>
                <tr>
                    <th>列标题1</th>
                    <th>列标题2</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>行1，列1</td>
                    <td>行1，列2</td>
                </tr>
                <tr>
                    <td>行2，列1</td>
                    <td>行2，列2</td>
                </tr>
                </tbody>
            </table>
        </td>
    </tr>
    </tbody>
</table>
```

<table border="4">
    <thead>
    <tr>
        <th>列标题1</th>
        <th>列标题2</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td>
            行1，列1<br>
            你可以嵌套任意文本<br>
            甚至一张图片:<br>
            <img src="..\assetss\Day01-基础\javascript.svg" alt="图片失效" >
        </td>
        <td>行1，列2的表格
            <table border="2">
                <thead>
                <tr>
                    <th>列标题1</th>
                    <th>列标题2</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>行1，列1</td>
                    <td>行1，列2</td>
                </tr>
                <tr>
                    <td>行2，列1</td>
                    <td>行2，列2</td>
                </tr>
                </tbody>
            </table>
        </td>
    </tr>
    <tr>
        <td>行2，列1的表格
            <table border="2">
                <thead>
                <tr>
                    <th>列标题1</th>
                    <th>列标题2</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>行1，列1</td>
                    <td>行1，列2</td>
                </tr>
                <tr>
                    <td>行2，列1</td>
                    <td>行2，列2</td>
                </tr>
                </tbody>
            </table>
        </td>
        <td>行2，列2的表格
            <table border="2">
                <thead>
                <tr>
                    <th>列标题1</th>
                    <th>列标题2</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>行1，列1</td>
                    <td>行1，列2</td>
                </tr>
                <tr>
                    <td>行2，列1</td>
                    <td>行2，列2</td>
                </tr>
                </tbody>
            </table>
        </td>
    </tr>
    </tbody>
</table>



## 单元格的边距和间距

-   cellpadding
    -   Cell Padding
    -   边距
    -   单元格内容和边框之间的空白
-   cellspacing
    -   Cell Spacing
    -   间距
    -   单元格之间的距离



```html
<table border="2" cellpadding="32" cellspacing="8">
    <thead>
    <tr>
        <th>列标题1</th>
        <th>列标题2</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td>行1，列1</td>
        <td>行1，列2</td>
    </tr>
    <tr>
        <td>行2，列1</td>
        <td>行2，列2</td>
    </tr>
    </tbody>
</table>
```

![image-20250730172133714](../assetss/Day03-表格/image-20250730172133714.png)


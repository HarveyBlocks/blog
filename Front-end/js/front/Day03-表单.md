# 表单

## 表单验证

```js
function onSubmit(event) {
  event.preventDefault(); // 阻止表单提交到服务器
  let name = document.forms['name-form']['name-input'].value;
  if (name === null || name === '') {
    alert('请输入');
  } else {
    document.getElementById('name-output').innerHTML = 'Hello, ' + name + ' !';
  }
}

function registerOnSubmit(formName, submitAction) {
  document.getElementById(formName).addEventListener('submit', submitAction);
}

registerOnSubmit('name-form', onSubmit);

```

```html
<form id="name-form" name="name-form" >
    <label for="name-input">Name: </label><input type="text" id="name-input">
    <input type="submit" value="提交">
</form>
<div id="name-output"></div>
```

也可以让submit函数返回flase来阻止表单提交到服务器

```js
function onSubmit() {
  let name = document.forms['name-form']['name-input'].value;
  if (name === null || name === '') {
    alert('请输入');
  } else {
    document.getElementById('name-output').innerHTML = 'Hello, ' + name + ' !';
  }
  return false;
}

```

```html
<form name="name-form" onsubmit="return onSubmit()"> <!--注意这里的要return-->
    <label for="name-input">Name: </label><input type="text" id="name-input">
    <input type="submit" value="提交">
</form>
<div id="name-output"></div>
```

## 自动检查

用HTML的一些`require`or`readonly`之类的

```html
<form name="name-form" onsubmit="return onSubmit()">
    <label for="name-input">Name: </label><input type="text" id="name-input" required>
    <input type="submit" value="提交">
</form>
```

这段检查的逻辑是发生在自定以`onSubmit`监听器之前的

```html
<label for="age-input">age: </label>
<input type="text" id="age-input" onkeyup="this.value=this.value.match(/^1?\d{0,2}/)">
```

单独表单上的检查也是在提交之前

上述代码表示年龄必须在0-199之间. 如果不是, 就无法填入表单

原理是, 从输入的文本中查找符合正则表达式的文本, 填入表单. 如果查询不到文本, 就强制把表单清空

| 属性     | 描述                      |
| :------- | :------------------------ |
| disabled | 规定应禁用 input 元素。   |
| max      | 规定 input 元素的最大值。 |
| min      | 规定 input 元素的最小值。 |
| pattern  | 规定 input 元素的值模式。 |
| required | 规定 input 字段必填。     |
| type     | 规定 input 元素的类型。   |

### 选择器伪类

| 选择器    | 描述                                      |
| :-------- | :---------------------------------------- |
| :disabled | 选择规定了 "disabled" 属性的 input 元素。 |
| :invalid  | 选择有无效值的 input 元素。               |
| :optional | 选择未规定 "required" 属性的 input 元素。 |
| :required | 选择规定了 "required" 属性的 input 元素。 |
| :valid    | 选择具有有效值的 input 元素。             |

## API

| 成员                | 描述                                       |
| :------------------ | :----------------------------------------- |
| checkValidity()     | 如果 input 元素包含有效数据，则返回 true。 |
| setCustomValidity() | 设置 input 元素的 validationMessage 属性。 |
| validationMessage   | 包含当有效性为 false 时的无效原因, 有效时值是空字符串  |
| validity          | 包含与输入元素有效性相关的布尔属性。        |
| willValidate      | 指示是否将验证 input 元素。                 |

```html
<label for="age-input">age: </label> <input type="number" id="age-input" min="100" max="300" required>
<input type="button" value="按钮" onclick="onClick()">
```

```js
function onClick() {
  let inpObj = document.getElementById('age-input');
  let msg;
  if (!inpObj.checkValidity()) {
    msg = inpObj.validationMessage;
  } else {
    msg = 'Input OK';
  }
  document.getElementById('name-output').innerHTML = msg;
}
```

![image-20250811001416431](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/front/Day03-表单/image-20250811001416431.png)

### validity属性

validity有以下bool属性, 用于检查是什么情况导致的检查不通过

| 属性            | 描述                                                  |
| :-------------- | :---------------------------------------------------- |
| customError     | 如果设置了自定义有效性消息，则设置为 true。           |
| patternMismatch | 如果元素的值与其 pattern 属性不匹配，则设置为 true。  |
| rangeOverflow   | 如果元素的值大于其 max 属性，则设置为 true。          |
| rangeUnderflow  | 如果元素的值小于其 min 属性，则设置为 true。          |
| stepMismatch    | 如果元素的值对其 step 属性无效，则设置为 true。       |
| tooLong         | 如果元素的值超过其 maxLength 属性，则设置为 true。    |
| typeMismatch    | 如果元素的值对其 type 属性无效，则设置为 true。       |
| valueMissing    | 如果元素（具有 required 属性）没有值，则设置为 true。 |
| valid           | 如果元素的值有效，则设置为 true。                     |

```js
if (document.getElementById("age-input").validity.rangeOverflow) {
    txt = "年龄太大bu'ke'ne";
}
```


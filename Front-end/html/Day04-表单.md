# 表单与输入

| 标签         | 描述                                                       |
| :----------- | :--------------------------------------------------------- |
| `<form>`     | 定义供用户输入的表单                                       |
| `<label>`    | 定义 `<input>` 元素的标签，一般为输入提示                  |
| `<input>`    | 定义输入域, 可以创建文本输入框、密码框、单选按钮、复选框等 |
| `<textarea>` | 定义文本域 (一个多行的输入控件)                            |
| `<fieldset>` | 定义一组相关的表单元素，并使用外框包含起来                 |
| `<legend>`   | 定义 `<fieldset> `元素的标题                               |
| `<select>`   | 定义下拉选项列表                                           |
| `<optgroup>` | 定义选项组                                                 |
| `<option>`   | 定义下拉列表中的选项                                       |
| `<button>`   | 定义一个点击按钮                                           |
| `<datalist>` | 指定一个预先定义的输入控件选项列表                         |
| `<keygen>`   | 定义了表单的密钥对生成器字段                               |
| `<output>`   | 定义一个计算结果                                           |

-   form 
    -   `action` 定义表单数据提交的目标 URL
    -   `method` 定义提交数据的 HTTP 方法

-   input
    -   `type` 定义输入框的类型
    -   `id` 关联 `<label>` 元素
    -   `name`  标识表单字段

```html
<form action="/" method="post">
    <!-- 文本输入框 -->
    <label for="name">用户名:</label>
    <input type="text" id="name" name="name" required>
</form>
```

## 输入`<input>`

### 文本

```html
<form>
    <label>
        First name:
        <input type="text" name="firstname">
    </label>
    <br>
    <label>
        Last name:
        <input type="text" name="lastname">
    </label>
</form>
```

### 密码

将不会显示文本

```html
<form>
    <label>
        Password:
        <input type="password" name="password">
    </label>
</form>
```

### 单选

```html
<form>
    <label>
        <input checked type="radio" name="sex" value="male">
        <!--checked表示选中该选项-->
        <!--效果就是, 默认选中了'男'-->
        男
    </label>
    <br>
    <label>
        <input type="radio" name="sex" value="female">
        女
    </label>
</form>
```

<form>
    <label>
        <input checked type="radio" name="sex" value="male">
        男
    </label>
    <br>
    <label>
        <input type="radio" name="sex" value="female">
        女
    </label>
</form>

### 复选框

```html
<form>
    <label>
        <input checked type="checkbox" name="value[]" value="1">
        自动安装插件
    </label>
    <label>
        <input checked  type="checkbox" name="value[]" value="2">
        创建桌面图标
    </label>
    <label>
        <input   type="checkbox" name="value[]" value="3">
        开机自启动
    </label>
</form>
```

<form>
    <label>
        <input checked type="checkbox" name="value[]" value="1">
        自动安装插件
    </label>
    <label>
        <input checked indeterminate type="checkbox" name="value[]" value="2">
        创建桌面图标
    </label>
    <label>
        <input   type="checkbox" name="value[]" value="3">
        开机自启动
    </label>
</form>
复选框有半选状态`indeterminate`

![image-20250827064908685](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/html/Day04-表单/image-20250827064908685.png)

```html
<input checked type="checkbox" indeterminate name="value[]" value="2">
创建桌面图标
```

### 提交按钮

单击确认按钮时，表单的内容会被传送到服务器

```html
<form name="input" action="html_form_action.php" method="get"><!--默认 get-->
    <label>
        Username:
        <input type="text" name="user">
    </label>
    <input type="submit" value="提交">
</form>
```

<form name="input" action="url/resource" method="post">
    <label>
        Username:
        <input type="text" name="user">
    </label>
    <input type="submit" value="提交">
</form>

form 的method 为get时, 会将**提交的参数**放在**URL的参数**上

## 下拉列表

```html
<form action="">
    <label>
        手机品牌是: 
        <select name="cars">
            <option value="s">三星</option>
            <option value="a">苹果</option>
            <option value="h" selected>华为<!--selected表示预选, 也就是默认--></option>
            <!--如果不预选, 那么第一个选项就是默认-->
            <option value="n">诺基亚</option>
        </select>
    </label>
</form>
```

<form action="">
    <label>
        手机品牌是: 
        <select name="cars">
            <option value="s">三星</option>
            <option value="a">苹果</option>
            <option value="h" selected>华为<!--selected表示预选, 也就是默认--></option>
            <!--如果不预选, 那么第一个选项就是默认-->
            <option value="n">诺基亚</option>
        </select>
    </label>
</form>

## Textarea

```html
<textarea rows="10" cols="30">
我是一个文本框。
</textarea>
```

<textarea rows="10" cols="30">
一个文本框
</textarea>


## 按钮

```html
<form action="">
    <input type="button" value="你好" onclick="alert('你好')">
    <button onclick="alert('你好啊')">别按</button>
</form>
```

<form action="">
    <input type="button" value="你好" onclick="alert('你好')">
    <button onclick="alert('你好啊')">别按</button>
</form>

## 表单域

```html
<form action="">
    <fieldset style="width: 400px">
        <legend>身份信息</legend>
        <label>
            姓名:
            <input type="text" size="30">
        </label><br>
        <label>
            邮件:
            <input type="text" size="30">
        </label><br>
        <label>
            生日:
            <input type="date" >
        </label>
    </fieldset>
</form>
```

<form action="">
    <fieldset style="width: 400px">
        <legend>身份信息</legend>
        <label>
            姓名:
            <input type="text" size="30">
        </label><br>
        <label>
            邮件:
            <input type="text" size="30">
        </label><br>
        <label>
            生日:
            <input type="date" >
        </label>
    </fieldset>
</form>


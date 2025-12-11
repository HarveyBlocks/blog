# 数据集合

## 数组

其实长度可变, 更类似于列表

### 创建

```js
var array_name = [item1, item2, ...];
```

或者

```js
var array_name = new Array(item1, item2, ...);
```

### 访问元素

```js
var item  = array_name[index];
array_name[index] = item;
```

### 数组对象的成员

-   数组长度

    ```js
    console.log(arr.length);
    ```

-   排序

    自定义比较法

    ```js
    let sorted_arr = arr.sort((a, b) => a - b);
    console.log(sorted_arr); // [4, 6, 12, 21, 21, 23, 45, 64]
    console.log(arr); // 写入原数组
    ```

    ```js
    let sorted_arr = arr.sort((a, b) => b - a);
    console.log(sorted_arr); // [64, 45, 23, 21, 21, 12, 6, 4]
    ```

    如果自定义比较法, 默认是将数字转为字符串后进行排序

-   reverse 反转数组(写入)

-   forEach方法遍历

    ```js
    arr.forEach((value, index, origin_arr) => {
      console.log(value);
      console.log(index);
      console.log(origin_arr);
    });
    arr.forEach(console.log);
    ```

-   push 添加列表元素到数组尾

    ```js
    arr.push(11);
    ```

    不建议下面的写法

    ```js
    arr[fruits.length] = 11;
    ```

-   pop 删除最后一个元素

-   shift 删除第一个元素, 其他元素前移

-   unshift 往数组最前面加入元素, 其他元素后移, 返回新数组的长度

-   delete关键字, 删除数组的某个元素

    ```js
    delete arr[2];
    ```

-   isArray 判断是否是Array

    ```js
    Array.isArray(arr);   // 返回 true
    ```

-   join 联合数组元素

    ```js
    arr.join("_")
    ```

-   splice 拼接数组

    ```js
    arr.splice(2,3,'a','b','c');
    ```

    -   2- cursor指向的索引
    -   3- 删除cursor后的3个元素
    -   'a','b','c'-往cursor后插入的元素
    -   会导致元素的前后移动

-   concat 连接数组, 不会改变原数组

    ```js
    var arr1 = ["A", "B"];
    var arr2 = ["a", "b", "c"];
    var arrAll = arr1.concat(arr2); 
    ```

    也可以与值合并

    ```js
    var arr = arrOrigin.concat("NEW ITEM"); 
    ```

-   slice 裁剪数组

    ```js
    let sliced = arr.slice(start);
    ```

    ```js
    let sliced = arr.slice(start,end);
    ```

    不包括end本身

-   indexOf依据元素值, 返回索引

    ```js
    array.indexOf(item, start)
    ```

    未找到项目，返回 -1

    ```js
    let arr = [12, 45, 21, 4, 6, 23, 64, 21];
    console.log(arr.indexOf(21)); // 2
    ```

    `start` 负值将从结尾开始的给定位置开始，并搜索到结尾

-   lastIndexOf 

    `start` 负值将从结尾开始的给定位置开始，并搜索到结尾

### 迭代

-   map , 过对每个数组元素执行函数来创建新数组, 不更改原数组

    ```js
    var numbers2 = numbers1.map((i)=>-i);
    ```

    参数有: 

    -   值
    -   索引
    -   数组本身

-   filter 同map

-   find 同 filter, 不过找到第一个符合的就会结束

-   reduce 将先前返回的值再作为参数返回给函数

    自定义函数可以接收的四个参数

    -   上一次的返回值
    -   数组元素
    -   索引
    -   数组本身

    可以自定义初始值

    ```js
    let number = arr.reduce((pre, item) => {
      return pre * item;
    }, 1);
    console.log(number); // 获得了数组所有元素的乘积
    ```

-   reduceRight 从右开始迭代

-   every 参数是**Predicate**, 最终every的返回值是每个元素对Predicate进行完**test**的**bool**结果再进行**`&`**

-   some  同every, 或

-   

## 对象

### 声明

```js
var|let|const object = {
    [property1: value1,
    property1: value1,..,
    func: function([param1,param2,...]){
    	// 函数体
	}]
}
```

### 调用

读

```js
let variable1 = object.p1;
let variable2 = object["p1"];
```

写

```js
object.p1 = value;
object["p1"] = value;
```

### 示例

```js
const simpleTest = function () {
  let object = {
    p1: 'a',
    p2: 12,
    method1: function (msg) {
      console.log('p1=' + this.p1 + ':' + msg);
    },
    method2: simpleTest,
  };
  object.p1 = 'b'; // 写
  console.log(object['p2']);// 读
  object.method1('内容'); 
  object.method2(); // 实质上的递归
};
```

### 删除

delete 关键词从对象中删除属性

```js
delete data.property;
```

### 对象访问器

就是Getter/Setter

```js
var person = {
  name: "John",
  language : "en",
  get lang() {
    return this.language;
  }
};
```

### Object.defineProperty()

定义属性

定义函数

```js
Object.defineProperty(obj, "reset", {
  get : function () {this.counter = 0;}
});
```

### 构造器

```js
function Person(first, last, age, eye) {  
  this.firstName = first;  
  this.lastName = last;  
  this.age = age;
  this.eyeColor = eye;
  this.name = function() {return this.firstName + " " + this.lastName;};
}
```

```js
var myFather = new Person("John", "Doe", 50, "blue");
var myMother = new Person("Sally", "Rally", 48, "green");
```

**无法**为已有的对象构造器**添加新属性**

所有的对象都继承自对象原型, `prototype`, 可以从原型添加属性

```js
Person.prototype.nationality = "English";
```

## 字符串

-   length
-   indexOf
-   lastIndexOf
-   search 搜索字串所在的位置
-   slice 值为负数(-12,-6), 裁剪从-12剪到-6
-   substring 无法接收负值
-   substr (开始, 长度), 缺省长度表示截取剩下的全部
-   replace(原始子字符串, 新字符串)
-   concat(连接符, 下一个字符串)
-   trim
-   charAt, 没有, 则返回空字符串
-   charCodeAt 返回unicode编码(数字)
-   split

### 正则表达式

>   **文本搜索**和**文本替换**

正则表达式不写引号

```js
/pattern/modifiers;
```

modifiers: 

| 修饰符 | 描述                                                     |
| :----- | :------------------------------------------------------- |
| i      | 执行对大小写不敏感的匹配。                               |
| g      | 执行全局匹配（查找所有匹配而非在找到第一个匹配后停止）。 |
| m      | 执行多行匹配。                                           |

-   String#match 返回数组

```js
let str = 'Is this all there is?';
let patt1 = /[a-z]*/gi;
let result = str.match(patt1).filter((s) => s.length > 0);
console.log(result); // ['Is', 'this', 'all', 'there', 'is']
```


# 生成器

## 声明

```js
function * generatorIdentifier(){
    // code
}
```

generator 函数返回一个`generator`

`generator`有api `next()`

`next()`函数返回`GeneratorResult`类型的对象

`GeneratorResult`有属性done 和 value

done 为 false 时 表示generator还有值没用放完

```js
for (let item of generator()){
    console.log(item);
}
```



## yield表达式

只能在generator内使用

在 yield 后跟一个表达式

```js
yield expression;
```

1.   调用generator, 运行函数内程序直到yield表达式
2.   表达式运行完毕后, 保留函数内的运行位置(程序计数器), 跳出函数
3.   运行函数外的内容
4.   再次调用generator时, 从yield表达式之后开始运行generator的内容

## 测试

```js
function* counter(start, step, end) {
  console.log("in");
  while (true) {
    console.log("in yield before");
    console.log('in yield expression:' + (yield start)); // undefined
    console.log("in yield after");
    start += step;
    if (start > end) {
      console.log('in end!');
      break;
    }
  }
}

function test() {
  let generator = counter(0, 1, 100);
  while (true) {
    let next = generator.next();
    console.log("out done: " + next.done);
    if (!next.done) {
      // 还未结束
      console.log("out value: " + next.value);
    } else {
      console.log('out end!' + next.value);
      break;
    }
  }
}
```


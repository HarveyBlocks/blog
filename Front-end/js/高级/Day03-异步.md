# 异步

## 回调

把函数作为参数传入另一个函数, 由另一个函数决定是否调用这个函数

## 异步

### 等待超时

>   setTimeout(func,millionsecond);
>
>   clearTimeout(id)

```js
function task(name) {
  console.log('task:' + name);
}

function simpleTest() {
  setTimeout(task, 200, 'A');
  console.log('task: outer');
}
```

![image-20250809142712186](../../assets/Day03-异步/image-20250809142712186.png)

```js
setTimeout(task, 0, 'A');
```

能够开始一个异步操作, 并立即执行task

如果task非常耗时, 就可以同时执行setTimeout下面的代码

`clearTimeout`暂停等待

```js
let timeoutId = setTimeout((msg) => console.log(msg), 200, 'A');
console.log('task: outer');
clearTimeout(timeoutId);
```

### 等待间隔

>   setInterval(func,millionsecond);

```js
function task(name) {
  console.log('task:' + name);
}

function simpleTest() {
  setInterval(task, 200, 'A');
  setInterval(task, 300, 'B');
}
```

setInterval 函数返回一个id, 可以用`clearInterval`暂停这个id对应的循环

```js
let id = setInterval(action, 5);
clearInterval(id);
```

###onreadystatechange  等待文件

>   on ready state change

```js
function afterGetFile(text) {
  console.log(text);
}

function hasGotFile(request) {
  return request.readyState === 4 && request.status === 200;
}

function syncRequestFile(file, afterGet) {
  let request = new XMLHttpRequest();
  request.onreadystatechange = function () {
    if (hasGotFile(this)) {
      afterGet(this.responseText);
    }
  };
  request.open('GET', file, true);
  request.send();
}

function simpleTest() {
  syncRequestFile('index2.html', afterGetFile);
}
```

### Promises

| myPromise.state | myPromise.result |
| :-------------- | :--------------- |
| "pending"       | undefined        |
| "fulfilled"     | a result value   |
| "rejected"      | an error oject   |



```js
function executor(onSuccessful, onError) {
  sleep(1000 * 3);
  log('executor complete');
  let flag = Math.random() <= 0.5;
  if (flag) {
    onSuccessful('OK');
  } else {
    onError('ERROR');
  }
}

function showResource(resource) {
  sleep(3000);
  log('OK');
}

function alertError(error) {
  sleep(3000);
  log('ERROR');
}

function simpleTest() {
  log('start');
  // 实例化, 同时阻塞执行executor
  let promise = new Promise(executor);
  log('instance complete');
  // 异步执行then
  let promise1 = promise.then(showResource, alertError);
  log('after');
}
```

-   log 和 sleep 是自己写的工具类
-   log 有美观的输出
-   sleep用轮询的原理, 阻塞一定时间

==then有返回值, 但不知道是作甚的==

如果`executor`中永远不调用`resolve ` 或者`reject`, 那么 then 中的两个函数也永远不会被调用(废话....)

但其实, 在`executor`中有意不调用`resolve`, 而是将`resolve`作为变量拉到外边的作用域, 然后再执行`then`方法, 最后才执行`resolve`, then中的方法就会被调用了

```js
class Thread {
  constructor(runnable) {
    this.runnable = runnable;
    // 保留字段
    let resolve, reject;
    this.promise = new Promise((res, rej) => {
      resolve = res;
      reject = rej;
    });
    this.resolve = resolve;
    this.reject = reject;
    // 开启then
    this.promise.then(this.runnable);
  }

  start() {
    this.resolve(); // 开启调用
  }
}

async function simpleTest() {
  log('start');
  let thread = new Thread(async () => {
    log('start run');
    await sleep(1000);
    log('end run');
  });
  log('created thread');
  await sleep(1000);
  log('slept');
  thread.start();
  log('end');
}

simpleTest();
```

![image-20250809222622206](../../assets/Day03-异步/image-20250809222622206.png)

但这样是不好的, 不太符合JavaScript的设计初衷



## Async

关键字await使函数阻塞等待promise的executor中的resolve被调用(或者rejected被调用, 总之就是出了某个结果之后就停止阻塞)

await 后 如果不是promise, 将不阻塞, 直接进行await下面的代码

```js
async function simpleTest() {
  let promise = new Promise(executor);
  let result = await promise;
  log(result);
}

simpleTest();
```

自定义sleep函数

```js
const sleep = (million) =>
  new Promise((resolve) => setTimeout(() => resolve(), million));


async function simpleTest() {
  log('start');
  await sleep(1000); // 使用方法
  log('end');
}
```

1.   setTimeout函数马上就会被执行完毕, 所以Pormise的实例化很快就会结束
2.   经过million之后, 才会调用`resolve`
3.   调用了resolve , 这个await才会结束


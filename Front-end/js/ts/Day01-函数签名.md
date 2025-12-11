# 函数签名

可以在参数和返回值上定义类型

```ts
function add(x: number, y: number): number {
    return x + y;
}

let myAdd: (a: number, b: number) => number = function(x: number, y: number): number { 
    return x + y; 
};
```

不在乎参数名是否一致

## 可选参数

使用`?:`表示参数可选

```ts
function buildName(firstName: string, lastName?: string) {
    if (lastName)
        return firstName + " " + lastName;
    else
        return firstName;
}
```

可选参数必须在必选参数后面, 自动赋值为undefined

## 默认参数

可以给参数提供默认值, 同时表示可选, 此时, 即使不标注, 参数的类型被限定为默认值的类型

```ts
function buildName(firstName: string, lastName = "Smith") {
    // ...
}
```

也可以标注

```ts
function buildName(firstName: string, lastName:any = "Smith") {
    // ...
}
```

默认参数可以放在必选参数前面, 此时传入`undefined`时采用默认值

```ts
function buildName(firstName: string = "Mike", lastName:string): void {
    // ...
}

buildName(undefined, "Smith");
```

## 不定参数

不定参数的类型标注使用数组

```ts
function buildName(firstName: string, ...restOfName: string[]) {
  return firstName + " " + restOfName.join(" ");
}
```

在函数签名的类型标注上也可以使用`...`+数组类型的方式表示不定参数

```ts
let buildNameFun: (fname: string, ...rest: string[]) => string = buildName;
```

## this参数

为了防止this指示不明, 在参数列表的最前面加一个`this:Type`来指明类型, 编译后这个参数被去除

```ts
class A {
    name: string;

    constructor(name: string) {
        this.name = name;
    }

    crateFunc = function (this: A) {// 仅仅用于指示
        return () => { // 使用箭头函数, 将依据声明处的上下文来判断this
            // 如果不是箭头函数, this将报错
            console.log(this.name);
        }
    }
}

let a: A = new A("X");
a.crateFunc()(); // X
```

如果使用了this指示, 在调用函数的时候也会提示使用是否正确

```ts
class Handler {
    method1(msg: string) {
    }

    method2(this: Handler, msg: string) {
    }
}

let h = new Handler();
let method1 = h.method1;
let method2 = h.method2;
method1("");
method2(""); // 报错
```

使用`this:void`, 表明允许一切this

```ts
class Handler {
    method1(msg: string) {
    }

    method2(this: void, msg: string) {
    }
}

let h = new Handler();
let method1 = h.method1;
let method2 = h.method2;
method1("");
method2(""); // 报错
```

如果既要this指向本类型, 又要能够作为回调函数调用, 则应该使用原生的箭头函数解决

## 重载

Javascript是动态语言, 只能用类型判断来进行不同的逻辑了

```ts
function func(x): any {
    if (typeof x == "object") {
    }else if (typeof x == "number") {
    }
}
```

但是使用类似声明的语法, 来方便函数的调用者进行检查(每个函数签名的对应)

```ts
function pickCard(a: string): number;
function pickCard(b: number): string;
function pickCard(x: any): any {
    if (typeof x == "string") {
        return x.length;
    } else if (typeof x == "number") {
        return x + "";
    }
}

console.log(pickCard(15));
console.log(pickCard("15"));
// console.log(pickCard(true)); ERROR
```

重载的匹配是从上到下的, 因此, 函数匹配范围最小的(最精确详细的), 应该放在最前面

## 迭代器


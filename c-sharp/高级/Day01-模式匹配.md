# 模式匹配

用于将常用的判别表达式写的更简洁

## 模式匹配表达式

### is表达式

语法

```csharp
待审查元素 is 模式
```

`is` 运算符检查表达式的结果是否与给定的类型相匹配

```csharp
E is T
```

-   `E`  返回一个值的表达式
-   `T`  类型或类型参数的名称
-   `E` 不得为匿名方法或 Lambda 表达式

`is` 运算符不会考虑用户定义的转换

表达式结果为非 null , 且在以下情况中, `is`表达式返回true

-   `E`的运行时类型为 `T`

-   `E`的运行时类型派生自类型 `T`、实现接口 `T`

    ```csharp
    if (a is Student sa && b is Student sb) {
        Console.WriteLine($"{sa.Name} is playing with {sb.Name}");
    } else if (a is Person ap && b is Person bp) {
        Console.WriteLine($"{ap.Name} is talking with {bp.Name}");
    } else if (a is not null && b is not null) {
        // not否定匹配
        Console.WriteLine("Unknown Type");
    } else if (a is null) {
        Console.WriteLine("a is null");
    } else {
        Console.WriteLine("a is null");
    }
    ```

    变量 sa/sb等 仅在 if 子句的 true 部分可供访问和分配

    ```cs
    Show(new Student("a"), new Student("b")); // a is playing with b
    Show(new Student("a"), new Person("b")); // a is talking with b
    Show(new Person("a"), new Student("b")); // a is talking with b
    Show(new Person("a"), new Person("b")); // a is talking with b
    Show(new Student("a"), null); // b is null
    Show(null, new Student("b")); // a is null
    Show(null, null); // a is null
    Show(new object(), new object()); // Unknown Type
    ```

-   `E` 的运行时类型存在从其到 `T` 的其他的*隐式引用转换*

-   `E`的运行时类型是基础类型为 `T` 且 *`Nullable.HasValue`*为 `true` 的*可为空值类型*

    ```csharp
    int? num = 2; // Nullable类型
    Console.WriteLine(num is int);
    // 等价于
    Console.WriteLine(num.HasValue ? (num.Value is int) : false);
    ```

    可以看Nullable`<T>`类型的源码

-   存在从表达式结果的运行时类型到类型 `T` 的 *装箱* 或 *取消装箱* 的转换

    ```csharp
    int num = 2;
    Console.WriteLine(num is int); // True
    Console.WriteLine(num is long); // False
    Console.WriteLine(num is System.Int32); // True
    object obj = num;
    Console.WriteLine(obj is int); // True
    Console.WriteLine(obj is long); // False
    Console.WriteLine(obj is System.Int32); // True
    ```

### and/or/not

```csharp
if (a is Teacher or Student /*a1 不能声明*/) {
    Console.WriteLine($"{{{a}}} is a student or teacher");
}

if (a is Person and not Teacher) {
    Console.WriteLine($"{{{a}}} is a person who is not a teacher");
}

if (a is Person and ICloneable variable) {
    Console.WriteLine($"{{{variable}}} is a cloneable person");
    Console.WriteLine(variable.Clone());
}
```

```csharp
Show(new Person("A"));
// {Person {Name: A}} is a person who is not a teacher
Show(new Student("B"));
// {Student : {Person {Name: B}}} is a student or teacher
// {Student : {Person {Name: B}}} is a person who is not a teacher
Show(new Teacher("C"));
// {Person {Name: C}} is a student or teacher
// {Person {Name: C}} is a cloneable person
```

优先级上, `not`>`and`>`or`, 也可以加上括号

```csharp
if (a is Person and not (Stident or  Teacher)) {
    Console.WriteLine($"{{{a}}} is a person who is not a student or a teacher");
}
```

### switch表达式

#### 基本元素

-    `switch` 关键字之前的表达式, 被检查, 待匹配的数据
-   `switch` 分支表达式
    -   用逗号分隔
    -   每个 `switch` 分支表达式 都包含一个**模式**
    -   可选的 case guard
    -   可选`=>` 标记+一个表达式 

```csharp
结果类型 结果元素 = [(]待审查参数1[,待审查参数2,待审查参数3...)]switch{
    [(]待审查参数1匹配模式1[,待审查参数2匹配模式1,待审查参数3匹配模式1...)] => 匹配成功后执行逻辑表达式(可有返回)
      [,[(]待审查参数1匹配模式2[,待审查参数2匹配模式2,待审查参数3匹配模式2...)] => 匹配成功后执行逻辑表达式(可有返回),[(]待审查参数1匹配模式2[,待审查参数2匹配模式2,待审查参数3匹配模式2...)] => 匹配成功后执行逻辑表达式(可有返回),[(]待审查参数1匹配模式2[,待审查参数2匹配模式2,待审查参数3匹配模式2...)] => 匹配成功后执行逻辑表达式(可有返回)...]

};
```

#### 使用实例

```csharp
object result = (a, b)switch{
    (> 0, < 0) => -1,
    (< 0, > 0) => -1,
    (not 0, 0) => a,
    (0, not 0) => b,
    (< 0, < 0)when a > b => a * b,
    (> 0, > 0) when a < b => new Student("A")
    //只要可以转换, 就可以返回不同的数据类型, 否则不想
};
```

#### 非完备的switch表达式

如果 `switch` 表达式的模式均未捕获输入值，则运行时将引发异常

```csharp
int a = 2;
int b = 2;
object result = (a, b)switch{
    (> 0, < 0) => -1,
    (< 0, > 0) => -1,
    (not 0, 0) => a,
    (0, not 0) => b,
    (< 0, < 0)when a > b => a * b,
    // (> 0, > 0) when a < b => new Student("A")
    // 只要可以转换, 就可以返回不同的数据类型, 否则不想
};
```

```log
未经处理的异常:  System.InvalidOperationException: 对象的当前状态使该操作无效。
   在 <PrivateImplementationDetails>.ThrowInvalidOperationException()
   在 ConsoleApplication1.FlagsEnumExample.Main() 位置 D:\IT_study\source\csharp\ConsoleApplication1\ConsoleApplication1\Program.cs:行号 71

```

## 模式

-   类型模式
-   声明模式: 类型模式+声明匹配后的新变量
-   逻辑模式: 使用了`not`, `and`,`or`
-   弃元模式: 使用了`_`放弃一切审查, 直接完成匹配
-   常量模式: 匹配常量表达式的结果值
-   关系模式: 匹配由常量表达式组成的布尔表达式的
-   属性模式: 对参数的属性进行模式匹配
    -   拓展属性模式: 对参数的递归属性(属性的属性等)直接进行模式匹配,  C# 10开始支持
-   位置模式: 利用析构元组的隐式转换, 将参数转变为元组之后再对元组的元素进行逐一的审查
-   var 模式: 匹配所有参数后转为新声明的变量
-   列表模式: 在 C# 11 中引入

### 声明和类型模式

-   检查表达式的运行时类型是否与给定类型兼容
-   可声明新的局部变量

switch

```csharp
private static void ShowMessage(object a) {
    object msg = a switch{
        null => null,
        Array array => $"array{array}, len: {array.Length}",
        List<int> list => $"list{list}, capacity: {list.Capacity}",
        not null => "unknown"
    };
    Console.WriteLine(msg);
}
```

is表达式

```csharp
private static void ShowMessage(object a) {
    object msg;
    if (a is null) {
        msg = null;
    } else if (a is Array array) {
        msg = $"array{array}, len: {array.Length}";
    } else if (a is List<int> list) {
        msg = $"list{list}, capacity: {list.Capacity}";
    } else if (a is not null) {
        msg = "unknown";
    } else {
        // 此分支根本没必要, 但为了演示, 避开编译器检查
        msg = null;
    }
    Console.WriteLine(msg);
}
```

### 逻辑模式

使用了and not or

### 弃元模式

`_` 来匹配任何表达式，包括 `null`

放弃对某个参数的审查, 直接成功匹配进入分支

```csharp
string msg = a switch{
    null => null,
    Array array => $"array{array}, len: {array.Length}",
    List<int> list => $"list{list}, capacity: {list.Capacity}",
    _ => "unknown"
};
```

弃元模式只能用在switch表达式而不能在is语句或switch语句中使用

要弃用表达式的返回值, 应当使用`var _`如

```csharp
_ = a switch{
    null => null,
    Array array => $"array{array}, len: {array.Length}",
    List<int> list => $"list{list}, capacity: {list.Capacity}",
    _ => "unknown"
};
```

### 常量模式

常量可以是

-   数值文本
-   字符
-   字符串字面量。
-   `true` 或 `false`
-   enum 值
-   声明常量字段或本地的名称
-   `null`
-   常量表达式
-   类型为 `Span<char>` 或 `ReadOnlySpan<char>` 的表达式可以在 C# 11 及更高版本中针对常量字符串进行匹配

```csharp
string msg = a switch{
    1 => "1",
    2 => "2",
    3 => "3",
    null => null,
    _ => "unknown"
};
```

### 关系模式

进行布尔表达式比较

构成布尔表达式的, 应是常量

```csharp
string msg  = a switch{
    int num => num switch{
        > 3 => ">3", // 关系模式
        > 2 => ">2", // 关系模式
        > 1 => ">1", // 关系模式
        > 0 or 0 => "not negative", // 逻辑模式
        _ => "negative" // 弃元模式
    }, // 声明模式
    null => null, // 常量模式 
    _ => "unknown type" // 弃元模式 
};
```

### 属性模式

将待审查参数的属性或字段与嵌套模式进行匹配

```csharp
待审查参数 is {待审查参数的字段1: 模式1, 待审查参数的字段2: 模式2, ....}
```

```csharp
DateTime dt = DateTime.Now;
bool isNationalDay = dt is{ Month: 10, Day: 1 };
```

```csharp
string msg = a switch{
    int num => num switch{
        > 3 => ">3",
        > 2 => ">2",
        > 1 => ">1",
        > 0 or 0 => "not negative",
        _ => "negative"
    },
    DateTime dt => dt switch{
        {Day: 1, Month: 10} => "National Day",
        {Day: 1, Month: 1} => "New Years Day",
        _ => "Normal Day"
    },
    null => null,
    _ => "unknown type"
};
```

属性模式是一种递归模式

```csharp
public struct Point {
    public int X { get; }
    public int Y { get; }

    public Point(int x, int y) {
        X = x;
        Y = y;
    }
}

public struct Triangle {
    public Point A { get; }
    public Point B { get; }
    public Point C { get; }

    public Triangle(Point a, Point b, Point c) {
        A = a;
        B = b;
        C = c;
    }
}

public static bool IsTriangle(Triangle triangle) {
    return triangle is { A:{ X: 0, Y: 0 } } or{ B:{ X: 1, Y: 1 } };
}
```

#### 扩展属性模式

```csharp
return triangle is { B.X:1, B.Y:1};
```

### 位置模式

[析构元组#隐式转换](Day02-析构元组#隐式转换)

```csharp
private class Point {
    public int X { get; set; }
    public int Y { get; set; }

    public Point(int x = 0, int y = 0) {
        X = x;
        Y = y;
    }

    public void Deconstruct(out int x, out int y) {
        x = X;
        y = Y;
    }
}
```

```csharp
var msg = point switch{
    (0, 0) => "原点",
    (not 0, 0) => "在x轴上",
    (0, not 0) => "在y轴上",
    _ => "普通的点",
};
Console.WriteLine(msg);
```

### var模式

声明模式, 只不过匹配所有的参数, 然后将其转为新变量

```csharp
bool hasOneNumber = GetRandomArray(seed) is var results && results.Length == 1;
```

1.  生成了一个随机数数组
2.  将数据赋值给了新声明的变量`results`
3.  判断随机数数组`results`的长度是1

一行解决, 还能控制变量的作用域, 真是方便(★ ω ★)

### 列表模式

从 C# 11 开始，可以将数组或列表与模式的序列进行匹配

```csharp
Console.WriteLine(numbers is [0 or 1, <= 2, >= 3,_,var last] && last!=0);  // True
```


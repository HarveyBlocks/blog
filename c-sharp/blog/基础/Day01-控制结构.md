# 控制结构


## 顺序

## 分支

### switch

#### `switch` 语句

```csharp
void DisplayMeasurement(double measurement) {
    switch (measurement) {
        case < 0.0:
            Console.WriteLine($"Measured value is {measurement}; too low.");
            break;

        case > 15.0:
            Console.WriteLine($"Measured value is {measurement}; too high.");
            break;

        case double.NaN:
            Console.WriteLine("Failed measurement.");
            break;

        default:
            Console.WriteLine($"Measured value is {measurement}.");
            break;
    }
}
```

-   [关系模式](https://learn.microsoft.com/zh-cn/dotnet/csharp/language-reference/operators/patterns#relational-patterns)：用于将表达式结果与常量进行比较
    -   [integer](https://learn.microsoft.com/zh-cn/dotnet/csharp/language-reference/builtin-types/integral-numeric-types) 或 [floating-point](https://learn.microsoft.com/zh-cn/dotnet/csharp/language-reference/builtin-types/floating-point-numeric-types) 数值文本
    -   字符
    -   字符串常量
    -   布尔值 `true` 或 `false`
    -   enum
    -   声明[常量](https://learn.microsoft.com/zh-cn/dotnet/csharp/language-reference/keywords/const)字段或本地的名称
    -   `null`
    -    `Span<char>` 或 `ReadOnlySpan<char>` 的表达式可以在 C# 11 及更高版本中针对常量字符串进行匹配。
-   [常量模式](https://learn.microsoft.com/zh-cn/dotnet/csharp/language-reference/operators/patterns#constant-pattern)：测试表达式结果是否等于常量
    -   使用关系运算符+常量表达式
    -   and模式匹配(见下)

### Case guard

必须与匹配模式同时满足

case guard 必须是布尔表达式

```csharp
switch ((a, b)) {
    case (> 0, > 0) when a > b:
        Console.WriteLine("进行case匹配, 完成when的比较");
        break;
    default:
        Console.WriteLine("其他");
        break;
}
```

验证case和when的顺序

```csharp
switch (a, b) {
    case (> 0, > 0) when WhenFunc(a, b):
        Console.WriteLine("完成when后比较后, 进行case匹配");
        break;
    default:
        Console.WriteLine("其他");
        break;
}
```



```csharp
private static bool WhenFunc(int a, int b) {
    Console.WriteLine("WhenFunc");
    return a >= b;
}
```

只有在a,b都大于0时才会打印语句, 说明case guard 的执行逻辑在case之后

### 模式匹配

[模式](../高级/Day01-模式匹配)

## 循环

### foreach

对于迭代器可用的foreach

```C#
List<int> list = new List<int>();
list.Add(1);
list.Add(2);
list.Add(3);
foreach (var i in list) {
    Console.WriteLine(i);
}
```


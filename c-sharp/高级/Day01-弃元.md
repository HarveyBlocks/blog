# 弃元

可以忽略:

-   表达式的结果
-   元组表达式的一个或多个成员
-   方法的 `out` 参数
-   模式匹配表达式的目标

## 独立弃元

```csharp
num is int;
```

编译异常

```csharp
_ = num is int;
```

正常

## 模式匹配中的弃元

[模式匹配中的弃元](Day01-模式匹配#弃元模式)

## 对方法返回元组的弃元

```csharp
public (int, int, double) ToTuple() {
    return (X, Y, Math.Sqrt(X * X + Y * Y));
}
public static void Show(Point point) {
    int a, b;
    (a, b, _) = point.ToTuple(); // 发生隐式转换
    Console.WriteLine($"x is {a},y is {b}");
}
```
## 对方法out参数的弃元

```csharp
public void ToTuple(out int x, out int y, out double z) {
    x = X;
    y = Y;
    z = Math.Sqrt(X * X + Y * Y);
}
public void ToTuple(out int x, out int y) {
    ToTuple(out int x2, out int y2, out _);
    x = x2;
    y = y2;
}
```
## 标识符`_`

`_` 是有效标识符, 在上下文的分析中, 在`_`被视为变量的情况下, 弃元不被启用

```csharp
int _ = -1;
GetNumber(out int num1, out _);
Console.WriteLine($"{num1}, {_}"); // 1,1
```

被警告, 改为:

```csharp
int _ = -1;
GetNumber(out int num1, out var _);
Console.WriteLine($"{num1}, {_}"); // 1,1
```

为佳


# 数据类型

## 值类型和引用类型

值类型（例如 int 、 short、double 、 char , struct、enum）

引用类型（如 string、class ）、数组和其他集合

值类型都不可被继承

## 数字类型

### 整形

| C# 类型/关键字 | 范围                                                    | 大小                       | .NET 类型                                                    |
| :------------- | :------------------------------------------------------ | :------------------------- | :----------------------------------------------------------- |
| `sbyte`        | -128 到 127                                             | 8 位带符号整数             | [System.SByte](https://learn.microsoft.com/zh-cn/dotnet/api/system.sbyte) |
| `byte`         | 0 到 255                                                | 无符号的 8 位整数          | [System.Byte](https://learn.microsoft.com/zh-cn/dotnet/api/system.byte) |
| `short`        | -32,768 到 32,767                                       | 有符号 16 位整数           | [System.Int16](https://learn.microsoft.com/zh-cn/dotnet/api/system.int16) |
| `ushort`       | 0 到 65,535                                             | 无符号 16 位整数           | [System.UInt16](https://learn.microsoft.com/zh-cn/dotnet/api/system.uint16) |
| `int`          | -2,147,483,648 到 2,147,483,647                         | 带符号的 32 位整数         | [System.Int32](https://learn.microsoft.com/zh-cn/dotnet/api/system.int32) |
| `uint`         | 0 到 4,294,967,295                                      | 无符号的 32 位整数         | [System.UInt32](https://learn.microsoft.com/zh-cn/dotnet/api/system.uint32) |
| `long`         | -9,223,372,036,854,775,808 到 9,223,372,036,854,775,807 | 64 位带符号整数            | [System.Int64](https://learn.microsoft.com/zh-cn/dotnet/api/system.int64) |
| `ulong`        | 0 到 18,446,744,073,709,551,615                         | 无符号 64 位整数           | [System.UInt64](https://learn.microsoft.com/zh-cn/dotnet/api/system.uint64) |
| `nint`         | 取决于（在运行时计算的）平台                            | 带符号的 32 位或 64 位整数 | [System.IntPtr](https://learn.microsoft.com/zh-cn/dotnet/api/system.intptr) |
| `nuint`        | 取决于（在运行时计算的）平台                            | 无符号的 32 位或 64 位整数 | [System.UIntPtr](https://learn.microsoft.com/zh-cn/dotnet/api/system.uintptr) |

-   每个整型类型的默认值都为零 `0`。
-   每个整型类型都有 `MinValue` 和 `MaxValue` 属性， 编译时常量
-   `nint` 和 `nuint`的`MinValue` 和 `MaxValue` 属性是在运行时计算的。 这些类型的大小取决于进程设置
-   [System.Numerics.BigInteger](https://learn.microsoft.com/zh-cn/dotnet/api/system.numerics.biginteger) 结构用于表示没有上限或下限的带符号整数

### int

```C#
Console.WriteLine($"max: {int.MaxValue}, min: {int.MinValue}");
```

```C#
Console.WriteLine($"{1.CompareTo(2)}");
```

值类型也是支持用`.`来调用相关方法的

### 浮点

| C# 类型/关键字 | 大致范围                       | 精度              | 大小      | .NET 类型                                                    |
| :------------- | :----------------------------- | :---------------- | :-------- | :----------------------------------------------------------- |
| `float`        | ±1.5 x 10−45 至 ±3.4 x 1038    | 大约 6-9 位数字   | 4 个字节  | [System.Single](https://learn.microsoft.com/zh-cn/dotnet/api/system.single) |
| `double`       | ±5.0 × 10−324 到 ±1.7 × 10308  | 大约 15-17 位数字 | 8 个字节  | [System.Double](https://learn.microsoft.com/zh-cn/dotnet/api/system.double) |
| `decimal`      | ±1.0 x 10-28 至 ±7.9228 x 1028 | 28-29 位          | 16 个字节 | [System.Decimal](https://learn.microsoft.com/zh-cn/dotnet/api/system.decimal) |

-   每个浮点类型的默认值都为零，`0`
-    每个浮点类型都有 `MinValue` 和 `MaxValue` 常量

### decimal

数字中的 M 后缀指明了常数应使用 decimal 类型。 否则，编译器默认 double 类型。

```c#
decimal c = 1.0M;
decimal d = 3.0M;
Console.WriteLine(c / d); // 0.3333333333333333333333333333
```

decimal范围小于 double 类型, 但精度更高

```C#
double a = 1.0;
double b = 3.0;

decimal c = 1.0M;
decimal d = 3.0M;

Console.WriteLine(a / b); // 0.333333333333333
Console.WriteLine(c / d); // 0.3333333333333333333333333333

Console.WriteLine($"double max: {double.MaxValue}, min: {double.MinValue}");
// double max: 1.79769313486232E+308, min: -1.79769313486232E+308
Console.WriteLine($"decimal max: {decimal.MaxValue}, min: {decimal.MinValue}");
// decimal max: 79228162514264337593543950335, min: -79228162514264337593543950335
Console.WriteLine($"max: {double.PositiveInfinity/*正无穷*/}, min: {double.NegativeInfinity}");
```

### [System.Numerics.Complex](https://learn.microsoft.com/zh-cn/dotnet/api/system.numerics.complex)

## 引用类型

<img src="../../assets/Day01-%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B/image-20240925112608694.png" alt="image-20240925112608694" style="zoom:50%;" />

### object

`object` 类型是 [System.Object](https://learn.microsoft.com/zh-cn/dotnet/api/system.object) 在 .NET 中的别名

所有类型（预定义类型、用户定义类型、引用类型和值类型）都是直接或间接从 [System.Object](https://learn.microsoft.com/zh-cn/dotnet/api/system.object) 继承的

将值类型的变量转换为对象的过程称为**装箱**

将 `object` 类型的变量转换为值类型的过程称为**取消装箱**

## 其他

-   文本值的类型
    -   long后加L
    -   float后加f
    -   ...
-   泛型类型
-   隐式类型
    -   `var`的局部变量(编译器会推测)
-   匿名类型
-   可以为 null 的值类型
    -   引用类型可以为null
    -   `int?`
    -   `结构体类型?`
-   编译时类型和运行时类型
    -   编译时类型: 显式声明的变量类型 or 编译器推测的类型
    -   运行时类型: 多见于多态 

## 类型转换

### decimal和其他浮点数

不能在表达式中将 `decimal` 类型与 `float` 和 `double` 类型混合使用

必须将操作数显式转换为 `decimal` 或反向转换

```C#
double a = 1.0;
decimal b = 2.1m;
Console.WriteLine(a + (double)b);
Console.WriteLine((decimal)a + b);
```

### bool和数字

bool 不能转换为 int 


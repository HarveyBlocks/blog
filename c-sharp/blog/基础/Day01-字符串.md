# 字符串

字符串是引用类型的一种

## ==

字符串是引用类型的一种, 但是其判断相等是用值判断

```csharp
string s1 = new(new[]{ '1', '2' });
string s2 = new(new[]{ '1', '2' });
Console.WriteLine(s1 == s2); // True
```





## 字符串连接

```C#
const int num = 1;
Console.WriteLine("Hello " + num + " World");
```

## 字符串字面量

使用`"""...."""`的字符串, 不用转移字符, 自动输出回车, 引号等(代码上的呈现)

```csharp
var msg = """
          from "Hello"
          the origin
          """;

Console.WriteLine(msg);
```

## 复合格式设置

```csharp
Console.WriteLine(string.Format("Name = {0}", "Mike")); // Name = Mike
```

被`"{}"`包裹的部分是格式化字符串, 后面"Mike"的是变量, 0表示第0个变量

```csharp
{索引[,对齐方式][格式化字符串]}
```

### 索引

```csharp
Console.WriteLine(string.Format("Name = {0} {1}", "Mike","Smith")); // Name = Mike Smith
Console.WriteLine(string.Format("{0} is {1} years old", "Mike",12)); //  Mike is 12 years old
Console.WriteLine(string.Format("{1} years old boy: {0}", "Mike",12)); // 12 years old boy: Mike

```

### 对齐方式

空格补全

```csharp
Console.WriteLine(string.Format("Name = {0,12};", "Mike"));
Console.WriteLine(string.Format("Name = {0,-12};", "Mike"));
```

```l
Name =         Mike;
Name = Mike        ;
```



### 字符串格式

| 类型或类型类别                                               | 查看                                                         |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| 日期和时间类型（DateTime，DateTimeOffset）                   | [标准日期和时间格式字符串](https://learn.microsoft.com/zh-cn/dotnet/standard/base-types/standard-date-and-time-format-strings)  [自定义日期和时间格式字符串](https://learn.microsoft.com/zh-cn/dotnet/standard/base-types/custom-date-and-time-format-strings) |
| 枚举类型                                                     | [枚举格式字符串](https://learn.microsoft.com/zh-cn/dotnet/standard/base-types/enumeration-format-strings) |
| 数值类型（BigInteger、Byte、Decimal、Double、Int16、Int32、Int64、SByte、Single、UInt16、 UInt32、UInt64） | [标准数字格式字符串](https://learn.microsoft.com/zh-cn/dotnet/standard/base-types/standard-numeric-format-strings)  [自定义数字格式字符串](https://learn.microsoft.com/zh-cn/dotnet/standard/base-types/custom-numeric-format-strings) |
| [Guid](https://learn.microsoft.com/zh-cn/dotnet/api/system.guid) | [Guid.ToString(String)](https://learn.microsoft.com/zh-cn/dotnet/api/system.guid.tostring#system-guid-tostring(system-string)) |
| [TimeSpan](https://learn.microsoft.com/zh-cn/dotnet/api/system.timespan) | [标准 TimeSpan 格式字符串](https://learn.microsoft.com/zh-cn/dotnet/standard/base-types/standard-timespan-format-strings)  [自定义 TimeSpan 格式字符串](https://learn.microsoft.com/zh-cn/dotnet/standard/base-types/custom-timespan-format-strings) |

#### 0对齐

仅对数字生效

```csharp
Console.WriteLine(string.Format("Age = {0,12:000000000};", 31));
Console.WriteLine(string.Format("Age = {0,-12:000000};", 31));
// Age =    000000031;
// Age = 000031      ;
```

```csharp
Console.WriteLine(string.Format("Age = {0,12:000000000};", "MM"));
Console.WriteLine(string.Format("Age = {0,-12:000000};", "MM"));
// Age =           MM;
// Age = MM          ;
```

#### 整形进制转换

```csharp
Console.WriteLine(string.Format("{0:d}", 41));      // 41
Console.WriteLine(string.Format("{0:D}", 41));      // 41
Console.WriteLine(string.Format("{0:x}", 0x41A));   // 41a
Console.WriteLine(string.Format("0x{0:X}", 0x41A)); // 0x41A
Console.WriteLine(string.Format("{0:X}", 0x41A));   // 41A
```

#### 浮点型

```csharp
Console.WriteLine(string.Format("{0:e}", 41.123));  // 4.112300e+001
Console.WriteLine(string.Format("{0:f}", 41.123));  // 41.12
Console.WriteLine(string.Format("{0:E}", 41.123));  // 4.112300E+001
```

#### 时间

```csharp
var timeFormat = string.Format("time = {0:hh:mm:ss}", DateTime.Now);
Console.WriteLine(timeFormat); // time = 03:43:05
```

## 字符串内插

1.  string 前面有 $符号
2.  在字符串 声明中嵌入 C# 代码
3.  实际字符串使用自己生成的值替换该 C# 代码
4.  用`{{`表示字符`{`, `}}`表示字符`}`
5.  c#11之后, 可以在`{}`中换行以提高可读性

```c#
const int num = 1;
Console.WriteLine($"Hello {num} World");
```

```csharp
int safetyScore = 120;
string message = $"The usage policy for {safetyScore} is {
    safetyScore switch
    {
        > 90 => "Unlimited usage",
        > 80 => "General usage, with daily safety check",
        > 70 => "Issues must be addressed within 1 week",
        > 50 => "Issues must be addressed within 1 day",
        _ => "Issues must be addressed before continued use",
    }
}";
Console.WriteLine(message);
```

### 内插字面量

```csharp
int X = 2;
var msg = $"""
          from "{X}" 
          the origin
          """;

Console.WriteLine(msg);
```

### 内插格式化

字符串内插还支持[格式化](#复合格式设置)

吧索引的位置直接替换成值就可以了

```csharp
var f = $"一些内容{{value[,对齐方式][:格式化字符串}另一些内容"
```

```csharp
Console.WriteLine($"|{"Left",-7}|{"Right",7}|");
```

## 逐字文本

以`@`开头的字符串

```csharp
Console.WriteLine(@"\{}\'%\"); // \{}\'%\
Console.WriteLine(@"空白符:\n\t\r"); // 空白符:\n\t\r
Console.WriteLine(@"Unicode: \u0041"); // Unicode: \u0041
Console.WriteLine(@"Ascii: \x0041"); // Ascii: \x0041
Console.WriteLine(@"打不出引号? `""` <-这是一个引号, 只有一个");
// 打不出引号, 咋办?看: " <-这是一个引号, 只有一个
```


# 常量

## 数字文本

```c#
var decimalLiteral = 42; // 十进制
var hexLiteral = 0x2A; // 十六进制
var binaryLiteral = 0b_0010_1010; // 二进制
```

## 真实文本

###直接表示

-   不带后缀的文本或带有 `d` 或 `D` 后缀的文本的类型为 `double`
-   带有 `f` 或 `F` 后缀的文本的类型为 `float`
-   带有 `m` 或 `M` 后缀的文本的类型为 `decimal`

```csharp
double d = 3D;
d = 4d;
d = 3.934_001;

float f = 3_000.5F;
f = 5.4f;

decimal myMoney = 3_000.5m;
myMoney = 400.75M;
```

### 科学计数法

```csharp
double d = 0.42e2;
Console.WriteLine(d);  // output 42

float f = 134.45E-2f;
Console.WriteLine(f);  // output: 1.3445

decimal m = 1.5E6m;
Console.WriteLine(m);  // output: 1500000
```
# 程序结构

-   命名空间
-   类 class
    -   引用类型
-   结构 struct
    -   指类型
-   枚举enum
    -   指类型
-   记录 record
    -    C# 9 添加记录
    -   Java17后也有
    -   所有属性皆为共有常量
    -   自动生成该记录的toString, hashCode, equals
    -   可以是引用类型 ( record class ) 或值类型 ( record struct )。
-   委托

## 命名空间

>   namespace

通过`.`来调用命名空间的成员

```csharp
System.Console.WriteLine("Hi")
```

提前声明命名空间

```csharp
using System;
```

自定义命名空间

```csharp
namespace Application{
    // ...
}
```

命名空间可以嵌套

```csharp
namespace Application{
    // ...
    namespace ApplicationInner{
    // ...
	}
}
```

也可以

```csharp
namespace Application.ApplicationInner {
    // ...
}
```



C#10, 文件作用域的命名空间

```csharp
namespace Application.ApplicationInner;
// ...

```



## 枚举

### 声明

```c#
public enum Color {
    Red,Blue,Green,Yellow,White,Black,Purple
}
```

### 使用

```c#
Console.WriteLine(Color.Red.ToString()); // Red
Console.WriteLine(Color.Red.GetHashCode()); // 0
Console.WriteLine(Color.Blue.GetHashCode()); // 1
```

### 初始化

```C#
public enum Color {
    Red = 2,
    Blue,
    Green,
    Yellow = 7,
    White,
    Black,
    Purple
}
```

规则同C#

### 成员方法

不能直接声明成员方法, 但是可以使用[扩展方法](../高级/Day01-扩展方法.md)代替

### 构造器

没找到, 应该是不支持的

### 类型转换

能与int互相转换

```C#
Console.WriteLine((int)Color.Red); // 2
Console.WriteLine(((Color)1)); // 1
Console.WriteLine(((Color)2)); // Red
```

和string不能转

### 默认值

默认的枚举总是为`(E)0`的枚举, 即使没有0对应的枚举常量

### 位标志

枚举类型表示选项组合

-   声明

    ```C#
    // 无Flags不会造成编译上的错误, 但可以给Days days 更好的输出 
    // 底层是将Days隐式地封装成了set,以进行或和与运算
    [Flags]
    public enum Days {
        None = 0b_0000_0000, // 0
        Monday = 0b_0000_0001, // 1
        Tuesday = 0b_0000_0010, // 2
        Wednesday = 0b_0000_0100, // 4
        Thursday = 0b_0000_1000, // 8
        Friday = 0b_0001_0000, // 16
        Saturday = 0b_0010_0000, // 32
        Sunday = 0b_0100_0000, // 64
        Weekend = Saturday | Sunday
    }
    ```

-   使用

    ```C#
    Days meetingDays = Days.Monday | Days.Wednesday | Days.Friday;
    
    Console.WriteLine(meetingDays);
    // Monday, Wednesday, Friday
    
    Days workingFromHomeDays = Days.Thursday | Days.Friday;
    Console.WriteLine(meetingDays & workingFromHomeDays);
    // Friday
    
    Console.WriteLine((meetingDays & Days.Tuesday) == Days.Tuesday);
    //False
    
    Console.WriteLine((Days)37);
    // Monday, Wednesday, Saturday
    ```

## struct-class-recode

### 使用规模与设计

-   类用于对更复杂的行为建模
-   结构最适用于小型数据结构
-   记录类型是具有附加编译器合成成员的数据结构

###可变性设计

-   类通常存储计划在创建类对象后进行修改的数据
-   结构通常存储不打算在创建结构后修改的数据
-   记录通常存储不打算在创建对象后修改的数据

### 封装

### 成员

C# 没有全局变量或方法

-   字段
-   常量
-   属性
    -   有访问器的叫属性
    -   没访问器的叫字段
    -   大概
-   方法
-   构造函数
-   事件
-   终结器
-   索引器
-   运算符
-   嵌套类型

### 访问控制

可访问性, 默认为`private`

-   public
-   protected
-   internal
-   protected internal
-   private
-   专用受保护



### 扩展方法

创建单独的类型来“扩展”类，而无需创建派生类

该类型包含可以调用的方法，就像它们属于原始类型一样

## 结构

值类型

结构类型的变量包含类型的实例

默认情况下，通过拷贝来移动值

### 声明

```csharp
public struct Student {
    public string Name;
    public Student(string name) {
        Name = name;
    }
}
```

### `readonly` 结构

-   任何字段声明都必须具有 `readonly` 修饰符
-   任何属性（包括自动实现的属性）都必须是只读的或[`init`仅](https://learn.microsoft.com/zh-cn/dotnet/csharp/language-reference/keywords/init)。 请注意，仅限 init 的资源库从 [C# 版本 9 开始](https://learn.microsoft.com/zh-cn/dotnet/csharp/whats-new/csharp-version-history)可用。

```Csharp
public  struct Student {
    public readonly string Name;
    public readonly int Code = 0;

    public Student(string name) {
        Name = name;
    }

    public override string ToString() {
        return $"{base.ToString()} :{{\"Name\": '{Name}', \"Code\": {Code}}}";
    }
}
```

## 类

### 实例化对象

-   用new
-   初始化(支持直接在字段上给默认值, 编译器默认值0/0.0/False/null)

### 继承

```csharp
class Son: Parent{

}
```

-   要求Parent 不被`sealed`修饰

    `sealed`的类不能被继承

-   不支持多继承

###抽象类

使用`abstract`修饰

可以有抽象方法

不能被实例化

### 密封类

用`sealed`修饰类, 阻止该类被继承

### 静态类型

用`static`修饰, 只能有静态成员, 不能被new关键字实例化



## 记录

属性, 对象不可变

引用类型

### 值相等性

1.  两个变量类型相匹配

2.  所有属性和字段值都相同

    =>记录类型的两个变量是相等的

对于如类引用对象的相等性, 为引用的相等

### 继承

-   记录可以从记录继承
-   记录不能从类继承
-   类不能从记录继承

```csharp
record Parent { }

record Son:Parent { }
```

Typora似乎没有更新高版本的Csharp语法

### with-非破坏性修改

==with表达式==

产生一个记录的新拷贝, 并更新一部分值

```csharp
public record Student {
    public string Name;
    public int Code = 0;

    public Student(string name, int code) {
        Name = name;
        Code = code;
    }
}
```

```csharp
Student student = new("Doe", 1);
Student student2 = student with{ Name = "Jhon"} ;
```

## 接口

-   可以定义 static 方法，此类方法必须具有实现
-   可为成员方法定义默认实现
-   不能声明实例数据， 如字段、自动实现的属性或类似属性的事件

## 匿名类型

类型名由编译器生成, 每个属性的类型由编译器推断

###声明使用

```c#
var v = new { FirstName = "John", /*string 不要写*/ LastName = "Doe" };
var v2 = new { Name = v, Age = 25 };
var v3 = new { Person = new { v /*不命名, 变量名为名*/, Age = 19 }, Grade = new Grade() };
Console.WriteLine(v);
Console.WriteLine(v2);
Console.WriteLine(v3);
Console.WriteLine(v3.Person.v);
```

-   返回值和形参不能声明成匿名类型

###特征

-   匿名类型包含一个或多个**公共只读属性**
-   是class 类型，直接派生自 object
-   包含其他种类的类成员（如方法或事件）为无效
-   用来初始化属性的值不能为 *null*、*匿名函数*或 *指针类型*

常用于LINQ

###匿名类型数组

指定匿名类型数组

```csharp
var anonArray = new[] { new { name = "apple", diam = 4 }, new { name =
"grape", diam = 1 }};
```

### with-非破坏性修改

```csharp
Console.WriteLine((v3 with { Person = v3.Person with { v = v with { FirstName = "Mike" } } }).Person.v);
```

```csharp
Console.WriteLine((v3 with {
    Person = v3.Person with {
        v = v with {
            FirstName = "Mike"
        }
    }
}).Person.v);
```

###Equals/GetHashCode方法

仅当同一匿名类型的两个实例的所有属性都相等时, Equals认为true, HashCode也一致

### ToString

会重写
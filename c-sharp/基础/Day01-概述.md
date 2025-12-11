# 概述

-   适用于 .NET 平台
-   程序可以 在许多不同的设备上运行，从物联网 (IoT) 设备到云以及介于两者之间的任何设备。
-   跨平台的通用语言
-   强类型语言
-   完全面向对象
-   函数编程
-   垃圾回收和内存管理
-   泛型
-   并发编程
-   异常机制
-   语言集成查询(LINQ)

## HelloWorld

```c#
namespace ConsoleApplication1
{
    internal static class Program
    {
        /**
         * 这是
         * 多行
         * 注释
         */
        public static void Main(string[] args)
        {
            // 这是单行注释
            System.Console.WriteLine("Hello World!");
        }
    }
}
```

```C#
using System; // 先行引入命名空间

// 自定义命名空间
namespace ConsoleApplication1 
{
    /**
     * 类声明
     */
    internal static class Program 
    {
        /**
         * 方法声明
         * 当没有顶级语句时，名为 Main 的静态方法将充当 C# 程序的入口点
         */
        public static void Main(string[] args)
        {
            // Console类
            // WriteLine大驼峰的方法
            Console.WriteLine("Hello World!");
        }
    }
}
```

## 模式匹配

```go
public static bool And(bool left, bool right) => (left, right) switch { //  => 类似于函数式编程
    // (left, right) System.ValueTuple 元组
    (true, true) => true,
    (true, false) => false,
    (false, true) => false,
    (false, false) => false
};
public static bool And2(bool left, bool right) => (left, right) switch {
    (true, true) => true,
    (_, _) => false // 给出值在默认情况下的策略
};
```

## LINQ

以特定语言在容器如(对象的集合、数据库表、云存储 Blob 或 XML 结构)中查找特定对象

```c#
var honorRoll = from student in Students
                where student.GPA > 3.5
                select student;
```


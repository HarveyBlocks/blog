# 程序入口

C# 程序中只能有一个入口点

## Main方法和命令行参数

如果多个类包含 Main 方法，必须使用 [StartupObject](https://learn.microsoft.com/zh-cn/dotnet/csharp/language-reference/compiler-options/advanced#mainentrypoint-or-startupobject) 来编译程序，以指定将哪个 Main 方法用作入口点

###声明

1.  Main 必须在类或结构中进行声明
2.  可以是内部类class 可以是非公开的, 可以是static类
3.  Main方法必须为 static
4.  Main 可以具有任何访问修饰符(`file` 除外)
5.  Main 的返回类型可以是 `void` 、 `int` 、 `Task` 或 `Task<int>` 
    -   **当且仅当** Main 返回 `Task` 或 `Task<int>` 时， Main 的声明可包括 async 修饰符
    -   ` async void Main` 是不合法的
6.  命令行参数是可选择的
7.  当应用程序入口点返回 Task 或 Task 时，编译器生成一个新的入口点，该入口点调用应用程序代码中声明的入口点方法

```C#
[public|protected internal|protected|internal|private protected|private(默认)] 
static void|int|[async] Task|[async] Task<int> Main([string[] args]){
    
}
```

### Async Main

```C#
class AsyncMainReturnValTest {
    public static int Main() {
        return AsyncConsoleWork().GetAwaiter().GetResult();
    }

    private static async Task<int> AsyncConsoleWork() { 
        // Main body here
        return 0;
    }
}
```

可替换为

```C#
class Program {
    static async Task<int> Main(string[] args) {
        return await AsyncConsoleWork();
    }

    private static async Task<int> AsyncConsoleWork() { 
        // main body here
        return 0;
    }
}
```



## 顶级语句

无需在控制台应用程序项目中显式包含 Main 方法

直接写在文件层面的语句

```go
using System;


Console.WriteLine("Hello World!");
```

编译器自动生成Program 类和入口方法

一个项目(编译单元)**只能有一个**包含顶级语句的文件

可以有Main函数, 但是编译器发出警告

### 全局命名空间

顶级语句隐式位于全局命名空间中

###命名空间和类型定义

命名空间和类型定义必须位于顶级语句之后

### 命令行参数

在顶级语句直接引用`args`变量来访问命令行参数

`args`以其`args.Length==0`来表示无命令行参数, 而不会为null

### 进程的退出代码

顶级语句也可以用return返回进程的退出码

### 隐式入口方法

编译器生成入口方法

| 包含await | 包含return | 生成的Main方法                               |
| --------- | ---------- | -------------------------------------------- |
| ×         | ×          | `static void Main(string[] args)`            |
| √         | ×          | `static async Task Main(string[] args)`      |
| ×         | √          | `static int Main(string[] args)`             |
| √         | √          | `static async Task<int> Main(string[] args)` |


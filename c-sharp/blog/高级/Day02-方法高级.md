# 方法

## 方法参数

### out参数

out参数用作方法的返回值的另一种形式, 以间接实现多返回值

out参数是不可读的

-   声明方法

    ```csharp
    public static void GetNumber(out int num1, out int num2) {}
    ```

-   方法体

    ```csharp
    public static void GetNumber(out int num1, out int num2) {
    	num1 = num2 = 1; // 每个out 参数都要赋值   
    }
    ```

-   调用方法

    可以在调用方法之前声明变量

    ```csharp
    int num2 = -1;
    GetNumber(out int num1, out num2);
    Console.WriteLine($"{num1}, {num2}");
    ```

    可以在调用方法的参数列表声明out参数

    ```csharp
    GetNumber(out int num1, out int num2);
    Console.WriteLine($"{num1},{num2}");
    ```

## 析构元组

### 隐式转换

创建`Deconstruct`析构方法, 用于将一个类/结构体等转换成一个析构元组

其元组的返回值, 在`out`参数中体现

```csharp
private class Point {
    public int X { get; set; }
    public int Y { get; set; }

    public Point(int x = 0, int y = 0) {
        X = x;
        Y = y;
    }

    public void Deconstruct(out int x, out int y, out double z) {
        // 元组的元素个数是自定义的, 只不过如果只有一个元素似乎有点难搞
        x = X;
        y = Y;
        z = Math.Sqrt(X * X + Y * Y);
    }
    public void Deconstruct(out int x, out int y) {
        // 可以搞重写
        // 但是, 即使参数列表的类型不同, 但是数量相同, 多个Deconstruct之间也是不明确的
        // 在重载解析过程中，不能区分具有相同数量参数的 Deconstruct 方法
        // 虽然编译不会报错
        Deconstruct(out int x2, out int y2, out _);
        x = x2;
        y = y2;
    }

    public static void Show(Point point) {
        int a, b;
        (a, b, _) = point; // 发生隐式转换
        Console.WriteLine($"x is {a},y is {b}");
    }
}
```

### 显式转换

创建一个函数, 返回值的类型是元组, 元组的各个元素的类型被各自指定

没有函数名的要求



```csharp
public (int, int, double) ToTuple() {
    return (X, Y, Math.Sqrt(X * X + Y * Y));
}
```

### Record的自带析构

```csharp
public record Name(string FirstName, string LastName) {
    public string FirstName { get; } = FirstName;
    public string LastName { get; } = LastName;
}

public static void Main() {
    Name name = new Name("John", "Smith");
    var (firstName, lastName) = name;
    Console.WriteLine(firstName + " " + lastName);
}
```

析构不是虚方法不能进行多态

```csharp
Person p = new Student(new Name("John", "Smith"), 12, 99);
Student s = new Student(new Name("John", "Smith"), 12, 99);
var (pName, pAge) = p;
var (sName, sAge, score) = s;
```

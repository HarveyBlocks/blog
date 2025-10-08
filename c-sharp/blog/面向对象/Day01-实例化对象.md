# 实例化对象

## 类实例

使用new运算符在堆上分配, 最终交由垃圾回收器回收

```csharp
ExampleClass instance2 = new();
```

或

```csharp
var instance2 = new ExampleClass();
```



## 结构实例

使用new运算符在堆上分配, 最终交由垃圾回收器回收

或通过已有实例拷贝实现实例化, 在栈上分配, 随着栈帧的回收而回收

```csharp
public struct Student {
    public string Name { get; set; }
    public int Age { get; set; }
    public int Score { get; set; }

    public Student(string name, int age, int score) {
        Name = name;
        Age = age;
        Score = score;
    }

    public Student() : this("", 0, 0) { }
}

public static class FlagsEnumExample {
    public static void Main() {
        Student s1 = new Student("", 12, 3); // 有参
        Student s2 = new("", 12, 3); // 有参
        Student s3 = s1; // 拷贝
    }
}
```

## 相等比较

### 类内存引用相等

所有引用类型隐式继承`System.Object`

使用`Object.Equals()`静态方法

```csharp
StudentClass s1 = new StudentClass("", 12, 3); // 有参
StudentClass s2 = new("", 12, 3); // 有参
StudentClass s3 = s1;
Console.WriteLine(Object.Equals(s1, s2)); // False
Console.WriteLine(object.Equals(s1, s2)); // False
Console.WriteLine(s3.Equals(s1)); // True
```

### 结构值相等

所有值类型隐式继承`System.ValueType`

```csharp
StudentStruct s1 = new StudentStruct("", 12, 3); // 有参
StudentStruct s2 = new("", 12, 3); // 有参
StudentStruct s3 = s1; // 无参
Console.WriteLine(ValueType.Equals(s1, s2)); // True
Console.WriteLine(s3.Equals(s1)); // True
```

值类型其实也隐式继承Object类

### 类的值相等

类重写`Equals`个`==`

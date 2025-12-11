# 多态

## 继承

- 结构不支持继承，但可以实现接口
- 子类中调用父类成员, 使用`base`关键字

```csharp
class Teacher : Person, IEquatable<Teacher>, IEquatable<Student> {
    public Teacher(string name) : base(name) { }

    public bool Equals(Teacher other) {
        return other != null && Name == other.Name;
    }

    public bool Equals(Student other) {
        return other != null && Name == other.Name;
    }
}
```

### 阻止继承

`sealed`

### 成员覆盖

相同名称或签名使子类覆盖父类成员

```csharp
public class Person {
    public string Name { get; set; }
    public int Age { get; set; }

    public Person(string name, int age) {
        Name = name;
        Age = age;
    }

    public void Say() {
        Console.WriteLine($"I'm {Name}, I'm {Age} years old. I'm a Person");
    }
}

public class Student : Person {
    public int Score { get; set; }

    public Student(string name, int age, int score) : base(name, age) {
        Score = score;
    }

    public Student() : this("", 0, 0) { }

    public void Say() {
        Console.WriteLine($"I'm {Name}, I'm {Age} years old. I'm a Student");
    }
}

public static class FlagsEnumExample {
    public static void Main() {
        Person p = new Student("A", 12, 3);
        Student s = new Student("A", 12, 3);
        s.Say(); // I'm a Student
        p.Say(); // I'm a Person
    }
}
```

具体见下

## 抽象

### 抽象方法

使用`abstract`声明抽象方法

### 抽象类

使用`abstract`声明抽象类

防止用new来实例化

抽象类中可以包含抽象成员方法而不提供实现

抽象类的第一个非抽象子类必须实现抽象类的成员方法

如果子类也是抽象的则不用实现抽象方法



## 重载-覆盖

### 虚方法

使用`virtual`声明虚方法(默认是非虚方法)

子类可以使用重新实现并在多态中重载(override)虚方法

不使用虚方法, 就会出现子类成员覆盖父类成员



字段不能是虚拟的，只有方法、属性、事件和索引器才可以是虚拟的



### 重载

子类成员**必须使用 override 关键字**显式指示该方法将参与虚调用

override和virtual必须是成对的才能实现重载

```C#
public virtual void Say() {
    Console.WriteLine($"I'm {Name}, I'm {Age} years old. I'm a Person");
}
```
```C#
public override void Say() {
    Console.WriteLine($"I'm {Name}, I'm {Age} years old. I'm a Student");
}
```

override和abstract成对实现重载, 被重载的成员可以被进一步派生的类给重载

```c#
public abstract class Animal {
    public abstract void Say();
}

public class Person : Animal {
    public override void Say() {
        Console.WriteLine($"I'm a Person");
    }
}
public class Student : Person {
    public override void Say() {
        Console.WriteLine($"I'm a Student");
    }
}
```

### 访问器的重载

访问器也能重载

```csharp
public class Person : Animal {
    public virtual int Age { get; set; } // 标注virtual
}

public class Student : Person {
    public override int Age {  // 进行重载  
        get => base.Age;
        set {
            if (value < 0 || value > 18) {
                throw new ArgumentOutOfRangeException();
            }

            base.Age = value;
        }
    }
}
```

### 重载运算符

```c#
public static bool operator ==(StudentClass a, StudentClass b) =>
    a.Name == b.Name
    && a.Age == b.Age
    && a.Score == b.Score;

public static bool operator !=(StudentClass a, StudentClass b) => !(a == b);
```



### 成员覆盖

在例子中

```csharp
public class Person {
    public void Say() {
        Console.WriteLine($"I'm {Name}, I'm {Age} years old. I'm a Person");
    }
}

public class Student : Person {
    public void Say() {
        Console.WriteLine($"I'm {Name}, I'm {Age} years old. I'm a Student");
    }
}

public static class FlagsEnumExample {
    public static void Main() {
        Person p = new Student("A", 12, 3);
        Student s = new Student("A", 12, 3);
        s.Say(); // I'm a Student
        p.Say(); // I'm a Person
    }
}
```

发出警告

![image-20240929195619817](../../assets/Day01-%E5%A4%9A%E6%80%81%E6%80%A7/image-20240929195619817.png)

使用`new`或`override`指定本方法与其父类同名方法处于哪种关系, "重载"or"覆盖"

```csharp
public new void Say() {
    Console.WriteLine($"I'm {Name}, I'm {Age} years old. I'm a Student");
}
```

如果派生类中的方法前面带有 new 关键字，则该方法被定义为独立于基类中的方 法。

此时父类的方法是否修饰`virtual`与此派生类方法无关

当前派生类的子类的方法修饰`override`不能与间接父类的`virtual`遥相呼应, 依旧能实现重载

除非

```csharp
public new virtual void Say() {
    Console.WriteLine($"I'm {Name}, I'm {Age} years old. I'm a Student");
}
```

是允许的

```csharp
public class Person : Animal {
    public virtual void Say() {
        Console.WriteLine("I'm a Person");
    }
}

class Student : Person {
    public new virtual void Say() {
        Console.WriteLine("I'm a Student");
    }
}

class Boy : Student {
    public override void Say() {
        Console.WriteLine("I'm a Boy");
    }
}
```

此时各自的逻辑关系是

```csharp
Person p = new Student();
Person sp = new Student();
Student ss = new Student();
Person bp = new Boy();
Student bs = new Boy();
Boy bb = new Boy();
p.Say(); // Person
sp.Say(); // Person
ss.Say(); // Student
bp.Say(); // Person
bs.Say(); // Boy
bb.Say(); // Boy
```

### 方法选择

```csharp
public class Person : Animal {
    public virtual void Say(int code) {
        Console.WriteLine($"I'm a Person {code}");
    }
}

class Student : Person {
    public override void Say(int code) {
        Console.WriteLine($"I'm a Student {code}");
    }

    public void Say(double code) {
        Console.WriteLine($"I'm a Student {code}f");
    }
}
```

执行代码调用方法

```csharp
Student s = new Student();
s.Say(10000); // 总是会调用double类型
```

这是由于int能自动转成double且`void say(int)`不直接在Student类的方法表中, 而在父类的虚方法表中

所以会优先将int转为double, 然后调用`void say(double)`方法, 而不是`void say(int)`方法

似乎是由于调用虚方法比类型转换更消耗性能(不知道)



解决方法是, 使用类型转换, 将调用方法的对象转为基类

```csharp
Student s = new Student();
((Person)s).Say(10000); 
```



```csharp
public class Person  {
    public virtual void Play(Person p) {
        Console.WriteLine($"I play with person {p} ");
    }
}

class Student : Person {
    public override void Play(Person p) {
        Console.WriteLine($"I play with person {p} ");
    }

    public void Play(Student s) {
        Console.WriteLine($"I play with student {s} ");
    }
}
```

不会出现这种情况

### 阻止重载

用`sealed`修饰方法


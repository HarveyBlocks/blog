# 委托

函数式接口

##声明

```csharp
public delegate TResult Func<in T1, in T2, in T3, in T4, in T5, in T6, in T7, in T8, out TResult>(
  T1 arg1,
  T2 arg2,
  T3 arg3,
  T4 arg4,
  T5 arg5,
  T6 arg6,
  T7 arg7,
  T8 arg8);
```

##使用

```csharp
var runnable = new Action(() => Console.WriteLine());
var consumer = new Action<object>(a => Console.WriteLine(a));
var supplier = new Func<object>(() => 1);
var function = new Func<object, object>(a => 1);

consumer(12);
```

```csharp
var choose = string (bool b) => b ? "true" : "false";
var choose1 =  (bool b) => b ? "true" : "false";
var choose2 = new Func<bool, string>(string (b) => b ? "true" : "false");
var choose3 = new Func<bool, string>(b => b ? "true" : "false");
```

```csharp
int number = 0;
var runnable1 = new Action(() => Console.WriteLine(++number));
var runnable2 = new Action(() => Console.WriteLine(--number));
runnable1.Invoke(); // 1
runnable1.Invoke(); // 2
runnable2.Invoke(); // 1
runnable1.Invoke(); // 2
runnable2.Invoke(); // 1
```
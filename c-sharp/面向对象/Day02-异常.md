# 异常

## 异常抛出

-   throw

## 异常捕获

-   try-catch[-finnally]

-   catch住的异常不需要可以不声明变量

    ```csharp
    try {
        throw new DivideByZeroException();
    } catch (DivideByZeroException) {
        Console.Error.WriteLine("Dividing by zero");
    }
    ```

## `when` 异常筛选器

```csharp
try {
    throw new DivideByZeroException();
} catch (Exception e) when (e is ArgumentException or DivideByZeroException) {
    Console.WriteLine($"Processing failed: {e.Message}");
}
```


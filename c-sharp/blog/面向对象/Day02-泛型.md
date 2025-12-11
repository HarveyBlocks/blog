# 泛型

值类型和引用类型都能作为泛型类型参数

## 泛型函数

### 声明

```csharp
T 方法名<T>(参数类型<T> 形式参数) {
    // 方法体
}
```

```csharp
public T GetFirst<T>(List<T> ls) {
    return ls[0];
}
```


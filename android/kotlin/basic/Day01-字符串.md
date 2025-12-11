# 字符串

>   String

用`""`包围

可以使用`s[i]`获取字符, 也可以使用`for` 遍历

```kotlin
for (i in 0 until str.length) {
    // until()=>this .. (to - 1)
    print("${str[i]},");
}
println()
for (s in str) {
    print("$s,");
}
println()
```

## 多行字符串

用三个引号, 内部没用转义, 且可以包含任何字符

```kotlin
val str = """
// 啥
    /* 都可以 */
    """
```

## 字符串模板

对于单独的变量, 使用`$variable`在字符串中插值

```kotlin
"hello $name"
```

对于需要对字符串进行一定运算的, 使用`${caculate...}`在字符串中运算

```kotlin
"hello ${student.name}"
```

多行字符串也能搞模板

```kotlin
val text = """
    $str
"""
```

那么, 多行字符串的`$` 为之奈何?

```kotlin
val text = """
    ${'$'}
"""
```

## 格式化

>   String.format()

就是传统的格式化


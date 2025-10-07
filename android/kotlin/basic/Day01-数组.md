# 数组

## 创建

-   `arrayOf(...item)`
-   `arrayOfNulls(size)`
-   `emptyArray()`
-   `Array<T>`构造器

还有原生的数组, 为基本数据类型, 不装包, 例如`BooleanArray`, 这种数组和`Array<T>`没有继承关系

## 嵌套数组

其实是锯齿数组

## Api

### 重载plus

```kotlin
val array1: Array<Int> = arrayOf(1, 2)
val array2 = array1 + 3 // 拷贝一份后增加
array1[1] = 4
println(array1.joinToString(",")) // 1,4
println(array2.joinToString(",")) // 1,2,3
```

源码

```kotlin
public actual operator fun <T> Array<T>.plus(element: T): Array<T> {
    val index = size
    val result = java.util.Arrays.copyOf(this, index + 1)
    result[index] = element
    return result
}
```

### sum

`.sum()`

累和

### shuffle

`.shuffle()`

洗牌

### 转List/Set

`.toList()`

`.toSet()`

### 转Map

需要Pair类型的元素

```kotlin
val pairArray = arrayOf("apple" to 120, "banana" to 150, "cherry" to 90, "apple" to 140)
println(pairArray.toMap())
```

to的源码

```kotlin
public infix fun <A, B> A.to(that: B): Pair<A, B> = Pair(this, that)
```

## 展开运算符

`*`

将数组的内容展开

-   在参数列表中使用

    ```kotlin
    val a = arrayOf(1, 2, 3)
    val list = asList(-1, 0, *a, 4)
    ```

    ```kotlin
    val a = intArrayOf(1, 2, 3) // IntArray 是一种原生类型数组
    val list = asList(-1, 0, *a.toTypedArray(), 4)
    ```


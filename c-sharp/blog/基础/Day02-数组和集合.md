# 数组和集合

## 数组

###一维数组

声明赋值

```csharp
int[] arr = new int[]{ 1, 2, 3, }; // 最后可以留逗号
int[] arr1 = new []{ 1, 2, 3 };
int[] arr2 = { 1, 2, 3 };
var arr3 = new int[]{ 1, 2, 3 };
var arr4 = new []{ 1, 2, 3 };
```

使用

```csharp
int[] arr ={ 1, 5, 4, 2, 4, 2, 7, 8, 3 };
Console.WriteLine(arr);
Console.WriteLine(arr.GetType());
Console.WriteLine(arr.Length);
Array.ForEach(arr, e => Console.Write($"{e} "));
Console.WriteLine();
Array.Sort(arr);
Array.ForEach(arr, e => Console.Write($"{e} "));
Console.WriteLine();
```

### 多维数组

```csharp
int[][] array = new int[3][];
array[0] = new int[]{ 1, 2, 3 };
array[1] = new int[]{ 1, 2, 3, 4 };
array[2] = new int[]{ 1, 2, 3, 4, 5 };
int[,] array2 = new int[3, 2];
int[,] array3 ={ { 1, 2 },{ 3, 4 },{ 5, 6 },{ 7, 8 } };
int[,] array4 ={ { 1, 2, 3 },{ 3, 4, 3 },{ 5, 6, 3 },{ 7, 8, 3 } };
```

```csharp
int[][] array = new int[3][];
array[0] = new int[]{ 1, 2, 3 };
array[1] = new int[]{ 1, 2, 3, 4 };
array[2] = new int[]{ 1, 2, 3, 4, 5 };
int[,] array2 = new int[3, 2];
Console.WriteLine(array.GetLength(0)); // 3
// Console.WriteLine(array.GetLength(1)); 越界
Console.WriteLine(array2.GetLength(0)); // 3
Console.WriteLine(array2.GetLength(1)); // 2
```

###继承

皆继承自Array类

```csharp
Array arr = new int[]{ 1, 2, 3 };
Console.WriteLine(arr);
Console.WriteLine(arr.GetType());
Console.WriteLine(arr.Length);
```

## 不可变长集合

-   Array
-   System.Span
-   System.Memory

## 列表List

## 映射Dictionary
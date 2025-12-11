# 数组与切片

## 数组

>   Array

固定大小的连续内存空间

### 声明

```go
var 数组变量名 [元素数量]元素类型
```

```go
var nums [3]int
nums[0] = 1
nums[1] = 2
nums[2] = 3
fmt.Println(nums)
```

只声明不初始化的数组会给每个元素赋默认值, 每个元素的默认值具体的值是多少取决于元素的类型

只声明不初始化的数组不是nil, 不能和nil比较

### 初始化

```go
var 数组变量名 = [元素数量]元素类型{元素值0, 元素值1, 元素值2...}
```

```go
var nums = [3]int{1, 2, 3}
fmt.Println(nums)
```

使用`...`代替元素数量, 让编译器确定元素数量

```go
var nums = [...]int{1, 2, 3}
fmt.Println(nums)
```

### 切片操作

见下

## 切片

>   Slice

-   数据容器
-   有序
-   动态分配内存大小
-   可变长
-   元素类型相同

字符串可以像切片一样操作

### 组成

-   指向起始元素的原始指针
    -   其指向真正存数据的内存空间(可以是数组, 也可以是切片本身)
-   元素数量
-   容量

### 声明

```go
var 切片名 []元素类型
```

只声明不初始化的切片的默认值是nil

### 空切片

```go
var 切片名 = []元素类型{}
```

此空切片已经被开辟了内存空间, 故不是nil

### 使用语法

索引从下标0开始

```go
var nums []int = []int{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}
fmt.Println(nums)      // [0 1 2 3 4 5 6 7 8 9]
fmt.Println(nums[:])   // [0 1 2 3 4 5 6 7 8 9]
fmt.Println(nums[0])   // 0
fmt.Println(nums[0:1]) // [0]
fmt.Println(nums[5:])  // [5 6 7 8 9]
fmt.Println(nums[1:3]) // [1 2]
fmt.Println(nums[:3])  // [0 1 2]
// fmt.Println(nums[-1]) 不支持
// fmt.Println(nums[0:4:2]) 不支持
```

### make()

```go
make([]元素类型, size, capacity)
```

-   `size`   为这个类型分配size个元素。

-   `capacity`   预分配的元素数量，这个值设定后不影响size，提前分配空间，降低多次分配空间造成的性能问题

### append

动态添加元素

当空间不能容纳足够多的元素时，切片就会进行“扩容”

切片在扩容时，容量的扩展规律按容量的2倍数扩充，例如1、2、 4、8、16……

```go
var nums = []int{}
fmt.Printf("size=%02d \t cap=%02d \t %p\n", len(nums), cap(nums), nums) // 存在栈里
for i := 0; i < 64; i++ {
    nums = append(nums, 0)
    fmt.Printf("size=%02d \t cap=%02d \t %p\n", len(nums), cap(nums), nums) // 已经在堆里了
}
```

### copy

```go
var src = []int{1, 2, 3, 4, 5, 6}
target := make([]int, 12)
copy(target, src) 
fmt.Println(target)  // [1 2 3 4 5 6 0 0 0 0 0 0]
fmt.Println(src) // [1 2 3 4 5 6 ]
```

copy是深拷贝而赋值`=`是浅拷贝

```go
var src = []int{1, 2, 3, 4, 5, 6}
target := make([]int, 12)
copy(target, src)
ref := src
fmt.Println(target) // [1 2 3 4 5 6 0 0 0 0 0 0]
fmt.Println(src)    // [1 2 3 4 5 6]
fmt.Println(ref)    // [1 2 3 4 5 6]
src[0] = -1
fmt.Println(target) //  [1 2 3 4 5 6 0 0 0 0 0 0]
fmt.Println(src)    // [-1 2 3 4 5 6]
fmt.Println(ref)    // [-1 2 3 4 5 6]
```

### 删除

可以用

```go
nums = append(nums[:index], nums[index+1:])
```

的方式进行删除index位置的元素

## 排序

```go
var src = []string{"1", "2", "5", "6", "4", "3", "6", "7", "4", "3", "2", "1"}
fmt.Println(src)  // [1 2 5 6 4 3 6 7 4 3 2 1]
sort.Strings(src) // 直接作用于值
fmt.Println(src)  // [1 1 2 2 3 3 4 4 5 6 6 7]
```


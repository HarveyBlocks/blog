# IllegalArgumentException
- RuntimeExcption
- 非法参数异常



## NumberFormatException

- RuntimeExcption
- extends IllegalArgumentException
- 数字格式异常

```java
Integer.valueOf("abc");
```





# IndexOutOfBoundsException

- RuntimeException
- 下标越界异常



## ArrayIndexOutOfBoundsException

- RuntimeException
- extends IndexOutOfBoundsException
- 数组下标越界异常

``` java
int[] arr = {1,2,3};
System.out.println(arr[3]);
```

# IllegalStateException

- RuntimeException
- 不允许修改锁定的参数映射
- 当前对客户端的响应已经结束，不能在响应已经结束（或说消亡）后再向 客户端（实际上是缓冲区）输出任何内容。


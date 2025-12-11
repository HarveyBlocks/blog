# 数组

## 数组的定义

### 静态初始化

``` java
int[] numbers={1,2,3,4,5,6,7};   //数据类型(数组类型)和变量名很好地分开来了
int numbers[]={1,2,3,4,5}    ;       //不推荐

int[] numbers = {1,2,3,4,5,};//末尾添逗号也是可以的
```

### 动态初始化

``` java
int[] numbers;//声明一个数组
numbers=new int[10];//指定它的大小

int[] numbers=new int[10]; //声明并指定数组的大小
```

### 用匿名数组初始化

```java
//---------------------↓匿名内部类
int[] numbers = {new int[]{1,12}};
```

### 数组初始值

```java
int[] numbers1 = new int[2];
System.out.println(Arrays.toString(numbers1));//[0,0]
int[] numbers2 = new int[0];
System.out.println(Arrays.toString(numbers2));//[]
//空数组和null不一样

String[] strings = new String[2];
System.out.println(Arrays.toString(strings));//[null, null]
```

## 数组数据类型的转化

```java
double[] numbers=new double[]{1,2,3};
```

## 数组的操作

``` java
System.out.println(numbers[1]);//数组数据读取

int len=numbers.length;//数组长度

numbers[1]=1;//赋值，若不赋值，默认值参考Day04-数据类型
```

## 数组的遍历

``` java
for (int number:numbers){
    System.out.println(number);
}

numbers.for//idea的快捷操作
```

## 多维数组

- java实际上没有多维数组,只有**数组的数组**

``` java
int[][]... numberss={{1,2},{2,3},{3,4}};
int[][] bumberss = new int[10][6];
```

### 不规则数组

```java
Integer[][] nums = {
        {1,2,3},
        {1,2},
        {2,4,3},
};

for (int i = 0; i < nums.length; i++) {
    for (int j = 0; j < nums[i].length; j++) {
        System.out.print(nums[i][j]+",");
    }
    System.out.println();
}

System.out.println("==================");

for (Integer[] integers:nums) {
    for (Integer integer:integers){
        System.out.print(integer+",");
    }
    System.out.println();
}
```

### Arrays.deepToString(Object[] a)

```Java
Integer[][][] nums = {
        {{1},{2,3,3},{4,2}},
        {{1,2},{2,5,1,4},{1,2,4},{1,4}},
        {{2,2,4},{4,2,3},{}},
};
System.out.println(Arrays.deepToString(nums));
```

## 冒泡排序

``` java
public static int[] bubble(int[] array){
        int temp=0;
        boolean flag;
        for (int i = 0; i < array.length; i++) {
            flag=false;
            for (int j = 0; j < array.length-i-1; j++) {
                if (array[j] > array[j + 1]) {
                    temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    flag = true;
                }
            }
            if (!flag) {         //优化
                return array;
            }
        }
        return array;
    }
}
```

## Arrays类

**java.util.Arrays**是一个与数组相关的工具类，里面提供了大量静态方法，用来实现数组常见的操作。

### **public static String toString(数组)**:

将参数数组变成字符串(按照默认格式:[元素1,元素2,元素3…]);

```java
//定义一个字符串
String str = "sfehacdkj4312";

//将字符串转化为字符数组
char[] chars = str.toCharArray();

Arrays.toString(chars);
System.out.println(chars);//sfehacdkj4312
```

### **Arrays.sort(); //数组排序**

**public static void sort(数组)**:按照**默认升序(从小到大)**对[数组元素](https://so.csdn.net/so/search?q=数组元素&spm=1001.2101.3001.7020)进行排序。

备注：
1:如果是数值，sort默认按照升序从小到大；
2:如果是字符串，sort默认按照字母升序；
3:如果是自定义的类型，那么这个自定义的类需要有Comparable或者Comparator接口的支持。

**1.数字排序**

```
    int[] intArray = new int[] { 4, 1, 3, -23 };
    Arrays.sort(intArray);
    //输出： [-23, 1, 3, 4]
```

**2.字符串排序，先大写后小写**

```
    String[] strArray = new String[] { “z”, “a”, “C” };
    Arrays.sort(strArray);
    //输出： [C, a, z]
```

**3.严格按字母表顺序排序，也就是忽略大小写排序 Case-insensitive sort**

```java
    Arrays.sort(strArray, String.CASE_INSENSITIVE_ORDER);
    //输出： [a, C, z]
```

**4.反向排序， Reverse-order sort**

```java
    Arrays.sort(strArray, Collections.reverseOrder());
    //输出：[z, a, C]
```

**5.忽略大小写反向排序 Case-insensitive reverse-order sort**

```java
    Arrays.sort(strArray, String.CASE_INSENSITIVE_ORDER);
    Collections.reverse(Arrays.asList(strArray));
    //输出： [z, C, a]
```

**6.选择数组指定位置进行排序**

```java
    int[] arr = {3,2,1,5,4};
    Arrays.sort(arr,0,3);//给第0位（0开始）到第3位（不包括）排序
    String str = Arrays.toString(arr); // Arrays类的toString()方法能将数组中的内容全部打印出来
    System.out.print(str);
    //输出：[1, 2, 3, 5, 4]
```

## 自定义排序

```java
Integer[] nums = {1,2,3};
System.out.println();
Arrays.sort(nums,(o1,o2)-> o2-o1);
Arrays.stream(nums).forEach(System.out::print);//321
```

### **Arrays.fill(); //填充数组**

```java
    int[] arr = new int[5];//新建一个大小为5的数组
	Arrays.fill(arr,4);//给所有值赋值4
	String str = Arrays.toString(arr); // Arrays类的toString()方法能将数组中的内容全部打印出来
	System.out.print(str);
	//输出：[4, 4, 4, 4, 4]
12345
	int[] arr = new int[5];//新建一个大小为5的数组
	Arrays.fill(arr, 2,4,6);//给第2位（0开始）到第4位（不包括）赋值6
	String str = Arrays.toString(arr); // Arrays类的toString()方法能将数组中的内容全部打印出来
	System.out.print(str);
	//输出：[0, 0, 6, 6, 0]
```

### **Arrays.equals(); //比较数组元素是否相等**

```java
	int[] arr1 = {1,2,3};
	int[] arr2 = {1,2,3};
	System.out.println(Arrays.equals(arr1,arr2));
	//输出：true
	//如果是arr1.equals(arr2),则返回false，因为equals比较的是两个对象的地址，不是里面的数，而Arrays.equals重写了equals，所以，这里能比较元素是否相等。

```

### **Arrays.binarySearch(); //二分查找法找指定元素的索引值（下标）**

数组一定是排好序的，否则会出错。找到元素，只会返回最后一个位置

```java
	int[] arr = {10,20,30,40,50};
	System.out.println(Arrays.binarySearch(arr, 30));
    //输出：2 （下标索引值从0开始）

	int[] arr = {10,20,30,40,50};
	System.out.println(Arrays.binarySearch(arr, 36));
	//输出：-4 （找不到元素，返回-x，从-1开始数，如题，返回-4）

	int []arr = {10,20,30,40,50};
	System.out.println(Arrays.binarySearch(arr, 0,3,30));
	//输出：2 （从0到3位（不包括）找30，找到了，在第2位，返回2）

    int []arr = {10,20,30,40,50};
    System.out.println(Arrays.binarySearch(arr, 0,3,40));
    //输出：-4 （从0到3位（不包括）找40，找不到，从-1开始数，返回-4）

```

### Arrays.copeOf()赋值数组

- 为何不用赋值运算符:

``` java
int[] num1 = {1,2,3};
int[] num2 = num1;
num2[1] = 5;
System.out.println(Arrays.toString(num1));//153
```

因为数组的标识符存的是数组的位置

一改,全改

``` java
int[] num1 = {1,2,3};
int[] num2 = Arrays.copyOf(num1,5);//扩容
System.out.println(Arrays.toString(num2));//[1, 2, 3, 0, 0]
```

### **Arrays.copeOf() 和Arrays.copeOfRange(); //切片数组**

```java
	int[] arr = {10,20,30,40,50};
	int[] arr1 = Arrays.copyOf(arr, 3);
	String str = Arrays.toString(arr1); // Arrays类的toString()方法能将数组中的内容全部打印出来
	System.out.print(str);
	//输出：[10, 20, 30] （截取arr数组的3个元素赋值给新数组arr1）

	int []arr = {10,20,30,40,50};
	int []arr1 = Arrays.copyOfRange(arr,1,3);
	String str = Arrays.toString(arr1); // Arrays类的toString()方法能将数组中的内容全部打印出来
	System.out.print(str);
	//输出：[20, 30] （从第1位（0开始）截取到第3位（不包括））
```


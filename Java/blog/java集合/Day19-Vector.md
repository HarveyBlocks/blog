# Vector类

 字面义是向量

存储结构:数组

可以实现可增长的对象数组

根据需要增大或缩小

存在索引

实现List方法

# 构造方法

Vector()

- 事情颞部数据数组的大小为10,其标准容量大小为0

## 实例化Vector()

```java
//创建集合
Vector vector = new Vector();
```



## 方法

add()

addAll().....常见的



```java
//1.添加元素
vector.add("01");
vector.add("02");
vector.add("01");
vector.add("03");
vector.add("03");
vector.add("04");
vector.add("02");
vector.add("06");
vector.add("05");
System.out.println(vector.size());//9

//2.判断
System.out.println(vector.contains("05"));//true
System.out.println(vector.isEmpty());//false

//3.删除
vector.remove(3);//return oldValue
System.out.println(vector.size());//8
vector.remove("03");
System.out.println(vector.size());//7

//综合
while(!vector.isEmpty()){
        System.out.print(vector.get(0)+",");
        vector.remove(0);
}
//01,02,01,04,02,06,05,

```

## 枚举器elements()

返回元素 Enumeration 枚举器

```java
//[01, 02, 01, 04, 02, 06, 05]

//4.遍历,用枚举器
Enumeration en = vector.elements();

while (en.hasMoreElements()) {
    String str = en.nextElement().toString();
    //此时指针指向1
    System.out.print(str+",");
    //01,01,02,05,
    vector.remove(str);
    //删除0号元素,所有元素往前挪,指针还指向1,即原来的2号元素
}
System.out.println("\n"+vector.size());//3

en = vector.elements();//重置指针

while (en.hasMoreElements()) {
    System.out.print(en.nextElement()+",");
    //04,02,06,
}
while(!vector.isEmpty){
    
}
```

# LinkedList类

- 双向链表结构
- 增删快,查询慢

## 基本用法

### 创建集合

```java
//创建集合
LinkedList ll = new LinkedList();
```

### 添加

```java
//添加元素
Student s1 = new Student("钱二",12,97);
Student s2 = new Student("赵一",13,93);
Student s3 = new Student("钱二",12,97);
Student s4 = new Student("张三",14,97);
Student s5 = new Student("李四",14,96);

ll.add(s1);
ll.add(s2);
ll.add(s3);
ll.add(s4);
ll.add(s2);
ll.add(s5);
System.out.println(ll.size());//6
```

### 删除

```java
//删除
ll.remove();//删除first元素
/*
ll.remove(2);
ll.remove(s1);
*/
```

### 查找

```java

//查找
System.out.println(
        "=============查找=============="
);

System.out.print(ll.indexOf(s2)+":");
System.out.println(s2);
```

### 切片

``` java
//切片
System.out.println(
        "=============切片=============="
);
System.out.println(ll.subList(1, 3));
```

### 遍历

#### foi和foreach遍历

```java
    //fori
System.out.println(
        "================fori================="
);

for (int i = 0; i < ll.size(); i++) {
    System.out.println(ll.get(i));
}

    //foreach
System.out.println(
        "================foreach================="
);

for (Object object:
     ll) {
    Student student = (Student) object;
    System.out.print(ll.indexOf(student)+":");
    System.out.println(student);
}

```

#### 迭代器遍历

```java
    //迭代器
System.out.println(
        "================listIterator============"
);

Iterator it = ll.iterator();//无参

while (it.hasNext()) {
    System.out.println(
            it.next().toString()
    );
}

System.out.println(
        "============listIterator============="
);

ListIterator lit = ll.listIterator(ll.size());//可有参,可无参

while (lit.hasPrevious()) {//逆序
    System.out.println(lit.previous());
}
```

### 判断

```java
//判断
System.out.println(ll.contains(s1));//true
System.out.println(ll.isEmpty());//false
```

## 源码分析

implements List<E>

### 属性

```java
transient int size = 0;
transient Node<E> first;//头节点指针
transient Node<E> last;//尾节点指针
```

### 方法

....以后再看


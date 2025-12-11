# 类型通配符

- 一般是使用"?"代替具体的类型实参
- 类型通配符是类型实参,而不是类型形参

## 问题引入:

```java
package Generic;

public class Demo {
    public static void main(String[] args) {
        Person<Number> person1 = new Person<>();
        person1.setT(250.21);
        showPerson(person1);

        Person<Integer> person2 = new Person<>();//Integer extends Number,但是在这里适用吗?
        person2.setT(250);										//↓
        showPerson(person2);//编译时异常,类型不兼容				    ↓
    }															//↓
    															//↓
    public static void showPerson(Person<Number> person) {//这里也不能用<Object>
        Number t = person.getT();
        System.out.println(t.toString());
    }

}
```

```java
package Generic;

public class Demo {
    public static void main(String[] args) {
        Person<Number> person1 = new Person<>();
        person1.setT(250.21);
        showPerson(person1);//编译时异常

        Person<Integer> person2 = new Person<>();//Integer extends Number extends Object
        person2.setT(250);
        showPerson(person2);//编译时异常
    }

    public static void showPerson(Person<Object> person) {
        Object t = person.getT();
        System.out.println(t.toString());
    }

}
```

### 尝试用方法重载解决

要怎么解决这个异常?想到方法重载:

重载showPerson()方法

```java
public static void showPerson(Person<Integer> person) {
        Integer t = person.getT();
        System.out.println(t.toString());
}
//这可以重载吗
```

不能重载

```java
public static void showPerson(Person<Number> person)
```

这一条会编译时异常:冲突,两个方法具有相同的**擦除**

### 解决方法

```java
public static void showPerson(Person<?> person) {
    Object t = person.getT();//只能用Object
    System.out.println(t.toString());
}
```

\

```java
public static <T> void showPerson(Person<T> person) {
    T t = person.getT();
    System.out.println(t.toString());
}
```

## 类型通倍符上限

### 语法:

``` java
类/接口 <? extends 实参类型>
类/接口 <T extends 实参类型>
```

?(泛型的类型)  只能是实参类型的子类型(,孙爷类型)直至实参类型

```java
public static void showPerson(ArrayList<? extends Person> arrayList) {
```

### 使用示例

#### 使用示例一

```java
public static void main(String[] args) {
    //Person
    ArrayList<Person> personArrayList=new ArrayList<>();
    showPerson(personArrayList);

   //Person之子
    ArrayList<Student> studentArrayList=new ArrayList<>();
    showPerson(studentArrayList);

    //Person之父
    ArrayList<Object> objectArrayList=new ArrayList<>();
    showPerson(objectArrayList);
    //编译时异常
}

public static void showPerson(ArrayList<? extends Person> arrayList) {
    System.out.println(arrayList.toString());
}
```

#### 使用示例二

```java
public static void main(String[] args) {
    //Person
    ArrayList<Person> personArrayList=new ArrayList<>();
    personArrayList.addAll(personArrayList);

    //Person之子
    ArrayList<Student> studentArrayList=new ArrayList<>();
    personArrayList.addAll(studentArrayList);

    //Person之父
    ArrayList<Object> objectArrayList=new ArrayList<>();
    personArrayList.addAll(objectArrayList);//编译时异常

}
```

**看看addAll()的源码**

```java
public boolean addAll(Collection<? extends E> c) {...}
```

### 注意

在定义了通配符上限的方法内无关父子类,都会编译时异常

```java
public static void showPerson(ArrayList<? extends Person> arrayList) {
    arrayList.add(new Person());
    //编译时异常
    arrayList.add(new Student());//Person之子
    //编译时异常
    arrayList.add(new Object());//Person之父
    //编译时异常

    System.out.println(arrayList.toString());
}
```

**因为不知道传进来的arraylist的<>内的类是啥**

## 类型通倍符下线限

### 语法

```java
类/接口<? super 实参类型>
类/接口<T super 实参类型>
```

?(泛型的类型)  只能是实参类型的父类型(,爷类型)直至实参类型

```java
public static void showPerson(ArrayList<? super Person> arrayList)
```

### 使用示例

```java
public static void main(String[] args) {
    //Person
    ArrayList<Person> personArrayList=new ArrayList<>();
    showPerson(personArrayList);

    //Person之子
    ArrayList<Student> studentArrayList=new ArrayList<>();
    showPerson(studentArrayList);
    //编译时异常

    //Person之父
    ArrayList<Object> objectArrayList=new ArrayList<>();
    showPerson(objectArrayList);

}

public static void showPerson(ArrayList<? super Person> arrayList) {//定义下线是Person
    System.out.println(arrayList.toString());
}
```

### 注意

```java
public static void showPerson(ArrayList<? super Person> arrayList) {
    arrayList.add(new Person());
    //不再报错
    arrayList.add(new Student());//Person之子
    //不再报错
    //arrayList.add(new Object());//Person之父
    //编译时异常

    System.out.println(arrayList.toString());
}
```

### 下限在源码用的应用非常广泛

- 为什么这么在设计呢?
  - 我们在去构建子类对象,
    - 必然先构建父类对象,
    - 此时父类成员已经被初始化了,
    - 所以用父类的compare是合理的
  - 我们在去构建夫类对象,
    - 子类对象还未被构建

```java
public class Demo {
    public static void main(String[] args) {
        //TreeSet的构造方法之一:
        //public TreeSet(Comparator<? super E> comparator){...}
        //使用了下限,E即:
        //public class TreeSet<E> extends AbstractSet<E>
        //也就是说只能用Person的父类,子类不能用

        TreeSet<Person> persons = new TreeSet<>(new MyComparator1());
        persons.add(new Person("A",41));
        {
        persons.add(new Person("B",24));
        persons.add(new Person("B",43));
        persons.add(new Person("C",32));
        persons.add(new Person("B",45));
        persons.add(new Person("C",35));
        persons.add(new Person("A",43));
        persons.add(new Person("A",33));
        persons.add(new Person("B",34));
        persons.add(new Person("C",42));
        }//如此往复//远不止十
        System.out.println(persons.size());//3?实现compare的时候顺带的让他认为"凡name一致者皆为同一物"
        persons.forEach(System.out::println);//发现了好东西
        TreeSet<Person> persons2 = new TreeSet<>(new MyComparator2());//编译时异常,空引用

        TreeSet<Student> students = new TreeSet<>(new MyComparator2());
        students.add(new Student("A",41,97));
        {
            students.add(new Student("B",24,95));
            students.add(new Student("B",43,96));
            students.add(new Student("C",32,92));
            students.add(new Student("B",45,93));
            students.add(new Student("C",35,95));
            students.add(new Student("A",43,92));
            students.add(new Student("A",33,90));
            students.add(new Student("B",34,96));
            students.add(new Student("C",42,94));
        }//如此往复,远不止十
        System.out.println(students.size());//7?实现compare的时候顺带的让他认为"凡score一致者皆为同一物"
        students.forEach(System.out::println);
    }
}
class MyComparator1 implements Comparator<Person>{
    @Override
    public int compare(Person o1, Person o2) {
        return o1.name.compareTo(o2.name);
    }
}
class MyComparator2 implements Comparator<Student>{
    @Override
    public int compare(Student o1, Student o2) {
        return o1.age-o2.age;
    }

}
```


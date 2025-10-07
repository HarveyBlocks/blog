# 访问者

封装作用于某种数据结构的操作

在不改变数据结构的前提下定义作用于元素的操作



不改变对象结构的元素的情况下, 可以对对象结构中的元素增加新的功能,扩展性好

复用性好, 访问者作为参数可以被任意选择, 对象结构可以方便地实现对元素行为逻辑的调用

分离无关行为, 把相关的行为封装在一起构成一个访问者, 这样每个访问者的行为都比较单一



## 结构

-   抽象访问者
    -   Visitor
    -   定义对每一个元素访问的行为
    -   参数是可以访问的元素
-   具体访问者
    -   Concrete Visitor
    -   多种访问者都有访问元素的权限(继承), 只是访问的逻辑不一样(多态)
    -   Visitor的成员方法将作为Concrete Element参数以实现**分派**
-   抽象元素
    -   Element
    -   定义接收者的访问者的`accept`方法, 意指**任何一种元素都应该能够被访问者访问**
    -   Element的成员方法将作为Visiter参数 
-   具体元素
    -   ConcreteElement
    -   对于每一种具体元素, 访问者都应该有针对这一具体元素的应对行为实现
    -   由具体元素的成员调用Visiter的以ConcreteElement作为参数的方法, 将本身作为参数传入方法, 实现**分派**
-   对象结构
    -   Object Structure
    -   含有Element的聚合, 可以迭代这些元素, 供访问者访问

## 缺点

对象结构改变困难, 增加具体元素类, 要在每一个具体访问者类中增加新的具体操作, 未被开闭原则

访问者角色依赖具体类, 而不是抽象类, 违反了依赖倒转原则

## 使用原则

-   对象结构相对稳定, 但操作算法经常变动
-   对象结构中的对象需要提供多种不同且不想关的操作, 而且要避免让这些操作的变化影响对象的结构

## 实现

### Element

```java
public interface Element {
    void showMsg();
}
```

### ConcreteElement

```java
public class ElementImpl1 implements Element {
    @Override
    public void showMsg() {
        visitor.visit(this);
        System.out.println("Here is the Element1");
    }
}
```

```java
public class ElementImpl1 implements Element {
    @Override
    public void showMsg(Visitor visitor) {
        visitor.visit(this);
        System.out.println("Here is the Element2");
    }
}
```



### Visitor

```java
public interface Visitor {
    void visit(ElementImpl1 element);
    void visit(ElementImpl2 element);
}
```



###ConcreteVisitor

```java
public class VisitorImpl implements Visitor {
    @Override
    public void visit(ElementImpl1 element) {
        System.out.println("visit ElementImpl1");
    }
    @Override
    public void visit(ElementImpl2 element) {
        System.out.println("visit ElementImpl2");
    }
}
```



### Object Structure

```java
public class ObjectStructure {
    private final List<Element> elements = new ArrayList<>();

    public void add(Element element) {
        elements.add(element);
    }

    public void action(Visitor visitor) {
        for (Element element : elements) {
            element.showMsg(visitor)
        }
    }
}
```



### Demo

```java
ObjectStructure structure = new ObjectStructure();
// 组成结构聚合
structure.add(new ElementImpl1());
structure.add(new ElementImpl2());
structure.add(new ElementImpl1());
structure.add(new ElementImpl2());
structure.add(new ElementImpl1());
// 访问
structure.action(new VisitorImpl());
```

## 双分派

### 分派

>   Dispatch

变量被声明时的类型叫做变量的静态类型, 又称明显类型

变量所引用的对象的真实类型称为实际类型

根据对象的类型而对方法进行选择, 就称为分派

-   静态分派

    -   Static Dispatch
    -   编译时期, 根据静态类型信息发生, 方法重载就是静态分派

-   动态分派

    -   Dymanic Dispatch
    -   运行时期, Java通过方法的重写支持动态分派

-   静态分派的优先级比动态分派高

    ```java
    public static void show(Person person){
        System.out.println("Person");
    }
    public static void show(Student student)
        System.out.println("student");
    }
    public static void show(Teacher teacher){
        System.out.println("teacher");
    }
    public static void demo(){
        Person p = new Person();
        Person s = new Student();
        Person t = new Teacher();
        show(p); // Person
        show(s); // Person
        show(t); // Person
    }
    ```



### 双分派

在选择方法时,  要同时依据消息接受者(Reciver)的运行时状态区分, 还要依据参数的运行时状态区分

表现为

```java
interface ObjectA{
    void show(Executor exe);
}
class ObjectX extends ObjectA{
    public void show(Executor exe){
        // 第一次分派, 此刻exec的参数的类型实质上是明确的, 利用多态被强行明确了
        exe.exec(this);
    }
}

class ObjectY extends ObjectA{
    public void show(Executor exe){
        exe.exec(this);
    }
}

class ObjectZ extends ObjectA{
    public void show(Executor exe){
        exe.exec(this);
    }
}

class Executor(){
    public void exec(ObjectA a){
        System.out.println("A");
    } 
    public void exec(ObjectX x){
        // 参数类型被明确之后, 就可以进入对应的方法了
        System.out.println("X");
    } 
    public void exec(ObjectY y){
        System.out.println("Y");
    } 
    public void exec(ObjectZ z){
        System.out.println("Z");
    } 
}
clas Demo{
    public void demo(){
        ObjectA x = new ObjectX();
        ObjectA y = new ObjectY();
        ObjectA z = new ObjectZ();
        Executor exe = new Executor();
        x.show(exe);
        y.show(exe);
        z.show(exe);
    }
}
```


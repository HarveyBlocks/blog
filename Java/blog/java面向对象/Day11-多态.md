# 多态

- 多态对象除了重写的方法 其他和父类对象完全相同

- 表现为**对象多态**和**行为多态**
- 多态是对象,行为的多态属性不谈多态

```java
People p1 = new Student();//对象多态
p1.run()//行为多态
People p2 = new Teacher();//对象多态
p2.run()//行为多态
```

对象多态:People可以是Student,也可以是Teacher

行为多态:Teacher和Student的run()可能不一样

行为多态

- 针对子类有不同特点
- 编译看左:父类有方法就不报错
- 运行看右:子类重写方法就运行子类方法

​		

## 作用

- 动态编译，可拓展性(解耦合:电池不好了,可以换电池;轮胎不好了,可以换轮胎).
  - 例如:

```java
Father f = new Son();//突然想到是Daughter,就可以改,方便.否则要全动一遍
System.out.println(f);
System.out.println(f);
System.out.println(f);
System.out.println(f);
System.out.println(f);
```

- 方法的参数用父类,也可以接收一切子类对象

  - 例如:

  ```java
  package LearnOOP;
  public class Test {
      public static void main(String[] args) {
          Father f = new Father();
          go(f);
          Father s = new Son();
          go(s);
          Father d = new Daughter();
          go(d);        
      }
      public static void go(Father father){}
  }
  ```

- 同一方法，根据发送对象不同而采取多种不同的行为方式

- 一个对象的实际类型是确定的，但可以指定对象的引用类型有很多

ps:解耦合-紧耦合(轮胎和车焊死)

## 存在条件

1.  继承关系
2. 子类重写父类方法,行为多态一定要重写方法
3. 父类引用指向子类对象

``` java
Object object=new Human();//父类引用指向子类对象
```

![image-20230809191429158](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java面向对象/Day11-多态/image-20230809191429158.png)

如果子类重写了父类的方法，就调用子类的

如果子类没有重写父类的方法，就调用父类的

父类可以指向子类，但不能调用子类独有的方法

### 注意

多态是方法的多态，属性没有多态

![image-20230902192809583](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java面向对象/Day11-多态/image-20230902192809583.png)

## 缺点及其解决

- 多态不能使用子类独有的方法

为什么不能呢?因为编译看左边


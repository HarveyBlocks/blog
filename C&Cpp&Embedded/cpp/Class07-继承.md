# 继承

或称之为派生

从父派生子

子继承父

cpp支持多继承

```cpp
class 子类名:继承方式 父类{
    
};
```

## 继承方式

-   共有继承(`public`)
    -   在类内部, 父类的`public`成员都以`public`的身份出现在子类中
    -   在类内部, 父类的`protected`成员`protected`的身份出现在子类中
    -   在类内部, 父类的`private`成员不能直接访问
    -   在类内部, 子类中的成员函数都可以直接访问父类中的`public`和`protected`成员
    -   子类创建出来的对象能直接访问父类中的`public`成员
    -   子类创建出来的对象不能直接访问父类中的`protected`成员
-   保护继承`protected`
    -   在类内部, 父类的`public`和`protected`成员都以`protected`的身份出现在子类中
    -   在类内部, 父类的`private`成员不能直接访问
    -   在类内部, 子类中的成员函数都可以直接访问父类中的`public`和`protected`成员
    -   子类创建出来的对象不能直接访问父类中的任何成员
-   私有继承(`private`)
    -   在类内部, 父类的`public`和`protected`成员都以`private`的身份出现在子类中
    -   在类内部, 父类的`private`成员不能直接访问
    -   在类内部, 子类中的成员函数都可以直接访问父类中的`public`和`protected`成员
    -   子类创建出来的对象不能直接访问父类中的任何成员

##代码示例

```cpp
#include<iostream>
using namespace std;
class Person {
	private:
		int name;
	protected:
		int age;
	public:
		int legNum = 4; // 四条腿
		void say() {
			cout << "我是人我在说话" << endl;
		}
};
class Student: public Person {
	protected:
		void play() {
			cout<<"我是学生我在玩乐"<<endl;
			cout << "我" << age << "岁" << endl;
			cout << "我有" << legNum << "条腿" << endl;
			say();
		}
	public:
		void say(){
			cout<<"我是学生我在说话"<<endl;
		}
		void study() {
			cout<<"我是学生我在学习"<<endl;
			// cout<<"我叫"<<name<<endl; //scope错误
			cout << "我" << age << "岁" << endl;
			cout << "我有" << legNum << "条腿" << endl;
			say();
		}
};

class Teacher: protected Person {
	public:
		void teach() {
			cout<<"我是教师我在教书"<<endl;
			// cout<<"我叫"<<name<<endl; //scope错误
			cout << "我" << age << "岁" << endl;
			cout << "我有" << legNum << "条腿" << endl;
			say();
		}
};

class Worker: private Person {
	public:
		void work() {
			cout<<"我是打工人我在拉磨"<<endl;
			// cout<<"我叫"<<name<<endl; //scope错误
			cout << "我" << age << "岁" << endl;
			cout << "我有" << legNum << "条腿" << endl;
			say();
		}
};

int main() {
	Person person;
	Student student;
	Teacher teacher;
	Worker worker;
	person.say();
	student.say();
	student.study();
	student.legNum;;
	// teacher.say();
	teacher.teach();
	// worker.say();
	worker.work();
	return 0;
}

```



## 类型兼容规则

在继承规则为**公有**时, 有: 

-   一个**公有**子类的对象在使用上可以被当作父类的对象，**反之则禁止**
    -   子类的对象可以被赋值给父类对象。
    -   子类的对象可以初始化父类的引用。
    -   指向父类的指针也可以指向子类对象。
-   通过父类对象名、指针只能使用从父类继承的成员

当继承规则为`protect` 或`previte`, 则该规则失效, 因为父类的成员私有继承给子类之后, 将成为私有成员, 就不能从的子类私有到父类的私有进行转换

父类类型指向子类对象

```Cpp
#include<iostream>

using namespace std;

class Vehicle {
private:
    int price;
public:
    explicit Vehicle(int price) : price(price) {}

    Vehicle() {}

    int getPrice() const {
        return price;
    }

    void setPrice(int price) {
        this->price = price;
    }
    void run(){
        cout<<"交通工具运行"<<endl;
    }
};

class Car : public Vehicle {
private:
    int capacity; // 可以坐的人的个数
public:
    Car(int price, int capacity) : Vehicle(price), capacity(capacity) {}

    Car() {}

    explicit Car(int price) : Vehicle(price) {}

    void run(){
        cout<<"车运行"<<endl;
    }

    int getCapacity() const {
        return capacity;
    }

    void setCapacity(int capacity) {
        this->capacity = capacity;
    }
};

int main() {
    Vehicle* vehicle = new Car(120000);
    vehicle->run(); // 交通工具运行
}
```

但调用了父类成员方法? 

```Cpp
#include<iostream>

using namespace std;

class Vehicle {
public:
    void run(){
        cout<<"交通工具运行"<<endl;
    }
};

class Car : public Vehicle {
public:
    void run(){
        cout<<"车运行"<<endl;
    }
};


int main() {
    Vehicle* car = new Car();
    car->run(); // 交通工具运行
    car->Vehicle::run(); // 交通工具运行
}
```

Why?

其实是有了两个同名函数, 有两个`run`方法, 这时候, 将输出指针类型所对应类型的`run`成员方法

```cpp
#include<iostream>

using namespace std;

class Vehicle {
public:
    // 设置成虚函数
    virtual void run(){
        cout<<"交通工具运行"<<endl;
    }
};

class Car : public Vehicle {
public:
    void run(){
        cout<<"车运行"<<endl;
    }
};


int main() {
    Vehicle* car = new Car();
    car->run(); // 交通工具运行
    car->Vehicle::run(); // 交通工具运行
}
```

这样就真的可以父类类型指向子类对象, 调用子类成员啦

## 继承的关系

-   单继承
    -   子类从一个父类继承
-   多继承
    -   子类从多个父类继承
-   多重派生
    -   由一个父类派生处多个子类
-   多层派生
    -   子类又作为父类, 继续派生子类

```cpp
class Car : public Vehicle,private Mechine {
private:
    int capacity; // 可以坐的人的个数
public:
    // ...
};
```

### 多继承的问题-二义性

不同父类有同名的成员, 到时候调用哪个成员? 不知道

直接调用, 编译异常



-   在多继承时，父类之间出现同名成员时，将出现访问时的二义性（不确定性）

    用类名限定，或同名隐藏规则来解决。

-   当子类从多个父类派生，而这些父类又从同一个父类派生，则在访问此共同父类中的成员时，将产生二义性

    ![image-20240410153224920](../../java/微服务和分布式/RPC/assets/Day05-%E7%BB%A7%E6%89%BF/image-20240410153224920.png)

    采用虚基类来解决。

    B中的成员在C中算俩份

-   解决方法一：用类名来限定

    ```Cpp
    PublicCar* pPublicCar = new PublicCar();
    ```

    ```Cpp
    pPublicCar->Vehicle::getPrice();
    ```

    或

    ```Cpp
    pPublicCar->Vehicle2::getPrice();
    ```

-   解决方法二：同名隐藏

    在C 中声明一个同名成员函数f()，在f()中根据需要调用

    `父类A::f()`

    或

    `父类B::f()`

#### 虚基类

用于有共同基类的场合

-   声明

    以virtual修饰说明基类

    ```Cpp
    class B1:virtual public B
    ```

-   作用

    主要用来解决多继承时可能发生的对同一基类继承多次而产生的二义性问题.

    为最远的派生类提供惟一的基类成员，而不重复产生多次拷贝

注意：
**在第一级继承时就要将共同父类设计为虚基类。**



```cpp
#include<iostream>

using namespace std;

class B{
public:
    string field;
    explicit B(const string& field) : field(field) {}
    explicit B(){}
};
class B1 :virtual public B{
public:
    B1() :B("B1更改B的field"){
        this->field="B1更改B1的field";
    }
};
class B2 :virtual public B{
public:
    B2() :B("B2更改B的field"){
        this->field="B2更改B2的field";
    }
};
class C :  public B1, public B2{
public:
    void get(){
        cout<<"B1::field="<<this->B1::field<<endl;
        cout<<"B2::field="<<this->B2::field<<endl;
        cout<<"B::field="<<this->B::field<<endl;
    }
};

int main() {
    C* c = new C();
    c->get();
}
```

-   B1, B2都继承虚基类
    -   只有一个Filed, 其值为在构建C时最后一个被构建的父类B2决定
    -   而构建B2时, 有最后构建的在B2的构造函数的函数体内的赋值决定
    -   所以三个输出都是`B2更改B2的field`
-   B1不再继承虚基类
    -   想在C调用`this->B::field`编译直接报错, 没有`B::field`了, 两个父类存在各自的field, 而自己的field给了B2
    -   `B1::field`的值是`B1更改B1的field`
    -   `B2::field`的值是`B2更改B2的field`
-   B2不再继承虚基类
    -   想在C调用`this->B::field`编译直接报错, 没有`B::field`了
    -   `B1::field`的值是`B1更改B1的field`
    -   `B2::field`的值是`B2更改B2的field`
-   B1, B2都不继承虚基类
    -   想在C调用`this->B::field`编译直接报错, 没有`B::field`了
    -   `B1::field`的值是`B1更改B1的field`
    -   `B2::field`的值是`B2更改B2的field`



## 类的构建

对子类的构建必须先经过父类的构建, 否则子类构造不出来

构建方式是通过构造器

子类的构造器需要对父类的构造器进行调用并构造

如果父类有无参构造, 就不用调用父类的构造函数来显式构造父类

### 构造函数的调用顺序

调用顺序不看子类构造函数的调用顺序

1.  调用父类构造函数

    调用顺序按照它们被**继承时声明**的顺序

    ```cpp
    class Son: public Father, public Mother{ // 构造顺序看这里
    	public:
        	Son():Father(),Mother(){}//和这里无关
        // 构造父类时, 需要使用↑这种方法, 在函数体外声明, 如果要调用本类的其他构造函数帮助构造, 也要放在外面才能生效! 
        	
    };
    ```

    有默认拷贝构造函数的, 在其他父类构造完之后构造

2.  调用成员对象的构造函数，调用顺序按照它们在类中声明的顺序

    有默认拷贝构造函数的, 在其他成员对象构造完之后构造

3.  本子类中的辅助构造的构造函数

4.  子类的构造函数体中的内容

### 拷贝构造

```
若建立子类对象时调用默认拷贝构造函数，则编译器将自动调用基类的默认拷贝构造函数。
若编写派生类的拷贝构造函数，则需要为基类相应的拷贝构造函数传递参数。例如:
C::C(C &c1):B(c1){…}

```

### 析构函数

```
析构函数也不被继承，派生类自行声明
声明方法与一般（无继承关系时）类的析构函数相同。
不需要显式地调用基类的析构函数，系统会自动隐式调用。
析构函数的调用次序与构造函数相反。
```

## 虚基类

对于菱形继承

###构造器

-   作用

    -   **防止对顶端父对象的构造函数的重复创建**

-   构造器构建时机与构建角色

    -   调用顶端父类的构造器的职责交由最末端的子类, 
    -   建立对象时所指定的类称为**最远派生类**。
    -   虚基类不调用父类的构造函数, 而是最末端子类调用
    -   该派生类的**其他父类**对虚基类构造函数的调用**被忽略**。

-   要求

    -   顶端父类没有无参构造函数, 虚基类也是需要写父类的构造函数的

    -   在整个继承结构中，**直接或间接继承虚基类的所有派生类**

        都必须在构造函数的成员初始化表中给出对虚基类的构造函数的调用。

    -   如果未列出，则表示调用该虚基类的默认构造函数。

### 成员

顶端父类的成员只会构造一份



## 类型转换

兄弟, 叔侄不能转换

父子可以转换

```cpp
int main(){
    Person* person = new Student(114514,12);

    person->say();
    person->eat(10);

    // Student * student = (Student*) person; 也可
    auto * student = (Student*) person; // 当不知道父类指向的实体类型, 这种转换存在问题
    student->say();
    student->eat(100);

    Student  stu = *student;
    stu.say();
    stu.eat(1000);


   //  Student  s = (Student) ( *person); 编译错误

}
```

### 强制类型转换方法

| 类型强制转换工具                        |
| --------------------------------------- |
| static_cast<new_type> (expression)      |
| dynamic_cast<new_type> (expression)     |
| const_cast<new_type> (expression)       |
| reinterpret_cast<new_type> (expression) |

-   `new_type` 目标类型
-   `expression` 源类型, 即被转换的类型



附: 异常`bad_cast`, 有`try-catch`



####`static_cast`

>   静态转化



```
static_cast<new_type> (expression)
```

-   用于类层次结构中父类和子类之间指针或引用的转换。

-   进行上行转换（把子类的指针或引用转换成父类表示）是安全的；

-   进行下行转换时，由于没有**动态类型检查**，所以是不安全的。

-   用于**基本数据类型之间的转换**，如把char转换成int。

    ```cpp
    char a = 'a'; 
    int b = static_cast<int>(a);
    //正确，将char型数据转换成int型数据 
    ```

-   把空指针转换成目标类型的空指针。

    ```cpp
    double *c = new double; 
    void *d = static_cast<void*>(c);
    //正确，将double指针转换成void指针 
    ```

    

-   把任何类型的表达式转换成void类型。

-   注意：能增加`const`这种buff, 但**不能去掉expression的const、volatile等属性**。

    ```cpp
    int e = 10;
    const int f = static_cast<const int>(e);
    //正确，将int型数据转换成const int型数据 
    
    const int g = 20; 
    int *h = static_cast<int*>(&g); 
    //编译错误，static_cast不能转换掉g的const属性
    ```

    



####`dynamic_cast`

>   能类型检查的类型转换

-   主要用于父类与子类之间的上行转换和下行转换。
-   **具有类型检查的功能**，比static_cast更安全。
-   源类型应是目标类型的**公有子类**、或是目标类型的**公有父类**（要求基类中要有虚函数）、或与目标类型相同。
-   如果转换的类型是指针类型并且失败了，则返回0。
-   转换转换的类型是引用类型并且失败了，则将抛出一个**std::bad_cast**异常。

```cpp
void f(const Base &b)
{ 
   try{
      const Derived &d = dynamic_cast<const Derived &>(b); 
      //使用b引用的Derived对象 
   } catch(std::bad_cast){ 
       //处理类型转换失败的情况 
   } 
}

```



####`const_cast`



-   用于修改类型的const或volatile属性。
-   源类型与目标类型一致。
-   不能改变源类型的基类型。

```cpp
const int g = 20;
int* h1 = const_cast<int*>(&g);//去掉const常量的const属性 

const int &g2 = g;
int& h2 = const_cast<int&>(g2);//去掉const引用的const属性 

const char* g3 = "hello";
char* h3 = const_cast<char*>(g3);//去掉const指针的const属性

```

####`reinterpret_cast`

啥都能转, 无关也能转, 略

### 重写类型转换运算符

```cpp
class Score {
    double math;
    double eng;
public:
    Score(double math, double eng):math(math),eng(eng){};
    operator double (){
        return this->eng+this->math;
    }
};

int main(){
    cout<<(double) Score(99.4,95.2)<<endl;
}
```

## `typeid`运算符

>   **RTTI**，即运行时类型识别，是**C++**的新特性，支持通过基类指针或引用，判别其指向的对象的真实类型

类型: 

```cpp
class Base {
};

class Derived : public Base {
};

```



```cpp
int main() {
    // Base对象
    Base b;
    cout << "b type : " << typeid(b).name() << endl; // b type : 4Base
    // Derived对象
    Derived d;
    cout << "d type : " << typeid(d).name() << endl; // d type : 7Derived
    // Base 指针
    Base *pb = new Base();
    cout << "pb type : " << typeid(pb).name() << endl; // pb type : P4Base
    // Base 指针指向Derived对象
    Base *pd = new Derived();
    cout << "pd type : " << typeid(pd).name() << endl; // pd type : P4Base
    cout << "*pd type : " << typeid(*pd).name() << endl;// *pd type : 4Base
    // 引用类型的转换
    Base &rb = b;
    Base &rd = d;
    cout << "rb type : " << typeid(rb).name() << endl; // rb type : 4Base
    cout << "rd type : " << typeid(rd).name() << endl;// rd type : 4Base
}
```

当父类中**不含有虚函数**时，通过指向基类的指针或引用，得到的是父类类型（静态类型），而不是真实类型（动态类型）。

```cpp
class Base { public:virtual ~Base() {} };


class Derived : public Base {
};


int main() {
    // Derived对象
    Derived d;
    // Base 指针指向Derived对象
    Base *pd = new Derived();
    cout << "pd type : " << typeid(pd).name() << endl; // pd type : P4Base
    cout << "*pd type : " << typeid(*pd).name() << endl;// *pd type : 7Derived 正确指向了对象
    Base &rd = d;
    cout << "rd type : " << typeid(rd).name() << endl;// rd type : 7Derived
}
```


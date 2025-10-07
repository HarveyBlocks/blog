# 多态

同一个类中, 由于继承的缘故, 不可避免地产生对同名同参数列表的函数

##重载

同一个类中, 有多个同名的函数, 但参数列表不一样, 这叫函数重载

### 重载运算符

不能重载`.` , `.*`, `::`,`?:`

不能创造新的运算符

不能改变优先级和结合性

不能改变操作数个数



###加法

```cpp
class Vehicle {
private:
    int price;
public:
    Vehicle(int price) : price(price) {}
    Vehicle operator+(Vehicle &vehicle) const {// 参数个数是操作数的个数
        return Vehicle(this->price + vehicle.price); // 好吧, 值传递, 拷贝构造
    }
};
```

### 无交换律的减法

```Cpp
class Vehicle {
private:
    int price;
public:
    Vehicle(int price) : price(price) {}

    Vehicle operator-(Vehicle &vehicle) const {
        return Vehicle(this->price - vehicle.price);
    }

};


int main() {
    Vehicle *vehicle1 = new Vehicle(1);
    Vehicle *vehicle2 = new Vehicle(2);
    cout << (*vehicle1) - (*vehicle2) << endl;// 这种有运算顺序的, this,是前面的哪一个

}
```







### 一元, 自增自减

```cpp
class Vehicle {
private:
    int price;
public:
    explicit Vehicle(int price) : price(price) {
    }

    Vehicle operator-(Vehicle &vehicle) const {
        return Vehicle(this->price - vehicle.price); // 好吧, 值传递, 拷贝构造
    }

    Vehicle operator++(int ignore) { // 没有a++++这种东西, 用参数表来表示区别, 不参与运算
        cout << "do a++" << endl;
        Vehicle result = Vehicle(*this);
        this->price++;
        return result; // 调用拷贝构造
    }

    Vehicle& operator++() { // 为应对形如++++a的情况, 返回引用对象
        cout << "do ++a" << endl;
        this->price++;
        return *this;
    }

    int get() {
        return this->price;
    }
};
```



## 静态绑定与动态绑定

### 静态绑定

父类指针指向子类对象

调用父类和子类的同名成员, 编译器对调用哪个成员造成困惑

如果啥都不做, 就会调用父类的成员, 即 **静态绑定**

如果使用**重写**., 父类指针指向子类对象, 调用子类对象的成员, 即**动态绑定**

```cpp
// 父子之间存在同名函数(同名, 而不是同方法签名, 参数列表不同)
// 那么不能构成多态, 用子类访问时, 将覆盖父类方法
// 要访问父类需要用子类指向自己的父类的部分再进行访问被覆盖的函数
// son.Father::show();
// 标注为虚函数也没用
```

### 重写/动态绑定



在父类成员声明时加上virtual, 表示可以重写, 采用子类的成员

**虚函数具有继承性, 继承的方法无论有没有声明virtual, 都会被认定为虚函数**

```C
#include<iostream>

using namespace std;

class Vehicle {
public:
    // 设置成虚函数
    virtual void run(){ // 在声明时加上virtual, 表示可以重写
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

可以父类类型指向子类对象, 调用子类成员啦

## 虚函数

### 虚函数

#### 带默认参数的虚函数

```cpp
class Base {
public:
    virtual void show(int i = 0) {
        cout << "base =" << i << endl;
    }
};

class Derived : public Base {
public:
    virtual void show(int i = 5) {
        cout << "Derived =" << i << endl;
    }
};

int main(void) {
    Base *bp;
    Derived dobj;
    bp = &dobj;
    bp->show(); // Derived = 0
}
```

***==默认参数值采用静态联编, 实现选择子类==***



#### const修饰的虚函数

只有函数头的形式完全一致，才可实现覆盖

### 虚析构

析构函数和成员方法一样, 如果在父类指针指向子类对象的时候, 如果没有使用`virtual`为虚构函数声明, 虚构函数就不会被调用

子类有不同的析构, 虚析构就有必要

```cpp
class Base {
public:
    virtual ~Base(){
        cout<<"Base"<<endl;
    }
};

class Derived : public Base {
public:
    virtual ~Derived(){
        cout<<"Derived"<<endl;
    }
};

int main() {
    Base *bp;
    Derived dobj;
    bp = &dobj;
    delete bp;
    /*
     * Derived
     * Base
     */
}
```

## 抽象类

带有**纯虚函数**的类称为抽象类

抽象类不能声明抽象类的对象



###纯虚函数

没有代码体

在参数表直接`=0`没有完整实现, 没有花括号

```cpp
lass Person {
private:
    int age;
public:
    explicit Person(int age): age(age){}
    virtual void eat(int food)=0; // 纯虚函数
    virtual void say() const ;
};
```

## 抽象类的引用

```cpp
int main(){
    // Person的eat是抽象函数, Student实现了eat
    // Person person = Student(114514,12); 不可
    Person* person = new Student(114514,12);
    person->say();
    person->eat(10);
}
/*
Student 114514 is saying
Student 114514 is eating 10
*/
```




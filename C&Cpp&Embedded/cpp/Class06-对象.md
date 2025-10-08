

## 循环依赖

```cpp
class B; // 1. 提前声明


class A {
		B *b; // 不存储实例, 存储地址, 因为不能实例化
	public:
		A(B *b) :b(b){}
};


class B{
	A *a;
	public:
		B(A *a):a(a){}
};
```



const 修饰类成员字段



const 修饰类成员方法

```Cpp
int get() const;

//...

int get() const{
    // 实现
}
```

-   此方法内部不能更新对象数据成员

-   实现也要带const

-   const关键字修饰的方法**重载时也会被区分**

    ```cpp
    #include<iostream>
    using namespace std;
    
    class Student {
    	private:
    		int age;
    		string name;
    	public:
    		Student(int age = 12, string name = "UnKnow"): age(age), name(name)
    		{} 
    
    		void setAge(int age = 7) { // 关键字传参
    			this->age = age;
    		}
    		int getAge() const {
    			return this->age;
    		}
    
    		int getAge(int offset) const {
    			return this->age + offset;
    		}
    		int getAge()  {// 被调用的那个
    			return this->age-1;
    		}
    
    };
    
    
    int main() {
    	Student s1(12, "nihao");
    	cout << s1.getAge() << " , " << s1.getAge(12) << endl;
    	return 0;
    }
    ```

    

-   **常对象只能调用它的常成员函数**



## 重载

函数名相同, 参数列表不同的一组函数

类型+顺序+个数

关键词传参会和函数重载产生冲突



## static

静态成员被所用对象共享

```Cpp
class Student {
	private:
		static  int grade; // 静态属性存储在另一个地方
	public:
		int getGrade() {
			cout << this->grade << "," << grade << "," << Student::grade<<endl;
			return this->grade;
		}
		static int studentGrade(){
			cout<< grade<<Student::grade<<endl;
			return grade;
		}
		
};
int  Student::grade = 12; // 类外初始化, 别写static!

int main() {
	Student s1;
	cout << s1.getGrade() << endl;
	cout << s1.studentGrade() << endl;
	cout << Student::studentGrade() << endl;
	return 0;
}
```

## 友元

>   一种破坏数据封装的机制

将一个模块声明为另一个模块的友元. 一个模块能够引用到另一个模块中本是被隐藏的信息

可以使用友元函数和友元类

为了确保数据的完整性, 及数据封装与隐藏的原则, 减少使用友元

友元是**单向的**



### 友元函数

-   在类声明中由关键字`friend`修饰的说明的**非成员函数**, 在它的函数体中能够通过对象名访问`private`和`protected`成员
-   作用: 增加灵活性, 在封装和快速性方面做出合理选择
-   访问对象中的成员必须通过对象名

```Cpp
#include<iostream>
using namespace std;


class Student {
	private:
		int age;
	public:
		friend void notFrient(Student& s); // 意义不明
};

void notFrient(Student& s) {
	cout << s.age;
}
int main() {
	Student s1(-12, "你扫i的");
	notFrient(s1);
	return 0;
}
```



```Cpp
class B; // 声明类B，用于让类A使用友元函数

class A {
    private:
    	int data;
	public:
		void friendFunction(B objB); // 友元函数声明
};

class B {
	private:
		int data;
	public:
		B(int d) : data(d) {}

		friend void A::friendFunction(A objA); // 声明类A的友元函数
};

void A::friendFunction(B objB) { // 定义友元函数
	std::cout << "B: " << this.data << std::endl;// this是A的成员
}

int main() {
	B objB(20);
	A objA;
	objA.friendFunction(objB); // 在类A中调用友元函数，访问类B的私有字段

	return 0;
}
```

```Cpp
#include <iostream>
using namespace std;
class B; // 声明类B，用于让类A使用友元函数

class A {
private:
    int data;
public:
    void friendFunction(A objA); // 友元函数声明
};

class B {
private:
    int data;
public:
    B(int d) : data(d) {}

    friend void A::friendFunction(A objA); // 声明类A的友元函数
};

void A::friendFunction(A objA) { // 定义友元函数
    std::cout << "A: " << objA.data << std::endl;
}

int main() {
    B objB(20);
    A objA;
    objA.friendFunction(objA); // 在类A中调用友元函数，访问类A的私有字段, 但函数是在B中定义的

    return 0;
}
```



### 友元类

-   一个类为另一个类的友元, 则类内的所有成员都能访问对方类的私有成员

```Cpp
class A;

class B {
		friend class A;
	private:
		int data;
	public:
		B(int data): data(data) {}
		
};

class A {
	public:
		static void get(B b){
			cout<<b.data<<endl;
		}
};
int main() {
	A::get(B(12));
	return 0;
}
```



## 对象数组

对象数组在创建时需要调用构造函数, 其中每一个成员都需要被实例化

所以, 如果需要一个对象数组的初始化, 就需要一个无参构造方便初始化

## 成员指针

**指向函数成员的指针**

-   声明

    指向字段

    ```cpp
    成员类型 类名::*指针名;
    ```

    指向成员方法

    ```cpp
    返回值类型 (类名::*指针名)(参数列表);
    ```

    

-   初始化

    ```cpp
    指针名=类名::函数成员名；
    ```

    

-   通过对象名（或对象指针）与成员指针结合来访问函数成员

    ```cpp
    (对象名.* 类成员指针名)(参数表)
    ```

    或:

    ```cpp
    (对象指针名—>*类成员指针名)(参数表)
    ```

指向类成员的指针



```c++

class Point {
		int x;
		int y;
	public:
		Point(){};
		Point(int x , int y):x(x),y(y) {

		}
		int getX() {
			return this->x;
		}
		int getY() {
			return this->y;
		}
		double pi = 3.14;
};

int main() {	//主函数
	Point a(4, 5);	//声明对象A
	double Point::*pPi ; // 指向Point的对象的double类型的成员
	pPi = &Point::pi; // pi必须是共有的
    
	Point *p1 = &a;	//声明对象指针并初始化
	//声明成员函数指针并初始化
	
    int (Point::*pGet)() = Point::getX; // 也可以Point::getX
	//（1）使用成员函数指针访问成员函数
	cout << (a.*pGet)() << endl;
	//（2）使用对象指针访问成员函数
	cout << (p1->*pGet)() << endl;
	//（3）使用对象名访问成员函数
	cout << a.getX() << endl;
}

```





## 动态创建对象

`类名 * 对象指针名 = new 类名()`调用构造函数

`delete 对象指针名`**调用析构函数**

不能delete一个int值

```cpp
int a = 1;
int *p = &a;
// delete p;
// free(p); 都不行
```

这种int 会在函数结束后释放

```cpp
int *p = new int(1);
// delete p;
// free(p); 都不行

```

```cpp
int *p = new int(1);
delete p;
std::cout<<p<<std::endl;
std::cout<<*p<<std::endl; // 乱

int *p1 = new int(2);
free(p1);
std::cout<<p1<<std::endl;
std::cout<<*p<<std::endl; // 乱
```

`malloc()`不会调用构造函数和析构函数



### 创建数组

```Cpp
Point *points = new Point[2]; // 要求类有空参构造
```

```c++
delete[] points;  //删除整个对象数组
delete points[1];  //不行
delete[1] points;  //不行
```





### 创建数组类

```cpp
#include<iostream>
using namespace std;
class Point {
	private:
		int x;
		int y;
	public:
		void move(int x, int y) {
			this->x += x;
			this->y += y;
		}
};
class PointArray {
	public:
		PointArray(int n) {
			size = n;
			points = new Point[n];
		}
		~PointArray() {
			cout << "Deleting..." << endl;
			size = 0;
			delete[] points; // 在析构函数中需要释放整个数组的内存
		}
		Point& get(int index) {
			return points[index];
		}
	private:
		Point *points;
		int size;
};

int main() {
	int number;
	cout << "Please enter the number of points:";
	cin >> number;
	//创建对象数组
	PointArray points(number);
	//通过指针访问数组元素的成员
	points.get(0).move(5, 10);
	//通过指针访问数组元素的成员
	points.get(1).move(15, 20);
	return 0;

	return 0;
}

```

### 深拷贝和浅拷贝

对于类

```cpp
class PointArray {
	public:
		PointArray(int n) {
			size = n;
			points = new Point[n];
		}
		~PointArray() {
			cout << "Deleting..." << endl;
			size = 0;
			delete[] points; // 在析构函数中需要释放整个数组的内存
		}
		Point& get(int index) {
			return points[index];
		}
	private:
		Point *points;
		int size;
};

```

我们有字段`*points`是指针类型, 在调用默认拷贝构造函数的时候, 系统只是**简单的把数组指针拷贝到新的对象**, 实际指向的是同一片空间


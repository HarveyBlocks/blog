# Cpp

## namespace与类的创建

```C
#include <iostream>

// namwspace保证同名标识符不被重复
namespace Application {
using namespace std;
class Student
{
    // 缺省是private
    char* name;
    int age=20;
    static int grade;
public:
    void setAge(int aage) {
        age = aage;
    }
    void setName( char *);
    int getAge() {
        return age;
    }
};
// 使用Student::表示对类内部的函数进行操作
// inline 内联函数，没有控制转移的花销，也没有传参
// inline要求不能有循环，选择结构
inline void Student::setName(char * _name) {
    name = _name;
}
void inAndOut() {
    char  name[20];
    cin >> name;
    cout << "Hello!"<<name<<endl;
}

};

using namespace Application;

int main(int argc, char** argv) {
    Student stu; // 自动分配内存
    stu.setAge(12);
    cout << stu.getAge()<< endl;
    Student stu2;
    cout << stu2.getAge()<< endl;
    inAndOut();
    return 0;
}
```

同一个类实例化出来的对象各自占用自己的内存空间

## 对象引用

对象引用就是给一个对象起一个别名的形式, 使用这个对象(浅拷贝)

```C
void fun(string & str){
	printf("%p\n",&str);   // 0000002f305ff8f0 
}
int main(){
    string a = "你好";
    printf("%p\n",&a); // 0000002f305ff8f0
    fun(a);
}
```

使用`类型 & 别名`的形式传参, 不会构建新的对象, 而是直接传递原有的对象

函数传参的时候, 实参传给形参, 都会调用(**浪费时间**)该类型的拷贝构造函数(如果不是基本数据类型的话, 就都会有一个用于**深拷贝**的拷贝构造函数, 见下)并执行, 都会开辟新的内存空间(**浪费内存**)

但是用对象引用, 就不会调用拷贝构造, 就不会开辟内存, 因为只是取了一个别名(引用传递)

### 引用数组

不能声明引用的数组

```cpp
int x = 1, y = 2, z = 3;
int &a[3] = {x,y,z};
```

但能声明数组的引用

```cpp
int arr[] = {1,2,3};
int (&a)[3] = arr;
```

```cpp
int x = 1, y = 2, z = 3;
int arr[] = {x, y, z};
int (&a1)[3] = arr; // 可
// int &a2[3] = arr; // 不可
// int &a3[3] = {x,y,z}; // 不可
// int (&a4)[3] = {x,y,z}; // 不可
```

## 函数的重载

函数名相同, **参数列表不同**, 没有有关返回值的重载

```C
int fun(int a){
	return a;
}

int fun(int a, int b){
	return a+b;
}

```

无论方法还是行为还是构造器, 都可以重载

## 构造函数与实例化对象

### 默认空参构造函数

默认的构造函数都是public

-   空参构造

    ```c
    Student() {
    }
    ```

-   使用空参构造实例化

    ```c
    Student stu;
    // Student stu();  编译器不会检查, 但也不会成功编译, 这是函数声明
    Student stu = Student();
    Student * stu = new Student();
    ```

### 自定义构造函数

自定义构造函数

```C
class Student {
	private:
	public:
		Student() {
			cout << "hi" << endl;
		}
};
```

实例化方法和空参一样

#### 关键字传参与构造函数的简写

```Cpp
class Student {
		int age;
		string name;
	public:
		Student(int age, string name = "UnKnow"): 
			age(age), name(name) {}  // 用于构造函数的方便写法
		int getAge() {
			return this->age;
		}
		void setAge(int age = 7){ // 关键字传参
			this->age = age;
		}
		string getName() {
			return this->name;
		}
};
```

对于使用了关键字传参的构造函数

```Cpp
Student(int _age = 12, string _name = "UnKnow");
```

不能用

```Cpp
Student stu();  
stu.getName(); // 编译不通过, 没有Student()的构造器
```

实例化, 编译异常

### 注意

`Student stu;`和`Student stu();` 完全等价吗?

-   `Student stu;`
    -   声明对象并用无参构造初始化
-   `Student stu();`
    -   声明对象并用无参构造初始化
    -   ==函数的声明==

`Student *stu;`和`Student (*stu)();`和`Student * stu();`

-   `Student *stu;`
    -   指向对象的指针
-   `Student (*stu)();`
    -   返回值是Student的空参的函数指针
-   `Student * stu();`
    -   返回值是Student指针的函数声明

指向对象的指针, 

#### 重载构造函数

函数名相同, 参数列表不同

```C
class Student {
	private:
		int age = 0;
	public:
		Student(int age);
};
inline Student::Student(int _age) {
	age = _age;
	cout << "单参构造函数被调用" << age << endl;
}
```

实例化

```C
// Student stu;
// Student stu = Student(); 不可行了, 
// 有了自定义的重载构造函数, 无参就被覆盖了, 
// 除非再重新自定义个无参构造

Student stu1 = Student(10);
Student stu2(10);
Student * stu3 = new Student(10);
```

当然也可以定义多个参数的

```C
class Student {
	private:
		int age = 0;
		int score;
	public:
		Student(int age,int score);  
		// 在声明处形参名和字段名一致好像没关系
};
inline Student::Student(int _age,int _score) {
	age = _age;
	score = _score;
	cout << "多参构造函数被调用" << age<<","<<score << endl;
}
```

实例化

```C
Student stu1 = Student(10,99);
Student stu2(11,97);
Student * stu3 = new Student(12,98);
```

类中对象, 如果有空参的构造函数, 可以自动创建对象

如果没有空参构造函数, 就会编译报错

```Cpp

class hi {
	public:
		hi(int b) {
			int  a = b;
		}
};

class Student {
	private:
		static int idRecord;
		int id;
		int age;
		string name;
		double score;
		hi s;
	public:
		Student() {

		}
		Student(const string & name, int age, double score) {
			this->name = name;
			this->age = age;
			this->score = score;
			this->id = idRecord++;
		}
} 
```

### 隐式构造函数

-   当**构造函数的参数只有一个时**, 可以把参数的类型**转化为本类的对象**, 以此来实例化对象

    ```Cpp
    class Student {
    	private:
    		int age = 0;
    	public:
    		Student(int _age);
    };
    inline Student::Student(int _age) {
    	age = _age;
    	cout << "单参构造函数被调用" << age << endl;
    }

    int main() {
    	Student stu = 20;
    	return 0;
    }
    ```

-   构造函数的默认类型是**隐式的**

-   原理: 

    1.  编译器会创建一个**临时该类对象**
    2.  将参数传入该**临时对象**的对应构造器
    3.  再把**临时的对象**赋值给我们声明的类

-   取消隐式可以使用`explite`关键字修饰构造器

    ```C
    class Student {
    	private:
    		int age = 0;
    	public:
    		explicit Student(int _age);
    };
    inline Student::Student(int _age) {
    	age = _age;
    	cout << "单参构造函数被调用" << age << endl;
    }

    int main() {
    	// Student stu = 20; 编译不通过
    	return 0;
    }
    ```

-   字符数组能直接赋值给字符串, 也是这个原因罢

    ```C
    string str = "你好";
    ```

***注意!!!***

如果要使用隐式构造函数传入**字符串**

```C
class Student {
	private:
		char * name = NULL;
	public:
		Student(char * name);
};
inline Student::Student(char * _name) {
	name = _name;
	cout << "单参构造函数被调用" << name << endl;
}

int main() {
	Student stu = "Jack"; 
    // [Warning] ISO C++ forbids converting a string constant to 'char*' [-Wwrite-strings]
	return 0;
}
```

或者

```C
class Student {
	private:
		string name = ""; // 初值不能赋值成NULL, 编译器不会检查
	public:
		Student(string name);
};
inline Student::Student(string _name) {
	name = _name;
	cout << "单参构造函数被调用" << name << endl;
}

int main() {
	// Student stu = "Jack"; 会报错!!!
	// [Error] conversion from 'const char [5]' to non-scalar type 'Student' requested
	string name = "Jack";
	Student stu = name;
	return 0;
}
```

### 拷贝构造函数

-   类默认会自带一个拷贝构造函数, 默认如下

通过用构造函数 , 将老对象属性一一赋值给新对象的属性(深拷贝)

默认的是这样: 

```c
class Student {
	private:
		int age = 0;
		int score = 0;
		string name = ""; // 初值不能赋值成NULL, 编译器不会检查
	public:
    	Student(){} // 默认产生的空参构造函数
		Student(const Student& s){
			age = s.age;
			score = s.score;
			name = s.name;
		}// 默认产生的拷贝构造函数
};
```

```c
class Student {
	private:
		int age = 0;
		int score = 0;
		string name = ""; 
	public:
};

int main() {
	Student stu; // 使用缺省的空参构造器
	Student stu1 = Student(stu); // 使用显示的拷贝构造器
	Student stu3(stu); // 使用隐式的构造器
	Student stu2 = stu; // 使用 拷贝构造+隐式构造
	return 0;
}
```

如果一个对象已经完成了创建, **再使用赋值符`=`**的时候, 代表的 **不是使用拷贝构造函数** , 因为不是在构造对象,而只是简单的赋值

```C
int main() {
	Student stu; 
	Student stu1; 
	stu = stu1; // 当然也会一个属性一个属性地赋值
	return 0;
}
```

## 对象的生命周期

存活期

消亡期(函数的结束啊....包括**主函数**和**自定义函数**, 函数内创建的对象, 可以使用消亡期)

将内存回收

将数据成员返回

## 析构函数

**无返回值, 无参数, 类名做函数名**

```C
class Student {
	private:
		...
	public:
		~Student(){
			cout<<"该类型被析构"<<endl;
		}
};
```

对象在消亡的时候**被系统自动调用**

即消亡期将内存回收

**先构造的函数后析构, 后构造的函数先析构**

```C
class Student {
	private:
		int code = 0;
	public:
		Student(int _code){
			code = _code;
			cout<<"对象:`"<<code<<"`被构造"<<endl;
		}
		~Student(){
			cout<<"对象:`"<<code<<"`被析构"<<endl;
		}
};

void fun() {
	cout<<"============fun开始============="<<endl;
	Student stu3(3);
	cout<<"=============fun结束============"<<endl;

}

int main() {
	cout<<"=============main开始============"<<endl;
	Student stu(1);
	cout<<"---------------------------------"<<endl;
	Student stu2(2);
	cout<<"============fun调用============="<<endl;
	fun();
	cout<<"==============main结束==========="<<endl;
	return 0;
	// 函数执行完毕, 再次调用析构函数
}
```

输出结果: 

```text
=============main开始============
对象:`1`被构造
---------------------------------
对象:`2`被构造
============fun调用=============
============fun开始=============
对象:`3`被构造
=============fun结束============
对象:`3`被析构
==============main结束===========
对象:`2`被析构
对象:`1`被析构
```

主动调用析构函数

```C
class Student {
	private:
		int code = 0;
	public:
		Student(int _code){
			code = _code;
		}
		~Student(){
			cout<<"对象:`"<<code<<"`被析构"<<endl;
            // free(this); 不要在析构函数里释放, 因为 释放的过程就在这个对象里, 会导致变空指针
            // 属于是自己把自己删了, 老饕餮了
		}
};

int main() {
	Student stu(1);
	cout<<"-------------------------"<<endl;
	stu.~Student(); // 可以调用,但不会释放
	cout<<"-------------------------"<<endl;
	return 0;
	// 函数执行完毕, 再次调用析构函数
}
```

## 静态属性

所有对象所共有

静态方法不能使用const, 因为const指的是该方法不会对实例的非静态字段进行修改, 而静态方法和实例这个概念根本无关


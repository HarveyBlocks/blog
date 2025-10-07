## 头文件

-   .h文件
-   .c文件,**但不要这么做**,因为会把#include展开,会导致展开多次,导致函数重复定义

## 预处理

不会对语法进行报错,只有到编译阶段才会对程序检查

## 宏#define

### 不带参宏

```C
#define PI 3.14
```

完全替换

在预编译重新替换

方便,见名知意

#### 作用范围

1.  头文件
2.  同一个文件之下的所有文件

```C
#undefine PI
```

-   终止PI的宏定义



```C++
#define A 1
#include<stdio.h>

#define A 4//[Warning]redefine,但是输出4
//与头文件的重复定义很难说,以后再说


void fun1(void);
void fun2(void);

int main() {
	printf("%d\n", A);
	#undef A
	//printf("%d",A);
	#define A 2
	printf("%d\n", A);
	fun2();
	fun1();
	return 0;
}

void fun1(void) {
	#undef A
	//printf("%d",A);
	#define A 3
}

void fun2(void) {
	printf("%d\n", A);
}
```

![image-20231108161208362](../../../IT/JDK/JavaDailyBlog/typora-user-images/头文件/image-20231108161208362.png)



宏无论定义在哪里,都是**到文件末尾**

你也可以把宏放在头文件里,让引用头文件的文件使用这个宏



test.h

```h
#define A 5
```



```C++
#define A 1
#include<stdio.h>

#define A 4//[Warning]redefine,但是输出4
//与头文件的重复定义很难说,以后再说

void fun1(void);
void fun2(void);


int main() {
	printf("%d\n", A);
	#undef A
	//printf("%d",A);
	#define A 2
	printf("%d\n", A);
	fun2();
	fun1();
	return 0;
}

void fun1(void) {
	#undef A
	//printf("%d",A);
	#define A 3
}
#include"test.h"
void fun2(void) {
	printf("%d\n", A);
}
```

-   输出![image-20231108162207984](../../../IT/JDK/JavaDailyBlog/typora-user-images/头文件/image-20231108162207984.png)



不会对重复定义报错和警告



### 带参宏

带参宏的参数是没有类型的

```C
#define S(a,b) a*b
S(3+4,7)
```

->

```C
3+4*7
```

**硬换是吧**

	- 我想,大抵是预处理只是展开,计算是在编译的把
	- 为了更快,就单单预处理展开,这是代价

**千万小心**

```C
#define S(a,b) (a)*(b)
```



### 宏和函数

宏在预处理,直接展开

函数压栈弹栈

宏->浪费了时间(展开了多次)[**这只是在预编译阶段花的时间比较长吧?运行的时候反而比函数快吗?**],节省了空间

函数->浪费了时间,节省了空间(**真的吗?不是压栈弹栈,形参啥都全是占空间的啊????**) 

## 选择性编译#ifdef-#else-#endif和#ifndef-#else-#endif

### #ifdef-#else-#endif

-   目的屏蔽某一个头文件的重复定义(**当然要注意解耦合**)

```C
#ifdef AAA
	代码一
#else
    代码二
#endif
```

-   如果在当前.c ifdef上边定义过AAA就编译代码段AAA,否则编译代码段二;

### 和if-else

if和else的内容都会编译(会不会被运行到是另一回事情)

而#ifdef-#else是编译二选一

 ```C
#define A 1
#include<stdio.h>

int main() {
	#ifdef A
		printf("你好");
	#else
		#ifdef B
			你个笨蛋!
		#endif	
	#endif
	//不会编译到的地方都不会报错!
	return 0;
}

 ```

### #ifndef-#else-#endif

>   ifndef常常用来做防止重复预定义(防止预定义头文件,宏啊之类的,**宏是小事,头文件是大事**)



```C
#define A 1
#include<stdio.h>

int main() {
	#ifndef B
		#define B 2
	#endif
	//#ifndef B常常用来做防止重复预定义
	printf("%d",B);
	//没有任何问题
	return 0;
}
```

#### 规范的#ifudef的用法

1.  在头文件里

    ```C
    #ifndef __FUN_H__
    	#define __FUN_H__ A
    	extern int fun(int,int);
    #endid
    ```



例如遇到这种情况:

```C
#include"test.h"

#include"test.h"
int main(){
    return 0;
}
```

-   编译之后

    ![image-20231108172328001](IT/JDK/JavaDailyBlog/typora-user-images/头文件/image-20231108172328001.png)

**重复啦?没关系!已经准备好啦**

## 选择性编译#if-#else-#endif

-   常常用来做软件裁剪(瞎说的)

```C
#define A 3
//1是测试版,2是发布版,3是内部版(瞎说的)
#include<stdio.h>

int main() {
	#if A==1
		printf("执行程序段1");
	#else 
		#if A==2
			printf("执行程序段2");
			//不要把else if写到同一行去[Warning],逻辑会很怪
		#else
			#if A==3
				printf("执行程序段3");
			#endif
		#endif
	#endif
	//缩进:我不好说
	return 0;
}
```


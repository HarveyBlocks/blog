# 数组指针

数组指针+1是加一个数组的长度

-   数组指针是指针
    -   是指向数组的指针
-   指针数组是数组
    -   很多指针的数组

```C
/*数组指针ArrayPoint*/
//指向数组的指针
#include<stdio.h>

void test1(void){
	int a[3][5];
	for(int i=0;i<3;i++){
		printf("a[%d]的地址=%p\n",i,a+i);
		//a+1比大了(5*4)个字节
	}
}
```

## 数组指针的定义+数组指针作为参数实现函数传参

```C
void test(){
	int a[10] = {1,2,3,4,5,6,7,8,9,0};
	int *p = a;
	for(int i=0;i<10;i++){
		printf("a[i]=%d ",a[i]);
		printf("*(a+i)=%d ",*(a+i));
		printf("*(p+i)=%d ",*(p+i));
		printf("p[i]=%d\n",p[i]);//四个完全等价
	}
}
```

-   嗯?我怎么感觉你是((二维数组的)元素(一维数组的地址))的指针数组)

```C
	int a[10][20];
	int (* p)[20];
//	指向数组的数据类型 (*指针变量名)[指向数组的大小]
	p = a;
	int b[32][20];
	p = b;
```

### 一维的数组指针指向二位的数组

```C
void test3(int (*a)[20]);
void test2() {
	int a[10][20];
	int (* p)[20];
//	指向数组的数据类型 (*指针变量名)[指向数组的大小]
//	记得加括号
//	额....p指向(一维)数组的地址,然后同过[]读取指向的数组(一维)的元素(值)
	p = a;

	//初始化一波
	test3(a);
	//对初始化结果输出
	for (int i = 0; i < 10; i++) {
		for (int j = 0; j < 20; j++) {
			printf("%-4d",a[i][j]);
		}
		putchar('\n');
	}
	//斯巴拉西!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	for (int i = 0; i < 10; i++) {
		printf("a[%d]的地址=%p\n", i, a + i);
		//a+1比大了(20*4)个字节
	}
	printf("--------------------------\n");
	for (int i = 0; i < 10; i++) {
		printf("p[%d]的地址=%p\n", i, p + i);
		//p+1比大了(20*4)个字节
	}

	printf("a的地址=%p\n", a); //P
	printf("a的地址=%p\n", a + 1); //P+4*10

	putchar('\n');

	printf("a[0][0]=%p\n", &a[0][0]); //P
	printf("&a[0][0]+1=%d\n", &a[0][0] + 1); //P+4
	printf("*(&a[0][0]+1)=%d\n", *(&a[0][0] + 1)); //1
}

/**
 * @brief 为a[][20]做初始化
 * @param (*a)[20] 指向数组的数组指针
 **/
void test3(int (*a)[20]) {
	for (int i = 0; i < 10; i++) {
		for (int j = 0; j < 20; j++) {
			a[i][j] = i * 20 + j;
		}
	}
}

int main() {
	test2();
	return 0;
}
```

### 多维数组指针的定义

```C
void test4() {
	int (*p)	[3][5];
	int a[10]	[3][5];
	p = a;
	int b[1]	[3][5];
	p = b;
	int c[23]	[3][5];
	p = c;
	int d[124]	[3][5];
	p = d;
	int e[523]	[3][5];
	p = e;
	int f[341]	[3][5];
	p = f;
	//懂吧?
}
```

## 数组取地址

```C
//给数组指针&取地址
void test5() {
	int a[10];
	printf(" a  =%p\n", a);//P
	printf(" a+1=%p\n", a + 1);//p+4
	//a是数组的第一个元素的地址,类型是整形指针(int *)

	//对a的数组名取地址
	printf("&a  =%p\n", &a);//P
	printf("&a+1=%p\n", &a + 1);//P+4*a
	//&a是a数组的首地址,类型是整形数组指针(int (*)[10])

	//	printf("&a  =%p\n",&(a+1));错的

	int (*p)[10] = &a;//必须是10
	printf(" p  =%d\n", p);//P
	printf(" p+1=%d\n", p + 1);//P+4*a
	//不要试图去探究此时的*p的值是啥
}

```

### 二维数组取地址

```C
//二维数组&数组名取地址
void test6(){
	int a[2][16];
	printf(" a  =%p\n",a);//P
	printf(" a+1=%p\n",a+1);//P+4*16
	//a是数组的第一个一维数组的地址,类型是整形数组指针(int (*)[16])

	printf("&a  =%p\n",&a);//P
	printf("&a+1=%p\n",&a+1);//P+2*4*16
	//&a是a数组的首地址,类型是二位数组指针(int (*)[2][16])

}
```

# 数组名字和指针变量的区别

```C
void test(){
	int a[10] = {1,2,3,4,5,6,7,8,9,0};
	int *p = a;
	for(int i=0;i<10;i++){
		printf("a+%d=%p ",i,a+i);
		printf("p+%d=%p\t",i,p+i);//两个完全等价

		printf("a[%d]=%d ",i,a[i]);
		printf("*(a+%d)=%d ",i,*(a+i));
		printf("*(p+%d)=%d ",i,*(p+i));
		printf("p[%d]=%d\n",i,p[i]);//四个完全等价
	}
}
```

-   二维数组和指针可视化

```C
void test7() {
  	int a2[2][5] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
	int (*p2)[5] = a2;

	printf("a2+i=%%000000ppppppppp ");
	printf("p2+i=%%000000ppppppppp\t");

	printf("*(a2+i/5)+i%5=%%ppppppppppp ");
	printf("*(p2+i/5)+i%5=%%ppppppppppp\t");

	printf("a2[i/5][i%5]=%d\t\t");
	printf("*(*(a2+i/5)+i%5)=%d\t");
	printf("*(*(p2+i/5)+i%5)=%d\t");
	printf("p[i][i]=%d\n");// 做了个columns
	for (int i = 0; i < 10; i++) {
		printf("a2+%d=%p ", i, a2 + i);
		printf("p2+%d=%p\t", i, p2 + i); //两个完全等价

		printf("*(a2+%d)+%d=%p ",i/5,i%5,*(a2+i/5)+i%5);
		printf("*(p2+%d)+%d=%p\t",i/5,i%5,*(p2+i/5)+i%5);

		printf("a2[%d][%d]=%d\t",i/5,i%5,a2[i/5][i%5]);
		printf("*(*(a2+%d)+%d)=%d\t\t",i/5,i%5,*(*(a2+i/5)+i%5));
		printf("*(*(p2+%d)+%d)=%d\t\t",i/5,i%5,*(*(p2+i/5)+i%5));
		printf("p[%d][%d]=%d\n", i/5,i%5,p2[i/5][i%5]);// 四个完全等价
	}
}
```

-   虽然没用,但是太酷啦!

### 区别!!!!!!!!!!!!!!!!!!!!

1.  数组名是常量

    数组指针p是变量

2.  a和p的地址是不一样的

3.  &a和&p;是不一样的!


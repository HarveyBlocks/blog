# Stream流

## Stream继承关系

![image-20240515140343205](../assetss/Class09-stream%E6%B5%81/image-20240515140343205.png)##





cin、cout和clog是带缓冲区的

缓冲区由streambuf类对象来管理。

而cerr为非缓冲区流，一旦错误发生立即显示。

要使用这四个功能，必须包含`<iostream>`文件。

## 格式控制

### 格式控制标识

用或“**|**”运算符来合成，合成为一个长整型数，在**`<ios>`**中为：

   **`protected:long x_flags`**;

```cpp
enum{ 
	skipws=0x0001			// 跳过输入中的空白字符
   	left=0x0002,	        // 输出左对齐
   	right=0x0004,			// 输出右对齐
   	internal=0x0008,		// 数字基左对齐，数字右对齐
   	dec=0x0010,				// 在输入输出时将数据按十进制处理
   	oct=0x0020,				// 在输入输出时将数据按八进制处理
   	hex=0x0040,				// 在输入输出时将数据按十六进制处理
   	showbase=0x0080,      	// 在输出时带有表示数制基的字符
   	showpoint=0x0100,		// 输出符点数时,必定带小数点
   	uppercase=0x0200,		// 输出十六进制,用大写
   	showpos=0x0400,			// 输出正数时,加”+”号
   	scientific=0x0800,		// 科学数方式输出浮点数
   	fixed=0x1000,			// 定点数方式输出实数
   	unitbuf=0x2000,			// 插入后,立即刷新流
   	stdio=0x4000			// 插入后,立即刷新stdout和stderr
}		
```



### 源码API

```cpp
inline long ios::flags() const { 
    //返回当前标志字
    return x_flags; 
} 
inline long ios::flags(long _l){
    //参数作为新的标志字,并返回原标志字
        long _l0;  
       _l0=x_flags;   
       x_flags=_l;   
      return _l0;  
}
inline long ios::setf(long _l){ 
    //增加控制(多项)
    long _l0; _l0=x_flags; x_flags|=_l; return _l0; 
} 
inline long ios::unsetf(long _l){ 
    //清除指定位上的控制
    long _l0; _l0=x_flags; x_flags &=(~_l); return _l0;
}
```



### 控制宽度

类ios中还设置了三个输入输出流格式控制标志：

```cpp
protected:
   int x_precision; //标志浮点数精度,缺省为6位
   int x_width; //输出域宽,缺省域宽为0，
      //重设域宽只对其后第一输出项有效,如域宽不足,则不受限制
   char x_fill; //标志域宽有富余时填入的字符


```

相关接口函数为：

```cpp
inline int ios::width() const{
    return x_width;
} //返回当前域宽

inline int ios::width(int _i){
    int _i0;
    _i0=(int)x_width;
    x_width=_i; 
    return _i0; 
} //把参数作为新的域宽,返回原域宽

inline char ios::fill() const{
    return x_fill;
} //返回当前填充字符

inline char ios::fill(char _c){ 
    char _c0; _c0=x_fill;
    x_fill=_c;
    return _c0;
} //参数作为新填充字符,返回原填充字符

inline int ios::precision(int i){
    int _i0;
    _i0=(int)x_presion;
    x_presion=_i;
    return _i0;
}    //参数作为新填充字符,返回原填充字符

inline int ios::precision() const {
    return x_presion;
} //返回当前精度

```

## 输入流

### 流状态

状态字state为整型，其各个位在ios中说明：

```cpp
enum ios_state{
   goodbit=0x00,	  	//流正常
   eofbit=0x01,			//输入流结束,忽略后继提取操作；或文件结束，已无数据可取
   failbit=0x02,		//最近的I/O操作失败,流可恢复
   badbit=0x04,		  	//最近的I/O操作非法,流不可恢复
}
```

读取状态的有关操作如下：

```cpp
inline int ios::rdstate() const {
    return state;
} //读取状态字

inline int ios::bad(){
    return state & badbit;
} //返回非法操作位

inline void ios::clear(int _i){
    lock();state=_i;unlock();
}//人工设置状态,可用来清状态;可使流有效

inline int ios::eof() const {
    return state&eofbit;
}  //返回流结束位

inline int ios::fail() const{
    return state&(badbit|failbit);
} //返回操作非法和操作失败这两位

inline int ios:operator !() const{
    return state&(badbit|failbit);
}//可用操作符!()代替fail()

inline int ios::good() const{
    return state==0;
} //正常返回1 

```

#### 健壮性输入示例

```cpp
char str[255];
int i;
cout<<"请输入整数:"<<endl; 				 //强制清空缓冲区,保证输出 
cin>>i; 								//可输入非数字字符,下次再输入若干字符加数字串等进行检测
while(cin.fail()){
	cout<<cin.rdstate()<<endl; 			//输出状态字
    // cin>>i; 							// 不被执行
	cin.clear(0); 						//清状态字
	cin.getline(str,254); 				//读空缓冲区
	cout<<"输入错误,请重新输入整数"<<endl;
	cin>>i; 
}
```



### 读取API

#### 读取一行

输入输出流中的成员函数
输入流成员函数声明：

```cpp
istream& istream::get(char &); 
//提取一个字符,放在字符型变量中;且不跳过空白符
istream& istream::get(char *,int,char=’\n’);
//提取字符串,到串结束符或指定长度为止;不读取分隔符, '\n'留在缓冲区

istream& istream::getline(char *,int,char=’\n’);
//同上,包括分隔符, '\n'从缓冲区去除,不放到char * 里去

```

示例

输入

`123`

```cpp
char a[5],b[5];
cin.get(a, 5);
cout << a << endl; // 缓冲区里的\n还在
cin.getline(b, 5); // b去读, 一下子就是\n,
cout << b << endl; // b里面有一个\n

```

输出

`123`

#### 清空缓存区

用来读空（指定一个大的数量）缓冲区：

```cpp
istream&istream::ignore(int=1,int=EOF);
```

第一个参数为要提取的字符数量，缺省为1；
第二个参数表示遇到该字符则结束，包括该结束字符，但对所提取的字符**不保存不处理, 还在缓冲区**，作用是空读；
第二个参数的缺省值EOF为文件结束标志。

## 输出流

输出流成员函数声明：

```cpp
ostream&  ostream::put(char); 	//输出参数字符
ostream&  ostream::flush(); 	//刷新一个输出流,用于cout和clog, 强制将缓冲区的内容输入到文件
```







## <<和>>运算符重载

自定义运算符, 将自己的类直接输入输出

```cpp
#ifndef SCHOOLWORK_CLOTH_H
#define SCHOOLWORK_CLOTH_H

#include <iostream>
using namespace std;

/**
 * 服装有尺码、颜色、价格等信息；
 */
class Cloth  {
private:
    int size;
    double price;
public:
    Cloth(int size, double price) :
            size(size), price(price) {}
    friend ostream &operator<<(ostream &s, const Cloth &obj) {
        return s << '(' << obj.price << ',' << obj.size << ')';
    }

    friend istream &operator>>(istream &s, Cloth &obj) {
        s >> obj.price >> obj.size;
        return s;
    }

};

#endif //SCHOOLWORK_CLOTH_H
```

## 文件流

`ifstream`, `ofstream`, `fstream`

### 打开文件

#### 打开文件流

```cpp
void ifstream::open(const char*,int =ios::in);
void ofstream::open(const char *,int=ios::out);
void fstream::open(const char*,int =ios::in|ios::out); 
```

第一个参数为要打开的磁盘文件名。

第二个参数为打开方式，有输入（in），输出（out）等，打开方式在ios基类中定义为枚举类型。

在打开文件流的同时, 可以打开文件对象

所以第二步可如下进行：

```cpp
iofile.open("myfile.txt",ios::in|ios::out);
```

#### 创建文件流对象

因此打开一个文件完整的程序为：

```cpp
fstream iofile("myfile.txt",ios::in|ios::out);
if(!iofile){
   cout<<"不能打开文件:"<<"myfile.txt"<<endl;
   exit(1); 
}
```

-   文件不存在
-   文件被占用

#### 关闭文件和文件流

```cpp
// 文件流
void ifstream::close();
void ofstream::close();
void fstream::close();
// 文件
void iofile::close();  

```

　　关闭文件时，系统收回与该文件相关的内存空间，可供再分配。把磁盘文件与文件流对象之间的关联断开，可防止误操作修改了磁盘文件。    关闭文件并没有取消文件流对象，该文件流对象又可与其他磁盘文件建立联系。文件流对象在程序结束时，或它的生命期结束时，由析构函数撤消。它同时释放内部分配的预留缓冲区。

#### 文件打开方式

```cpp
文件打开方式是在ios类中定义的，公有枚举成员：
enum open_mode{		
ios::in　= 0x01,　// 用于读的情况，文件必须已经存在(ifstream默认的打开方式)
ios::out　 = 0x02,　// 用于写的情况，文件不存在则创建，若文件已存在则清空原内容(ofstream默认的打开方式)
ios::ate　 = 0x04,　// 文件不存在时，生成空文件；如果文件存在，清空原文件（ofstream打开方式）。
// 如果没有文件，打开失败；如果有文件，文件指针定位到文件尾，但是不能写文件（ifstream打开方式）。
ios::app　 = 0x08,　// 用于写的情况，文件不存在则创建；若文件已存在，则在原文件内容末尾写入新的内容，文件指针的位置总在最后
ios::trunc　 = 0x10,　// 在读写前先将文件长度截断为0（默认）
ios::binary　= 0x80　 // 用于读写二进制格式文件, 以二进制方式打开文件使用方法与格式控制符相同
}; 
```

### 示例: 复制文件

```cpp
int main() {
    
    char filename[256], buf[100];
    
    // 文件打开逻辑
    
    fstream fileInputStream, fileOutputStream;
    cout << "输入源文件路径名:" << endl;
    cin >> filename;
    fileInputStream.open(filename, ios::in); //打开一个已存在的文件
    while (!fileInputStream) {
        cout << "源文件找不到,请重新输入路径名:" << endl;
        cin >> filename;
        fileInputStream.open(filename, ios::in);
    }
    cout << "输入目标文件路径名:" << endl;
    cin >> filename; //只能创建文件，不能建立子目录，如路径不存在则失败
    fileOutputStream.open(filename, ios::out);
    if (!fileOutputStream) {
        cout << "目标文件创建失败" << endl;
        exit(1);
    }

    // 拷贝逻辑
    
    
    
    // 关闭文件逻辑
    fileInputStream.close();
    fileOutputStream.close();
}
```



```cpp
// 拷贝逻辑
while (!fileInputStream.eof()) {  //按行拷贝   A行
    if (fileInputStream.gcount() <= BUF_CAPACITY - 1) {
        cout << buf << '\n'; //因换行符未输出，因此要补输出
    } else {
        cout << buf;
        if (!fileInputStream.fail()) {  //如果输入流不为fail状态，说明该行长度正好等于BUF_CAPACITY，需要补输出‘\n’
            cout << '\n';
        }
    }
    fileInputStream.getline(buf, 100);
    if (fileInputStream.eof()) {//处理最后一行；
        cout << buf << '\n';
    }
}
```
### 二进制文件的读写

```cpp
istream&  istream::read(char *,int);//从二进制流提取
//第一个参数指定存放有效输入的变量地址,第二个参数指定提取的字节数,
//函数从输入流提供指定数量的字节送到指定地址开始的单元
```

```cpp
ostream&  ostream::write(const char *,int);//向二进制流插入
//第一个参数指定输出对象的内存地址,第二个参数指定插入的字节数,
//函数从该地址开始将指定数量的字节插入输入输出流
```

读函数并不能知道文件是否结束，可用状态函数

```cpp
int ios::eof()
```

来判断文件是否结束。

必须指出，系统是根据当前操作的实际情况设置状态位，如需根据状态位来判断下一步的操作，必须在一次操作后立即去调取状态位，以判断本次操作是否有效

```cpp
void inventory::Bdatatofile(ofstream&dist){
	dist.write(Description,20);
	dist.write(No,10);
	dist.write((char*)&Quantity,sizeof(int));
	dist.write((char*)&Cost,sizeof(double));
	dist.write((char*)&Retail,sizeof(double));
}
void inventory::Bdatafromfile(ifstream&sour){
	sour.read(Description,20);
	sour.read(No,10);
	sour.read((char*)&Quantity,sizeof(int));
	sour.read((char*)&Cost,sizeof(double));
	sour.read((char*)&Retail,sizeof(double));
} //由此可见读和写是完全对称的过程,次序决不能错
```

示例

```cpp
ofstream ddatafile("d:\\Ex9_10.data",ios::out|ios::binary);
car1.Bdatatofile(ddatafile);
motor1.Bdatatofile(ddatafile); 
//ddatafile.close();
ifstream sdatafile("d:\\Ex9_10.data",ios::in|ios::binary); 
//重新打开文件,从头读取数据
car2.Bdatafromfile(sdatafile); //从文件读取数据拷贝到对象car2
```

使用二进制文件，可以控制字节长度，读写数据时不会出现二义性，可靠性高。同时不知格式是无法读取的，保密性好。文件结束后，系统不会再读,但程序不会自动停下来，所以要判断文件中是否已没有数据。如写完数据后没有关闭文件，直接开始读，则必须把文件定位指针移到文件头。如关闭文件后重新打开，文件定位指针就在文件头。



### 文件随机访问

```cpp
// ios类中公有枚举类型：
enum seek_dir{
	beg=0, //文件开头
	cur=1, //文件指针的当前位置
	end=2 //文件结尾
};
```

```cpp
// istream类中提供了如下三个成员函数：
istream&istream::seekg(streampos); //指针直接定位

istream&istream::seekg(streamoff, ios::seek_dir); //指针相对定位

long istream::tellg(); // 返回当前指针位置
```

流的指针位置类型streampos和流的指针偏移类型streamoff定义为长整型，也就是可访问文件的最大长度为4G 

用法

```cpp
datafile.seekg(-20L,ios::cur);
// 从当前位置向文件头方向移20个字节。
datafile.seekg(20L,ios::beg);
// 从文件头向文件尾方向移20个字节。
datafile.seekg(-20L,ios::end);
// 从文件尾向文件头方向移20个字节。
```

tellg()  和seekg()往往配合使用

 指针不可移到文件头之前或文件尾之后。

ostream类也提供了三个成员函数管理文件定位指针，它们是：

```cpp
ostream&ostream::seekp(streampos);
ostream&ostream::seekp(streamoff,ios::seek_dir);
long ostream::tellp();
```



## 字符串流

内存流

字符串流类包括`ostringstream`、`istringstream`、`stringstream`。它们在`<sstream>`中说明。

```cpp
istringstream :: istringstream (char * str);
istringstream :: istringstream (char * str,int);
ostringstream :: ostringstream (char*,int,int=ios::out); 
```



```cpp
char str[36]=”This is a book.\n”;
char ch;
istringstream input(str);
input>>ch; // 从输入设备(串)读入一个字符
cout<<ch<<endl; // 输出'T'
```


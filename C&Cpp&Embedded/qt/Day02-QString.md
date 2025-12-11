# QString

## Qt与字符编码

| **转入函数**                  | **转出函数**              | **描述**                                                     |
| ----------------------------- | ------------------------- | ------------------------------------------------------------ |
| **fromLocal8Bit**             | **toLocal8Bit**           | 与操作系统及本地化语言相关，Linux 一般是 UTF-8 字符串，Windows 一般是 ANSI 多字节编码字符串。 |
| **fromUtf8**                  | **toUtf8**                | 与 UTF-8 编码的字符串相互转换。                              |
| **fromUtf16**                 | **utf16 和 unicode**      | 与 UTF-16（UCS2）编码的字符串互相转换，utf16 函数与 unicode 函数功能一样， 注意没有 to 前缀，因为 QString 运行时的内码就是 UTF-16，字符的双字节采用主机字节序。 |
| **fromUcs4**                  | **toUcs4**                | 与 UTF-32（UCS4）编码的字符串互相转换，一个字符用四个字节编码，占空间多，应用较少。 |
| **fromStdString**             | **toStdString**           | 与 std::string 对象互相转换，因为 C++11 规定标准字符串 std::string 使用 UTF-8 编码，这对函数功能与上面 **Utf8 转码函数相同。 |
| **fromStdWString**            | **toStdWString**          | 与 std::wstring 对象相互转换，在 Linux 系统里宽字符是四字节的 UTF-32，在 Windows 系统里宽字符是两字节的 UTF-16。因为不同平台有歧义，不建议使用。 |
| **fromCFString fromNSString** | **toCFString toNSString** | 仅存在于苹果 Mac OS X 和 iOS 系统。                          |

```cpp
#include <QApplication>
#include <QString>
#include <QDebug>
int main(int argc, char *argv[]) {
    QString qStr = "中文";
    // qDebug 宏
    qDebug()<<QString::fromUtf8("中文"); // // "中文"
    qDebug()<<qStr; // "中文"
    qDebug()<<qStr.data(); // 0x27afe1b6880
    qDebug()<<QString::fromLatin1("中文").toUtf8(); // "\xC3\xA4\xC2\xB8\xC2\xAD\xC3\xA6\xC2\x96\xC2\x87"

    return 0;

}
```



## `QString`简介

### `QChar`简介

16 bit 的字符 `QChar` 

 `QChar` 是一个 Unicode 4.0 标准的字符

### `QString`简介

-   `QString` 是由一系列 `QChar` 组成的字符串，以 `NULL` 字符结尾（末尾的 `NULL` 不计入字符串长度）

-   对于**超过 16bit** 范围的国际码字符，`QString` 里采用**相邻的一对 QChar 来表示**

-   `QString` 使用的是 UTF-16 的双字节编码

    -   UTF-8 缺点就是一个字符的长度不固定，对字符串操作效率是有影响的，因为得先确定每个字符的长度。
    -   `QString` 采用**固定长度字符单元**的 UTF-16 编码，运行时字符串比较、查询操作效率更高。

    

**`tr` 函数就是将 UTF-8 变长编码的字符串转成 QString 运行时的内码**。

支持加法重载

```cpp
int main(int argc, char *argv[]) {
    QString qStr = "中文";
    QString qStrPlus = "难学";
    qDebug() << qStr + qStrPlus;
    qDebug() << qStr + "不好学";
    
    string str = "English";
    string strPlus = " hard";
    qDebug() << str + strPlus;
    cout << str + strPlus << endl;
    cout << str + " strPlus" << endl;
    cout << "str " + strPlus << endl;
    return 0;
```

### `QByteArray `

用于操作 UTF-8 编码以及其他本地化字符串（如 GBK、Big5）、字节数组（不以 NULL 结尾的纯数据）等

## 隐式共享机制

>   implicit sharing

#### 需求



字符串之间经常有手动复制或者通过函数参数、函数返回值等**复制操作**

QString 为了优化内存使用效率，避免出现大量相同内容的字符串副本

QString 对复制过程采用**隐式共享机制**

#### 原理



比如执行字符串对象 str1 = str2 时

-   如果这两个对象字符串内容都没有后续改变
    -   那么它们会指向同一块字符串数据
-   而如果其中之一发生改变
    -   字符串数据块的复制过程才会发生



## 基本类型与字符串互相转换

| **基本类型**           | **Qt别称**              | **转入函数**    | **转出函数**    | **描述**                                                     |
| ---------------------- | ----------------------- | --------------- | --------------- | ------------------------------------------------------------ |
| **short**              | **qint16**              | **arg或setNum** | **toShort**     | 2 字节长度，有符号短整型。                                   |
| **unsigned short**     | **ushort、quint16**     | **arg或setNum** | **toUShort**    | 2 字节长度，无符号短整型。                                   |
| **int**                | **qint32**              | **arg或setNum** | **toInt**       | 4 字节长度，有符号整型。                                     |
| **unsigned int**       | **uint、quint32**       | **arg或setNum** | **toUInt**      | 4 字节长度，无符号整型。                                     |
| **long**               | **无**                  | **arg或setNum** | **toLong**      | 有符号长整型，对于 32 位编程 long 是 4 字节长度，对于 64 位编程是 8 字节长度。 |
| **unsigned long**      | **ulong**               | **arg或setNum** | **toULong**     | 无符号长整型，对于 32 位编程 unsigned long 是 4 字节长度，对于 64 位编程是 8 字节长度。 |
| **long long**          | **qlonglong、qint64**   | **arg或setNum** | **toLongLong**  | 8 字节长度，有符号长长整型。                                 |
| **unsigned long long** | **qulonglong、quint64** | **arg或setNum** | **toULongLong** | 8 字节长度，无符号长长整型。                                 |
| **float**              | **默认情况下无**        | **arg或setNum** | **toFloat**     | 4 字节长度，单精度浮点数。                                   |
| **double**             | **默认情况对应 qreal**  | **arg或setNum** | **toDouble**    | 8 字节长度，双精度浮点数。                                   |

-   Qt 里的**类型的别称**都定义在头文件 `<QtGlobal>` 里面

    由于其他绝大多数 Qt 头文件都包含了该全局头文件

    通常不需要自己手动去包含它的。

### 转入QString

#### `setNum`

```cpp
QString & setNum(int n, int base = 10);
```

支持进制转化

```cpp
QString & setNum(int n, int base = 10);
```

`format`

-   'e'：科学计数法，小写 e，如 [-]9.9e[±]999。
-   'E'：科学计数法，大写 E，如 [-]9.9E[±]999。
-   'f'：定点数显示，[-]9.9。
-   'g'：自动选择用科学计数法或定点数显示，哪种方式最简洁就用哪个，科学计数法的 e 小写。
-   'G'：自动选择用科学计数法或定点数显示，哪种方式最简洁就用哪个，科学计数法的 E 大写。

#### `arg`

```cpp
QString arg(int a, int fieldWidth = 0, int base = 10, QChar fillChar = QLatin1Char( ' ' )) const;
```

```cpp
QString arg(double a, int fieldWidth = 0, char format = 'g', int precision = -1, QChar fillChar = QLatin1Char( ' ' )) const;
```

```cpp
QString arg(const QString & a, int fieldWidth = 0, QChar fillChar = QLatin1Char( ' ' )) const;
```

注意` const`，不改变字符串对象本身的内容，**返回一个全新的 `QString` 对象**

-   `base` 进制
-   `fieldWidth` 生成的目标字符串宽度，**0 表示自动设置长度**
-   `fillChar` 填充字符，如果设置的域宽比较大，多余的空位就会使用这个填充字符填满。
-   `QLatin1Char ` 一个字节长度的拉丁字符,` QLatin1Char` 有对应的类 `QLatin1String`





-   `arg(int)`

    ```cpp
    QString arg(int a, int fieldWidth = 0, int base = 10, QChar fillChar = QLatin1Char( ' ' )) const;
    ```

    -   Dec

        ```cpp
        long numDec = 800;
        QString strMod = QObject::tr("Normal: %1");
        strResult = strMod.arg(numDec);  //%1是占位符，第一个arg函数参数变量转后的字符串填充到 %1 位置
        qDebug() << "Mod: " << strMod << " \t Result: " << strResult;
        // Mod:  "Normal: %1"       Result:  "Normal: 800"
        ```

    -   Oct

        ```cpp
        int numOct = 63;
        strResult = QObject::tr("Oct: %1").arg(numOct, 4, 8, QChar('0'));  //numOct转换后为4字符域宽，8进制，填充0
        qDebug() << strResult;
        // "Oct: 0077"
        
        ```

    -   Hex

        ```cpp
        short numHex = 127;
        QString strPrefix = QObject::tr("0x");
        //占位符里可填充数值转的字符串，也可以直接填充原有的字符串
        strResult = QObject::tr("Hex: %1%2").arg(strPrefix).arg(numHex, 0, 16);  //串联：第一个arg函数参数填充到%1，第二个arg填充到%2
        qDebug() << strResult;
        // "Hex: 0x7f"
        ```

-   `arg(double)`

    ```cpp
    QString arg(double a, int fieldWidth = 0, char format = 'g', int precision = -1, QChar fillChar = QLatin1Char( ' ' )) const;
    ```

    -   double

        ```cpp
        double numReal = 123.78999;
        strResult = QObject::tr("Fixed: %1 \t Scientific: %2").arg(numReal, 0, 'f').arg(numReal, 0, 'e', 3);
        qDebug() << strResult;
        // "Fixed: 123.789990 \t Scientific: 1.238e+02"
        ```

-   `arg(string)`

    ```cpp
    QString arg(const QString & a, int fieldWidth = 0, QChar fillChar = QLatin1Char( ' ' )) const;
    ```

-   占位符

    ```cpp
    //占位符可重复，也可乱序
    int one = 1;
    int two = 2;
    int three = 3;
    strResult = QObject::tr("%1 小于 %2，%1 小于 %3，%3 大于 %2 。").arg(one).arg(two).arg(three);
    qDebug() << strResult;
    // "1 小于 2，1 小于 3，3 大于 2 。"
    ```

### QString转出

```cpp
int QString::toInt(bool * ok = 0, int base = 10) const;
```

-   `ok` 反馈转换过程是否成功
    -   溢出, 则失败
    -   进制不对(数符), 则失败
-   如果将 `base` 为 0 那么 toInt 函数将自动识别字符串对象里面的进制标识
    -   对于 "0" 打头的自动按八进制转换
    -   对于 "0x" 打头的自动按十六进制转换
    -   其他情况都按十进制转换。

如果转换出错，`ok` 指向的变量会设置为 false，**返回值为 0** 

转标准字符串

```cpp
std::string QString::toStdString() const;
```



## 字符串运算符

| **operator**                                               | **描述**                                                     |
| ---------------------------------------------------------- | ------------------------------------------------------------ |
| **=**                                                      | 赋值运算符，遵循**隐式共享**规则，在赋值的两个对象有变化时才真正复制数据块。 |
| **+=**                                                     | 追加。将运算符左边和右边字符串拼接后，赋值给左边对象。       |
| **<**<br>**<=**<br/>**==**<br/>**!=**<br/>**>**<br/>**>=** | 依据**字典序**比较                                           |
| **[]**                                                     | 类似数组取数的中括号，从指定位置取出 QChar 字符，另外还可以修改指定位置的 QChar 字符。 |
| **+**                                                      | 拼接。这是个友元函数，将两个字符串拼接后返回**全新的字符串对象**。 |

## 字串查询与操作

### 增加/填充/重复

-   追加子串到字符串**尾部**。

    ```cpp
    QString& append(const QString & str);
    ```

-   子串加到字符串**头部**

    ```cpp
    QString& prepend(const QString & str);
    ```

-   将子串 str 插入到 position 序号位置

    ```cpp
    QString &   insert(int position, const QString & str)
    ```

    -   `position ` 子串 str 插入后的起始序号

-   字符 ch 填充当前字符串

    ```cpp
    QString &   fill(QChar ch, int size = -1)
    ```

    -   不指定 size ，就把所有的字符都填成 ch 字符。
    -   如果指定正数 size，字符串长度被重置为 size 大小，里面依然全是 ch 字符。

-   将当前字符串重复拼接 times 次数

    ```cpp
    QString   repeated(int times) const
    ```

    -   返回新的重复串

    ```cpp
    void testSub() {
        QString qs = "这是一段中文"; // "这是一段中文"
        qDebug() << qs;
        qDebug() << qs[0];
        qDebug() << qs.fill(QChar(qs[0]), -1); // "这这这这这这"
        qDebug() << qs.fill(QChar(qs[0]), 2); // "这这"
        qDebug() << qs; // "这这"
    }
    ```

    



### 判断



-   判断字符串是否以某个子串**打头**

    ```cpp
    bool   startsWith(const QString & s, Qt::CaseSensitivity cs = Qt::CaseSensitive) const;
    ```

    -   `cs` 大小写是否敏感, 后略

-   判断字符串是否以某个子串结尾

    ```cpp
    bool   endsWith(const QString & s, Qt::CaseSensitivity cs = Qt::CaseSensitive) const
    ```

-   字符串对象里是否包含子串 str

    ```cpp
    bool  contains(const QString & str, Qt::CaseSensitivity cs = Qt::CaseSensitive) const
    ```



### 删

-   从 position 开始的位置移除掉 n 个字符

    ```cpp
    QString &   remove(int position, int n)
    ```

    -   如果 n 比 position 位置开始的子串长度大，后面的就会被全部移除。

-   将匹配的所有子串 str 都从字符串里面移除掉

    ```cpp
    QString &   remove(const QString & str, Qt::CaseSensitivity cs = Qt::CaseSensitive);
    ```

    -   拿来消除空格之类的字符比较好使。

-   剔除字符串**头部和尾部**的空白字符

    ```cpp
    QString   trimmed() const
    ```

    -   包括` '\t', '\n', '\v', '\f', '\r', ' ' `
    -   字符串中间的空白不处理。

-   剔除字符串里出现的**所有空白字符**

    ```cpp
    QString   simplified() const
    ```

    -   包括 `'\t', '\n', '\v', '\f', '\r', ' '` 
    -   端和中间的都剔除。

-   截断字符串

    ```cpp
    void   truncate(int position)
    ```

    -   从 position 序号开始截断字符串
    -   只保留 0 ~ position-1 位置的字符串
    -   position 位置被设为 NULL，后面的全移除。



### 改

-   从 position 序号开始的 n 个字符的子串替换成 after 字符串。

    ```cpp
    QString &   replace(int position, int n, const QString & after)
    ```

-   将字符串里出现的所有子串 before 全部替换为新的 after

    ```cpp
    QString &   replace(const QString & before, const QString & after, Qt::CaseSensitivity cs = Qt::CaseSensitive)
    ```



### 查

-   查找**第一个**指定子串

    ```cpp
    int   indexOf(const QString & str, int from = 0, Qt::CaseSensitivity cs = Qt::CaseSensitive) const;
    ```

    -   返回起始位置索引 
    -   找不到返回-1

-   查找**最后一个**子串

    ```cpp
    int   lastIndexOf(const QString & str, int from = -1, Qt::CaseSensitivity cs = Qt::CaseSensitive) const;
    ```

    -   返回起始位置索引 
    -   找不到返回-1

-   左边 n 个字符构成的子串。

    ```cpp
    QString   left(int n) const;
    ```

-   右边 n 个字符构成的子串。

    ```cpp
    QString   right(int n) const;
    ```

-   从 position 位置开始的 n 个字符构成的子串

    ```cpp
    QString   mid(int position, int n = -1) const
    ```

    -   不设置 n 的话就包含到末尾

### 聚合

字符串对象里子串 str 出现的次数

```cpp
int   count(const QString & str, Qt::CaseSensitivity cs = Qt::CaseSensitive) const
```

-   如果没出现就返回 0

### 切割

split 用字符或子串 sep 切分当前字符串内容，

```cpp
QStringList   split(QChar sep, SplitBehavior behavior = KeepEmptyParts, Qt::CaseSensitivity cs = Qt::CaseSensitive) const;

QStringList   split(const QString & sep, SplitBehavior behavior = KeepEmptyParts, Qt::CaseSensitivity cs = Qt::CaseSensitive) const;
```

-   可以从返回的列表提取各个子串。
-   behavior 是分隔模式，是否保留空白字符区域等



返回第 start 段到第 end 段之间的内容

```cpp
QString   section(QChar sep, int start, int end = -1, SectionFlags flags = SectionDefault) const

QString   section(const QString & sep, int start, int end = -1, SectionFlags flags = SectionDefault) const
```

-   如果没指定 end 就一直包含到最后
-   flags 参数影响划分行为，如大小写敏感、是否忽略空白区域等。

## QTextStream

```cpp
void testQStreamOut() {
    //内存输出流
    QString strOut;
    QTextStream streamOut(&strOut); // 将对此字符串进行一揽子操作
    //打印多种进制数字
    streamOut << 800 << Qt::endl;
    streamOut << Qt::hex << 127 << Qt::endl;
    // 之前设置的进制会一直持续生效，直到被重新设置为止。
    streamOut << 800 << Qt::endl;
    streamOut << Qt::oct << 63 << Qt::endl;
    //还原为十进制
    streamOut << Qt::dec;

    //设置域宽和填充字符
    streamOut << qSetFieldWidth(8) << qSetPadChar('0') << 800;
    //还原默认域宽和填充
    streamOut << qSetFieldWidth(0) << qSetPadChar(' ') << Qt::endl;

    //设置精度
    streamOut << qSetRealNumberPrecision(3) << Qt::fixed << 123.789999 << Qt::endl;
    streamOut << qSetRealNumberPrecision(6) << Qt::scientific << 123.789999 << Qt::endl;

    //打印字符串和数字混搭
    streamOut << QObject::tr("7*7 == ") << 7 * 7 << Qt::endl;
    //显示现在的字符串对象
    qDebug() << strOut;
    std::cout<<strOut.toStdString()<<std::endl;
}
void testQStreamIn(){
    //内存输入流
    QString strIn = QObject::tr("800  abcd  123.789999");
    QTextStream streamIn(&strIn);
    int numDec = 0;
    QString strSub;
    double dblReal = 0.0;
    //输入到变量里
    streamIn >> numDec >> strSub >> dblReal;
    //显示
    qDebug() << numDec;
    qDebug() << strSub;
    qDebug() << Qt::fixed << dblReal;   //定点数显示
}
```

## QByteArray

QByteArray 在赋值、传参数、返回值时也是使用**隐式共享机制**提高运行效率，只有字符串发生修改时才会执行深拷贝。

### 和char

QByteArray 的字节单元是 char

-   头文件 `<QByteArray> `还对C语言字符串函数做了安全版本的封装

    都是全局函数

    加了 q 字母前缀，如 

    -   `qstrlen`
    -   `qstrncmp`
    -   `qstrcpy`

### 作为字符串处理类

类似 QString ，但 QByteArray 内部字符编码不确定, 需要使用者自己决定，所以要慎用。

它会自动在字符串末尾添加 '\0'

```cpp
QByteArray basABCD = "ABCD";
QByteArray basXYZ = "XYZ";
qDebug()<<(basABCD < basXYZ);   //二者字符编码一致才能比较！
```
### 作为纯的字节数组

里面可以包含多个 '\0' ，经常用于网络数据的接收和发送。


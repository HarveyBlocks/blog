# ByteBuffer

##基本使用

###Channel利用Buffer读

1.  从Channel写, 向buffer写入数据	

    ```java
    len = channel.read(buffer) 
    // 返回读取到的字节数, 如果读完了会返回-1
    ```

2.  调用`flip()`切换为读模式

    ```java
    buffer.flip();
    ```

3.  从buffer读取数据

    ```java
    log.debug("{}", (char) buffer.get()/*读一个字节*/);
    ```

4.  调用`clear()`和`compact()`切换至写模式

    ```java
    // buffer切换到写模式
    buffer.clear();
    ```

实例

```java
public static void testByteBuffer() {
    try (FileInputStream file = new FileInputStream(RESOURCE_PATH_PREFIX+"data.txt")) {
        // 1. 通过输入输出流简介获取FileChannel
        FileChannel channel = file.getChannel();
        // 2. 获取ByteBuffer(abstract)缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(10/*缓冲区字节大小*/);// allocate,分配
        int len ;
        while ((len = channel.read(buffer)/*返回读取到的字节数, 如果读完了会返回-1*/) >= 0) {
            // 从 channel 读取 = 向 buffer缓冲区 写入
            log.debug("读到字节数：{}", len);
            // buffer切换读模式
            buffer.flip();
            while (buffer.hasRemaining()/*检查是否还有未读的数据*/) {
                log.debug("{}", (char) buffer.get()/*读一个字节*/);
            }
            // buffer切换到写模式
            buffer.clear();
        }
    } catch (IOException e) {
        log.error(e.getMessage());
    }
}
```

### Channel利用Buffer写

1.  将Buffer切换为写模式

    ```java
    buffer.clear();
    ```

2.  向Buffer写入数据

    ```java
    log.debug("写入长度{}", len);
    buffer.put(bytes, size * i, len);
    ```

3.  将Buffer切换为读模式

    ```java
    buffer.flip();
    ```

4.  向Channel中写入Buffer中的数据

    ```java
    channel.write(buffer);
    // 向channel中写 = 从buffer中读
    ```

实例

```java
public void testByteBufferWrite() {
    // 要写入的数据
    byte[] bytes = {
            0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68,
            0x69, 0x6A, 0x6B, 0x6C, 0x6D, 0x6E, 0x6F, 0x70};
    try (FileOutputStream file = new FileOutputStream(RESOURCE_PATH_PREFIX + "data.txt")) {
        // 1. 通过输入输入流间接获取FileChannel
        FileChannel channel = file.getChannel();
        // 2. 获取ByteBuffer(abstract)缓冲区
        int size = 10;//缓冲区字节大小
        ByteBuffer buffer = ByteBuffer.allocate(size);// allocate,分配
        int i = 0;
        do {
            buffer.clear();
            int len = Math.min(bytes.length - size * i, size);
            log.debug("写入长度{}", len);
            buffer.put(bytes, size * i, len);
            buffer.flip();
            i++;
            // 向channel中写 = 从buffer中读
        } while (channel.write(buffer) == size);
    } catch (IOException e) {
        log.error(e.getMessage());
    }
}
```

## ByteBuffer读写原理



###ByteBuffer的三种属性

* capacity容量
* position现有位置
* limit写入限制

本质上没有所谓"读模式"或"写模式", 只是`position`和`limit`指针的位置不同而已

"读模式"或"写模式"只是为了规范, 减少错误

###初始

一开始, 写模式

![](../../assets/Day01-ByteBuffer%E7%9A%84%E4%BD%BF%E7%94%A8/0021.png)

写模式下，position 是写入位置，limit 等于容量

###写模式

写入了 4 个字节后:

![](../../assets/Day01-ByteBuffer%E7%9A%84%E4%BD%BF%E7%94%A8/0018.png)

-   Position指针时刻指向下一个写入的位置

###flip切换

**flip 动作发生**后

1.  limit (读取限制)移至已写入内容的末尾(Position之前的位置)
2.  position 移至Buffer的开头
3.  切换为读模式

![](../../assets/Day01-ByteBuffer%E7%9A%84%E4%BD%BF%E7%94%A8/0019.png)

###读取

读取 4 个字节后，状态

![](../../assets/Day01-ByteBuffer%E7%9A%84%E4%BD%BF%E7%94%A8/0020.png)

###Clear切换

clear 动作发生后

1.  **不会清空Buffer中的数据**
2.  Posion移至开头
3.  Limit移至Cpacity处
4.  切换为写模式

==下图有误==

![](../../assets/Day01-ByteBuffer%E7%9A%84%E4%BD%BF%E7%94%A8/0021.png)

### Compact切换

>   压缩

1.  **不会清空Position之前的已读数据**
2.  Position和Limit之间的数据移至Buffer开头
3.  Position移至`(Limit-Position)`处
4.  Limit移至Capacity处
5.  切换至写模式

==下图有误==

![](../../assets/Day01-ByteBuffer%E7%9A%84%E4%BD%BF%E7%94%A8/0022.png)





-   由于不会清空数据, 一定要按照规范去操作Buffer



## 调试工具类

>   更好地输出Buffer的内容

```java
package com.harvey.netty.nio.util;

import io.netty.util.internal.StringUtil;

import java.nio.ByteBuffer;

import static io.netty.util.internal.MathUtil.isOutOfBounds;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-21 20:02
 */
public class ByteBufferUtil {
    private static final char[] BYTE2CHAR = new char[256];
    private static final char[] HEXDUMP_TABLE = new char[256 * 4];
    private static final String[] HEX_PADDING = new String[16];
    private static final String[] HEXDUMP_ROW_PREFIXES = new String[65536 >>> 4];
    private static final String[] BYTE2HEX = new String[256];
    private static final String[] BYTE_PADDING = new String[16];

    static {
        final char[] DIGITS = "0123456789abcdef".toCharArray();
        for (int i = 0; i < 256; i++) {
            HEXDUMP_TABLE[i << 1] = DIGITS[i >>> 4 & 0x0F];
            HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i & 0x0F];
        }

        int i;

        // Generate the lookup table for hex dump paddings
        for (i = 0; i < HEX_PADDING.length; i++) {
            int padding = HEX_PADDING.length - i;
            HEX_PADDING[i] = "   ".repeat(Math.max(0, padding));
        }

        // Generate the lookup table for the start-offset header in each row (up to 64KiB).
        for (i = 0; i < HEXDUMP_ROW_PREFIXES.length; i++) {
            StringBuilder buf = new StringBuilder(12);
            buf.append(NEWLINE);
            buf.append(Long.toHexString((long) i << 4 & 0xFFFFFFFFL | 0x100000000L));
            buf.setCharAt(buf.length() - 9, '|');
            buf.append('|');
            HEXDUMP_ROW_PREFIXES[i] = buf.toString();
        }

        // Generate the lookup table for byte-to-hex-dump conversion
        for (i = 0; i < BYTE2HEX.length; i++) {
            BYTE2HEX[i] = ' ' + StringUtil.byteToHexStringPadded(i);
        }

        // Generate the lookup table for byte dump paddings
        for (i = 0; i < BYTE_PADDING.length; i++) {
            int padding = BYTE_PADDING.length - i;
            BYTE_PADDING[i] = " ".repeat(Math.max(0, padding));
        }

        // Generate the lookup table for byte-to-char conversion
        for (i = 0; i < BYTE2CHAR.length; i++) {
            if (i <= 0x1f || i >= 0x7f) {
                BYTE2CHAR[i] = '.';
            } else {
                BYTE2CHAR[i] = (char) i;
            }
        }
    }

    /**
     * 打印所有内容
     */
    public static void debugAll(ByteBuffer buffer) {
        int oldLimit = buffer.limit();
        buffer.limit(buffer.capacity());
        StringBuilder origin = new StringBuilder(256);
        appendPrettyHexDump(origin, buffer, 0, buffer.capacity());
        System.out.println("+--------+-------------------- all ------------------------+----------------+");
        System.out.printf("position: [%d], limit: [%d]\n", buffer.position(), oldLimit);
        System.out.println(origin);
        buffer.limit(oldLimit);
    }

    /**
     * 打印可读取内容
     */
    public static void debugRead(ByteBuffer buffer) {
        StringBuilder builder = new StringBuilder(256);
        appendPrettyHexDump(builder, buffer, buffer.position(), buffer.limit() - buffer.position());
        System.out.println("+--------+-------------------- read -----------------------+----------------+");
        System.out.printf("position: [%d], limit: [%d]\n", buffer.position(), buffer.limit());
        System.out.println(builder);
    }

    private static void appendPrettyHexDump(StringBuilder dump, ByteBuffer buf, int offset, int length) {
        if (isOutOfBounds(offset, length, buf.capacity())) {
            throw new IndexOutOfBoundsException(
                    "expected: " + "0 <= offset(" + offset + ") <= offset + length(" + length
                            + ") <= " + "buf.capacity(" + buf.capacity() + ')');
        }
        if (length == 0) {
            return;
        }
        dump.append("         +-------------------------------------------------+")
                .append(NEWLINE)
                .append("         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |")
                .append(NEWLINE).append("+--------+-------------------------------------------------+----------------+");

        final int fullRows = length >>> 4;
        final int remainder = length & 0xF;

        // Dump the rows which have 16 bytes.
        for (int row = 0; row < fullRows; row++) {
            int rowStartIndex = (row << 4) + offset;

            // Per-row prefix.
            appendHexDumpRowPrefix(dump, row, rowStartIndex);

            // Hex dump
            int rowEndIndex = rowStartIndex + 16;
            for (int j = rowStartIndex; j < rowEndIndex; j++) {
                dump.append(BYTE2HEX[getUnsignedByte(buf, j)]);
            }
            dump.append(" |");

            // ASCII dump
            for (int j = rowStartIndex; j < rowEndIndex; j++) {
                dump.append(BYTE2CHAR[getUnsignedByte(buf, j)]);
            }
            dump.append('|');
        }

        // Dump the last row which has less than 16 bytes.
        if (remainder != 0) {
            int rowStartIndex = (fullRows << 4) + offset;
            appendHexDumpRowPrefix(dump, fullRows, rowStartIndex);

            // Hex dump
            int rowEndIndex = rowStartIndex + remainder;
            for (int j = rowStartIndex; j < rowEndIndex; j++) {
                dump.append(BYTE2HEX[getUnsignedByte(buf, j)]);
            }
            dump.append(HEX_PADDING[remainder]);
            dump.append(" |");

            // Ascii dump
            for (int j = rowStartIndex; j < rowEndIndex; j++) {
                dump.append(BYTE2CHAR[getUnsignedByte(buf, j)]);
            }
            dump.append(BYTE_PADDING[remainder]);
            dump.append('|');
        }

        dump.append(NEWLINE)
                .append("+--------+-------------------------------------------------+----------------+");
    }

    private static void appendHexDumpRowPrefix(StringBuilder dump, int row, int rowStartIndex) {
        if (row < HEXDUMP_ROW_PREFIXES.length) {
            dump.append(HEXDUMP_ROW_PREFIXES[row]);
        } else {
            dump.append(NEWLINE);
            dump.append(Long.toHexString(rowStartIndex & 0xFFFFFFFFL | 0x100000000L));
            dump.setCharAt(dump.length() - 9, '|');
            dump.append('|');
        }
    }

    public static short getUnsignedByte(ByteBuffer buffer, int index) {
        return (short) (buffer.get(index) & 0xFF);
    }
}
```



使用实例

```java
private void testByteBufferUtil(){
    ByteBuffer buffer = ByteBuffer.allocate(10);
    byte[] bytes = {0x61,0x62};
    buffer.put(bytes);
    ByteBufferUtil.debugAll(buffer);
}
```



使用结果

```text
+--------+-------------------- all ------------------------+----------------+
position: [2], limit: [10]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 00 00 00 00 00 00 00 00                   |ab........      |
+--------+-------------------------------------------------+----------------+
```

##API

### 分配空间方法

-   分配之后的容量大小不能改变

```java
private void testCreate() {
    log.debug(ByteBuffer.allocate(4)        // java.nio.HeapByteBuffer
            .getClass().getName());
    log.debug(ByteBuffer.allocateDirect(4)  // java.nio.DirectByteBuffer
            .getClass().getName());
}
```

#### HeapByteBuffer

>   Java堆内存

-   存在于Java虚拟机中
-   读写效率较低, 多一次拷贝, (需要从操作系统拷贝到Java虚拟机)
-   收到垃圾回收(GC)的影响(整理内存碎片), 数据会改变存储的位置

####DirectByteBuffer

>   直接内存

-   是操作系统提供的内存
-   读写效率较高
-   不受到垃圾回收的影响
-   分配内存较慢(需要调用操作系统的函数)
-   使用不当可能造成内存泄漏

###put()与get()



-   `put()`总是写到`position`所指向的位置
-   `get()`总是读`position`所指向的内容
-   `put()`执行之后,`position`自动后移一格
-   `get()`执行之后,`position`自动后移一格
-   `get()` 在非读模式下也能使用, 只是结果不一定和预期一样(主要看`position`)
-   `put()` 在非写模式下也能使用, 只是结果不一定和预期一样(主要看`position`)
-   `put()`返回`buffer`, 故支持链式编程
-   在`position`在值上超过`limit`, 就会报错: `BufferUnderflowException`



```java
private void testGetAndPut(){
    // test get()
    ByteBuffer buffer = ByteBuffer.allocate(4);
    byte[] bytes = {0x61,0x62};
    buffer.put(bytes);
    log.debug(buffer.limit()+","+buffer.capacity());//4,4
    log.debug(buffer.position()+"->"+buffer.get());//2->0
    log.debug(buffer.position()+"->"+buffer.get());//3->0
    // log.debug(buffer.position()+"->"+buffer.get());//BufferUnderflowException
	// test put()
    buffer.flip();
    ByteBufferUtil.debugAll(buffer);
    buffer.put((byte) 'x');
    ByteBufferUtil.debugAll(buffer);
}
```



-   `get(bytes)`是从buffer中寻找`bytes`一致的字节数组, 然后将position移到byte之后, 返回buffer

-   `get(index)`不会移动position指针

-   `ByteBuffer put(byte[] src, int offset, int length)`

    -   `offset`是src的偏移量

    -   `length`是偏移量之后的长度

    -   `put(byte[] src)` :

        ```java
        public final ByteBuffer put(byte[] src) {
            return put(src, 0, src.length);
        }
        ```



### 指针

#### 重置position指针

```java
buffer.rewind();
```

即`position = 0;`

####reset()和mark和mark()

mark和position一样是buffer的属性

-   mark总是小于position, mark默认-1

-   mark用来标记position的位置

    ```java
    buffer.mark(); // 标记位置
    ```

    即`mark = position`

-   position经过变化后可以用`reset()`回到曾经标记过的位置

    ```java
    buffer.reset();
    ```

    即`position = mark`

-   没啥卵用



当然并不是简简单单`position = 0;`就好了, 可以自己去看源码

### ByteBuffer和字符串的互相转换

#### 字符串转ByteBuffer

-   `\0`不会存入Buffer

```java
public ByteBuffer testString2ByteBuffer(String str){
    // 法一: 字符串转Byte[]
    byte[] bytes = str.getBytes(
            StandardCharsets.UTF_8/*可省略, 缺省的值有编程环境(idea)决定*/);
    // static ByteBuffer wrap(byte[] array, int offset, int length)
    ByteBuffer buffer1 = ByteBuffer.wrap(bytes);// 此时是读模式
    ByteBufferUtil.debugAll(buffer1);
    // 法二: StandardCharsets
    ByteBuffer buffer2 = StandardCharsets.UTF_8.encode(str); // 此时是读模式
    ByteBufferUtil.debugAll(buffer2);
    return buffer1;
}
```

#### ByteBuffer转字符串

-   Buffer不需要`\0`

```java
private String testByteBuffer2String(ByteBuffer buffer,int newPosition,int newLimit) {
    // StandardCharsets
    buffer.position(newPosition);
    buffer.limit(newLimit);
    CharBuffer decode = StandardCharsets.UTF_8.decode(buffer);
    String string = decode.toString();
    System.out.println(string);
    return string;
}
```

## 使用技巧与思想

### 分散写

>   Scattering Reads

####需求

一段文本文件`onetwothree`, 有三段信息, 已知长度分别是`3`,`3`,`5`

要求读入内存

#### 分析

-   将文件写入buffer, 再将buffer分成小buffer
-   将文件拿出`3`写入`buffer1`, 再拿出`3`写入`buffer2`, 拿出最后的`5`写入`buffer3`
    -   简化代码, 早早地完成分割

#### 代码实现

```java
public void testScatteringReads(){
    ByteBuffer buffer1 = ByteBuffer.allocate(3);
    ByteBuffer buffer2 = ByteBuffer.allocate(3);
    ByteBuffer buffer3 = ByteBuffer.allocate(5);
    try (FileInputStream inputStream = new FileInputStream(RESOURCE_PATH_PREFIX + "data.txt")) {
        FileChannel channel = inputStream.getChannel();
        long read = channel.read(new ByteBuffer[]{
                buffer1, buffer2, buffer3
        });
        log.debug("长度{}",read);// 长度11
        ByteBufferUtil.debugAll(buffer1);
        ByteBufferUtil.debugAll(buffer2);
        ByteBufferUtil.debugAll(buffer3);
    } catch (IOException e) {
        log.error(e.getMessage());
    }
}
```

###集中读

>   Gathering Writes

####需求

有三段Buffer

要求写入文件

#### 分析

-   将buffer集中成大buffer, 再向文件中写
    -   减少文件IO
-   `buffer1`,写入文件, buffer2写入文件, buffer3写入文件





####代码实现

```java
public void testGatheringWrites(){
    ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("one");
    ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("two");
    ByteBuffer buffer3 = StandardCharsets.UTF_8.encode("three");
    try (FileOutputStream outputStream = new FileOutputStream(RESOURCE_PATH_PREFIX + "data.txt")) {
        FileChannel channel = outputStream.getChannel();
        long write = channel.write(new ByteBuffer[]{
                buffer1, buffer2, buffer3
        });
        log.debug("长度{}",write); // 长度11
    } catch (IOException e) {
        log.error(e.getMessage());
    }
}
```

### 粘包和半包

#### 粘包和半包现象

1.  正常的信息由三段组成, 每段的最后以`\n`结尾

    ```java
    "HelloWorld\n"
    "I'm Zhang San\n"
    "How are you?\n"
    ```

2.  但是由于为了传输效率等问题, 固定了传输的长度

    -   传输长度太短, 导致请求过多, 效率降低
    -   传输长度太长, 导致占用宽带过多, 效率降低
    -   其他原因

    ```java
    "Hello,world.\nI'm Zhang San.\nHo"
    "w are you?\n"
    ```

3.  于是出现了粘包和半包现象

4.  多段信息在同一批数据中发送称之为**粘包**:

    ```java
    "Hello,world.\nI'm Zhang San.\n"
    ```

5.  一段信息被拆散在两批数据称之为**半包**:

    ```java
    "San.\nHo"
    "w are you?\n"
    ```

#### 粘包和半包的整理分割

-   需求: 将

    ```java
    "Hello,world.\nI'm Zhang San.\nHo"
    "w are you?\n"
    ```

    由`\n`分割成

    ```java
    "Hello,world.\n"
    "I'm Zhang San.\n"
    "How are you?\n"
    ```

-   分析: 

    -   由一个buffer承载一批数据
    -   读到`\n`之后使用`compact()`, 将`\n`之后, 而又没有被读到的数据压缩

-   实现

    ```java
    public List<String> split() {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        List<String> strings = new ArrayList<>();
        try (FileInputStream inputStream = 
             new FileInputStream(RESOURCE_PATH_PREFIX + "data.txt")) {
            FileChannel channel = inputStream.getChannel();
            buffer.clear();
            while (channel.read(buffer) >= 0) {
                buffer.flip();
                buffer.mark();
                while (buffer.hasRemaining() ) {
                    while (buffer.hasRemaining()&& buffer.get() != '\n');
                    if(!buffer.hasRemaining()){
                        break;
                    }
                    int limit = buffer.limit();
                    int position = buffer.position();
                    buffer.limit(position);
                    buffer.reset();
                    byte[] bytes = new byte[buffer.limit() - buffer.position()];
                    buffer.get(bytes);
                    strings.add(new String(bytes, StandardCharsets.UTF_8));
                    buffer.limit(limit);
                    buffer.mark();
                }
                buffer.reset();
                buffer.compact();
            }
            byte[] bytes = new byte[buffer.position()];
            buffer.limit(buffer.position());
            buffer.rewind();
            buffer.get(bytes);
            strings.add(new String(bytes, StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return strings;
    }
    ```

    因为不爽于他两个for遍历两次的冗余, 我写了这个麻烦的代码

    其实`get(bytes);`里也有循环

    可读性也降低了

    哭哭哭

    因为数组的大小是不可改变的, 两次重复的循环似乎是不可避免的

    




# RISC-V

2010年, 很年轻, 根据当下的需要去除了历史上的多余的指令

UC Berkeley设计

适合各种尺度的CPU(有32位, 64位, 128位的版本)

## 配置环境

[环境](https://venus.cs61c.org/)

![image-20240226120320289](../assetss/Day01-RISC-V/image-20240226120320289.png)

![image-20240226120338835](../assetss/Day01-RISC-V/image-20240226120338835.png)

![image-20240226120451002](../assetss/Day01-RISC-V/image-20240226120451002.png)

![image-20240226120503386](../assetss/Day01-RISC-V/image-20240226120503386.png)

## 寄存器和变量

汇编不存在变量, 统统滴地址的干活(当然也没有类型, 统统二进制的干活, 其意义取决于代码怎么去解释它)

寄存器在CPU中, CPU的资源很金贵, 不能把寄存器做很大

而且寄存器做太大, 需要的寄存器地址就越多, 导致占用编码就很长, 这也不好

RISC-V中只有32个通用寄存器(x0-x31), 每个32位, 速度是内存的100到500倍

x0寄存器是特殊的, 无论怎么写, 读到的都会是0, 也就是说只能用32个寄存器存东西



高级语言编译后, 其变量可能存储在寄存器, 可能存储在内存(volatile关键字保证编译器将变量存储在内存)

RASC-V**只能操作寄存器**, 

存储在内存的数据需要将数据从内存中拷贝出来, 再存入寄存器(load), 然后运算使用

数据需要存到内存, 也需要先存到寄存器, 然后再从寄存器存到内存(store)

16G->1T(15k)(32G*32)

256G->16T(6.4k)(1T*16)

CPU\*20(80k)(4000\*20)

## RISC-V指令



指令分为操作数(opcode)和操作码(operands)

```assembly
add x1 x2 x3 
# x1(目的寄存器) = x2 + x3
# add 是 operands
# x1, x2, x3 是opcode
```

![image-20240226125224923](../assetss/Day01-RISC-V/image-20240226125224923.png)

### RASI基础指令集

![在这里插入图片描述](../assetss/Day01-RISC-V/e60e9bc08961493c81d1be18b3452b10.png)







### 立即数指令

>立即数 Immediates

```assembly
addi x1 x2 常量 
# 没有subi, 要减只能把常量设成负数
```

常量的上限是12位二进制

也就是最大是0xfff(-1), 是12位的带符号数



12位带符号数拓展成32位会怎么做呢?

![image-20240226133200536](../assetss/Day01-RISC-V/image-20240226133200536.png)

测试一下

```assembly
addi x1 x0 -0x5 # 取反加一: 0000...0101, =>1111...1011=>0xF...B
```

![image-20240226133451780](../assetss/Day01-RISC-V/image-20240226133451780.png)



### 伪指令

机器一下子看不懂 , 基于基础指令实现的

```assembly
mv x5 x4 # 将x4赋值给x5
```

![image-20240226131244098](../assetss/Day01-RISC-V/image-20240226131244098.png)



```assembly
li x1 0x123 # 将0x123赋值给x1
```



## x0寄存器

x0用来置零啊,

 用来占一个操作码的位置啊等等

```assembly
or x1 x2 x0 # 将x2赋值给x1
addi x1 x0 0x123 # 将0x123赋值给x1
and x1 x1 x0 # 置零
```

不需要的值写到x0寄存器里, 相当于丢弃

```assembly
and x0 x1 x2 # 将结果丢弃
```

`no-op`空指令(啥都不做, 只是停了一下)

```assembly
add x0 x0 x0
```

`jump-and-link`会经常结合x0使用



## 内存读写

![image-20240226160554382](../assetss/Day01-RISC-V/image-20240226160554382.png)

### 端序

内存32位=4byte

大端小端

一个数`0xEF_12_32_AF`

| 地址                        | 0    | 1    | 2    | 3    |
| --------------------------- | ---- | ---- | ---- | ---- |
| 原数据(HEX)                 | 01   | 23   | 45   | 67   |
| 存入后的目标数据(小端)(HEX) | 67   | 45   | 23   | 01   |
| 存入后的目标数据(大端)(HEX) | 01   | 23   | 45   | 67   |

**大部分的指令集架构都是小端**, 早期是大端

![image-20240226155810971](../assetss/Day01-RISC-V/image-20240226155810971.png)



### load

>   从内存取数据到寄存器

使用`lw`(load word)指令, 在此环境中一个word是32位

```assembly
lw x20 12(x3)
# x3的值是内存中的地址. 
# 从内存中找到x3的地址所在的位置, 
# 偏移12个Byte, 
# 从此开始取一个word的长度
# 存到x20中
sw x20 12(x3)
# 存储x20中的数据
# x3的值是内存中的地址. 
# 从内存中找到x3的地址所在的位置, 
# 偏移12个Byte, 
# 从此开始存一个word的长度
```

在存储的时候, 考虑到符号拓展

```assembly
lb x2 12(x3) # 加载一个字节, 有符号拓展. 读入一个FF, 可能在集群器里变成FFFF_FFFF
lh x2 12(x3) # 有符号拓展
lbu x2 12(x3) # 无符号拓展
lhu x2 12(x3) # 无符号拓展
```

### Store

![image-20240226160751346](../assetss/Day01-RISC-V/image-20240226160751346.png)

## 指令集

### 算术运算指令



```text
ADD 加法。
ADC 带进位加法。
INC 加 1。
AAA 加法的ASCII码调整。
DAA 加法的十进制调整。
SUB 减法。
SBB 带借位减法。
DEC 减 1。
NEC 求反(以 0 减之)。
CMP 比较。(两操作数作减法，仅修改标志位，不回送结果)。
AAS 减法的ASCII码调整。
DAS 减法的十进制调整。
MUL 无符号乘法。
IMUL 整数乘法。
以上两条，结果回送AH和AL(字节运算)，或DX和AX(字运算)，
AAM 乘法的ASCII码调整。
DIV 无符号除法。
IDIV 整数除法。
以上两条，结果回送:
商回送AL，余数回送AH， (字节运算);
或 商回送AX，余数回送DX， (字运算)。
AAD 除法的ASCII码调整。
CBW 字节转换为字。 (把AL中字节的符号扩展到AH中去)
CWD 字转换为双字。 (把AX中的字的符号扩展到DX中去)
CWDE 字转换为双字。 (把AX中的字符号扩展到EAX中去)
CDQ 双字扩展。 (把EAX中的字的符号扩展到EDX中去)
```



### 逻辑运算指令

```text
AND 与运算。
OR 或运算。
XOR 异或运算。
NOT 取反。
TEST 测试。(两操作数作与运算，仅修改标志位，不回送结果)。
SHL 逻辑左移。
SAL 算术左移。(=SHL)
SHR 逻辑右移。
SAR 算术右移。(=SHR)
ROL 循环左移。
ROR 循环右移。
RCL 通过进位的循环左移。
RCR 通过进位的循环右移。
以上八种移位指令，其移位次数可达255次。
移位一次时， 可直接用操作码。 如 SHL AX，1。
移位>1次时， 则由寄存器CL给出移位次数。
如 MOV CL，04
SHL AX，CL
```





### 数据传输指令

```text
1。 通用数据传送指令。
MOV 传送字或字节。
MOVSX 先符号扩展，再传送。
MOVZX 先零扩展，再传送。
PUSH 把字压入堆栈。
POP 把字弹出堆栈。
PUSHA 把AX，CX，DX，BX，SP，BP，SI，DI依次压入堆栈。
POPA 把DI，SI，BP，SP，BX，DX，CX，AX依次弹出堆栈。
PUSHAD 把EAX，ECX，EDX，EBX，ESP，EBP，ESI，EDI依次压入堆栈。
POPAD 把EDI，ESI，EBP，ESP，EBX，EDX，ECX，EAX依次弹出堆栈。
BSWAP 交换32位寄存器里字节的顺序
XCHG 交换字或字节。( 至少有一个操作数为寄存器，段寄存器不可作为操作数)
CMPXCHG 比较并交换操作数。( 第二个操作数必须为累加器AL/AX/EAX )
XADD 先交换再累加。( 结果在第一个操作数里 )
XLAT 字节查表转换。
── BX 指向一张 256 字节的表的起点， AL 为表的索引值 (0-255，即
0-FFH); 返回 AL 为查表结果。 ( [BX+AL]->AL )
```



```java
2。 输入输出端口传送指令。
IN I/O端口输入。 ( 语法: IN 累加器， {端口号│DX} )
OUT I/O端口输出。 ( 语法: OUT {端口号│DX}，累加器 )
输入输出端口由立即方式指定时， 其范围是 0-255; 由寄存器 DX 指定时，
其范围是 0-65535。
```



```java
3。 目的地址传送指令。
LEA 装入有效地址。
例: LEA DX，string ;把偏移地址存到DX。
LDS 传送目标指针，把指针内容装入DS。
例: LDS SI，string ;把段地址:偏移地址存到DS:SI。
LES 传送目标指针，把指针内容装入ES。
例: LES DI，string ;把段地址:偏移地址存到ES:DI。
LFS 传送目标指针，把指针内容装入FS。
例: LFS DI，string ;把段地址:偏移地址存到FS:DI。
LGS 传送目标指针，把指针内容装入GS。
例: LGS DI，string ;把段地址:偏移地址存到GS:DI。
LSS 传送目标指针，把指针内容装入SS。
例: LSS DI，string ;把段地址:偏移地址存到SS:DI。
```



```java
4。 标志传送指令。
LAHF 标志寄存器传送，把标志装入AH。
SAHF 标志寄存器传送，把AH内容装入标志寄存器。
PUSHF 标志入栈。
POPF 标志出栈。
PUSHD 32位标志入栈。
POPD 32位标志出栈。
```





### 串指令

```text
DS:SI 源串段寄存器 :源串变址。
ES:DI 目标串段寄存器:目标串变址。
CX 重复次数计数器。
AL/AX 扫描值。
D标志 0表示重复操作中SI和DI应自动增量; 1表示应自动减量。
Z标志 用来控制扫描或比较操作的结束。
MOVS 串传送。
( MOVSB 传送字符。 MOVSW 传送字。 MOVSD 传送双字。 )
CMPS 串比较。
( CMPSB 比较字符。 CMPSW 比较字。 )
SCAS 串扫描。
把AL或AX的内容与目标串作比较，比较结果反映在标志位。
LODS 装入串。
把源串中的元素(字或字节)逐一装入AL或AX中。
( LODSB 传送字符。 LODSW 传送字。 LODSD 传送双字。 )
STOS 保存串。
是LODS的逆过程。
REP 当CX/ECX<>0时重复。
REPE/REPZ 当ZF=1或比较结果相等，且CX/ECX<>0时重复。
REPNE/REPNZ 当ZF=0或比较结果不相等，且CX/ECX<>0时重复。
REPC 当CF=1且CX/ECX<>0时重复。
REPNC 当CF=0且CX/ECX<>0时重复。
```



### 程序转移指令

```text
1>无条件转移指令 (长转移)
JMP 无条件转移指令
CALL 过程调用
RET/RETF过程返回。

2>条件转移指令 (短转移，-128到+127的距离内)
( 当且仅当(SF XOR OF)=1时，OP1 JA/JNBE 不小于或不等于时转移。

JAE/JNB 大于或等于转移。
JB/JNAE 小于转移。
JBE/JNA 小于或等于转移。
以上四条，测试无符号整数运算的结果(标志C和Z)。

JG/JNLE 大于转移。
JGE/JNL 大于或等于转移。
JL/JNGE 小于转移。
JLE/JNG 小于或等于转移。
以上四条，测试带符号整数运算的结果(标志S，O和Z)。

JE/JZ 等于转移。
JNE/JNZ 不等于时转移。
JC 有进位时转移。
JNC 无进位时转移。
JNO 不溢出时转移。
JNP/JPO 奇偶性为奇数时转移。
JNS 符号位为 "0" 时转移。
JO 溢出转移。
JP/JPE 奇偶性为偶数时转移。
JS 符号位为 "1" 时转移。

3>循环控制指令(短转移)
LOOP CX不为零时循环。
LOOPE/LOOPZ CX不为零且标志Z=1时循环。
LOOPNE/LOOPNZ CX不为零且标志Z=0时循环。
JCXZ CX为零时转移。
JECXZ ECX为零时转移。

4>中断指令
INT 中断指令
INTO 溢出中断
IRET 中断返回

5>处理器控制指令
HLT 处理器暂停， 直到出现中断或复位信号才继续。
WAIT 当芯片引线TEST为高电平时使CPU进入等待状态。
ESC 转换到外处理器。
LOCK 封锁总线。
NOP 空操作。
STC 置进位标志位。
CLC 清进位标志位。
CMC 进位标志取反。
STD 置方向标志位。
CLD 清方向标志位。
STI 置中断允许位。
```


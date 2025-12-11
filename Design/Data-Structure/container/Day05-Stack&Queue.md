# Stack&Queue

严格的栈和队列都不能遍历, 不能随机访问, 所以底层用链表实现比较合适, 避免申请大片连续的内存

## 栈实现队列

```cpp
template<typename T>
class Queue {
private:
    stack<T> in;
    stack<T> out;
public:
    void push(const T &t) {
        // 入队
        in.push(t);
    }

    T pop() {
        T top = this->front();
        out.empty() ? in.pop() : out.pop();
        return top;
    }

    T front() {
        if (out.empty()) {
            while (!in.empty()) {
                T t = in.top();
                in.pop();
                out.push(t);
            }
        }
        if (out.empty()) {
            throw Exception("Has Empty");
        }
        return out.top();
    }
};
```

## 队列实现栈

一个队列, 队首元素出队, 在队尾入队

```cpp

template<typename T>
class Stack {
private:
    deque<T> queue;
public:
    void push(const T &t) {
        // 入栈
        queue.push_back(t);
    }

    T pop() {
        // 出栈
        int n = queue.size();
        while (--n > 0) {
            auto head = queue.front();
            queue.pop_front();
            queue.push_back(head);
        }
        if (queue.empty()) {
            throw Exception("Has Empty");
        }
        auto top = queue.front();
        queue.pop_front();
        return top;
    }

    T top() {
        auto top = this->pop();
        queue.push_back(top);
        return top;
    }

    bool empty() {
        return queue.empty();
    }
};
```

## 以栈为载体排序

```cpp
static Array<T> &sort(Array<T> &array) {
    if (array.length() <= 1) {
        return array;
    }
    Stack<T> originSt;
    Stack<T> resultSt;
    int pre = array[0];
    originSt.push(pre);

    for (int i = 1; i < array.length(); ++i) {
        T &element = array[i];
        originSt.push(element);
    }

    resultSt.push(originSt.pop());
    while (!originSt.empty()) {
        T top = resultSt.top();
        T element = originSt.pop();
        if (top < element) {
            int count = 0;
            while (!resultSt.empty()) {
                top = resultSt.top();
                if (top >= element) {
                    break;
                }
                originSt.push(resultSt.pop());
                count++;
            }
            resultSt.push(element);
            while (count-- > 0) {
                resultSt.push(originSt.pop());
            }
        } else {
            resultSt.push(element);
        }
    }

    for (int i = 0; i < array.length(); ++i) {
        array[i] = resultSt.pop();
    }

    return array;
}
```

## 括号匹配

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Design/Data-Structure/container/Day05-Stack&Queue/20.有效括号.gif" alt="20.有效括号" style="zoom:67%;" />

## 删除重复项

"abbbaabababbab"

->"a**bbb**aabababbab"

->"aaabababbab"

->"**aaa**bababbab"

->"bababbab"

->"baba**bb**ab"

->"babaab"

->"bab**aa**b"

->"babb"

->"ba**bb**"

->"ba"

为之奈何?

![1047.删除字符串中的所有相邻重复项](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Design/Data-Structure/container/Day05-Stack&Queue/1047.删除字符串中的所有相邻重复项.gif)

```cpp
Stack<char> charStack;
for (char c: str) {
    if (charStack.empty() || charStack.top() != c) {
        charStack.push(c);
        continue;
    }
    // 比较
    charStack.pop();
}
Stack<char> output;
while (!charStack.empty()) {
    output.push(charStack.pop());
}
while (!output.empty()) {
    cout << output.pop() << flush;
}
```

但是奇数个元素重复, 则为止奈何?

```cpp
Stack<char> charStack;
for (int i = 0; i < str.size();) {
    char c = str[i];
    if (charStack.empty() || charStack.top() != c) {
        charStack.push(c);
        ++i;
    } else {
        // 比较
        charStack.pop();
        for (i; i < str.size() && str[i] == c; ++i);
    }
}
Stack<char> output;
while (!charStack.empty()) {
    output.push(charStack.pop());
}
while (!output.empty()) {
    cout << output.pop() << flush;
}
```

复杂度没变, 技术力变低哩(悲)

## 求逆波兰表达式的运算结果

表达式树

中缀表达式-中序遍历

逆波兰表达式-后序遍历

求值:

1.  遍历表达式栈

2.  表达式元素是数字进入3, 是双目运算符, 进入4

3.  入栈

4.  将两个元素弹出栈, 进行运算(小心没有交换律的运算)

    ```cpp
    num1 = stack.pop();
    num2 = stack.pop();
    result = num2 sigh num1;
    ```

## 中缀表达式转逆波兰表达式

### 优先级

-   结束符 -1
-   加减 1
-   乘除 2
-   右括号 0 右括号出现说明括号中的运算应该结束
-   左括号 3->0 左括号出现, 优先级最高, 入栈后, 左括号降级

### 转换

1.  设立运算符栈, 设立运算符的栈底为`#`
2.  当前字符是操作数的, 则直接输出到后缀式
3.  当前字符是运算符且优先级大于栈底运算符的, 入栈
4.  当前字符是运算符且优先级小于等于栈顶  运算符, 弹出栈顶运算符输出到后缀式
5.  括号出栈不出现在后缀表达式

```cpp
a+b*5+2*(c+d)
```

```cpp
ab5*+2cd +*+
```

## 滑动窗口

滑动窗口在遍历数组的时候, 显示每次状态滑动窗口中的最大值

维护一个递减队列, 里面存储滑动窗口中可能成为最大值的那个值

什么是"可能成为最大值"的值?

滑动窗口[1,2,5,2,4,6,1,2]中的所有局部峰值

遍历, 元素入队, 检查队列中是否有比自己小的, 比自己小的统统出队, 然后自己再入队

取得队列后, 队列中的元素脱离滑动窗口了, 就出队, 是局部峰值, 就入队

[代码随想录实现](https://www.programmercarl.com/0239.滑动窗口最大值.html#思路) 依据值来删除元素, 不是会导致相同值的元素全被删除, 而这个删除应该依据的是索引, 所以可能导致误删

```cpp
template<typename T>
class Deque {
private:
    deque<T> queue;
public:
    void pushFront(const T &t) {
        queue.push_front(t);
    }

    void pushBack(const T &t) {
        queue.push_back(t);
    }

    T popBack() {
        T t = this->back();
        queue.pop_back();
        return t;
    }

    T popFront() {
        T t = this->front();
        queue.pop_front();
        return t;
    }

    T front() {
		if (this->empty()) {
            throw Exception("Hash Empty");
        }
        return queue.front();
    }

    T back() {
        if (this->empty()) {
            throw Exception("Hash Empty");
        }
        return queue.back();
    }

    bool empty() {
        return queue.empty();
    }
};
```

```cpp
if (windowLen > data.size()) {
    windowLen = data.size();
}
Deque<int> index; // 存储可能的最大值的索引
// 初始化, 为了获取第一个元素(做成和后面统一的话想拿到第一个反而也会麻烦)
for (int i = 0; i < windowLen; ++i) {
    char c = data[i];
    if (index.empty() || data[index.back()] > c) {
        index.pushBack(i);
    } else if (data[index.back()] < c) {
        index.popBack();
        i--;
    }
}
cout << data[index.front()] << " " << flush;
for (int i = windowLen; i < data.size(); ++i) {
    char c = data[i];
    if (index.empty() || data[index.back()] > c) {
        index.pushBack(i);
        while (index.front() + windowLen <= i) {
            // 将队列设计成存储index的原因在此
            // 同时, 将队列存储成index, 其本质不是递减对列, 是"队列元素指向对象递减",是逻辑上的递减队列
            // 所以不能把这个构造递减队列的操作抽成一个类, 因为是逻辑上的递减
            // 除非外界给出cmp比较函数
            index.popFront();
        }
        cout << data[index.front()] << " " << flush;
    } else if (data[index.back()] < c) {
        index.popBack();
        i--;
    }
}
```

## 四色问题

地图染四色

### 思路

不断尝试, 出现错误就退回

### 需求

求所有解? 证明存在解? 

### 流程

1.  给地图各个子区块标上标号
2.  用邻接矩阵描述地图
    -   邻接矩阵, 二维表, 维度x: 所有区块的标号; 维度y: 所有区块的标号
    -   区块和区块之间相邻, 标注1
    -   区块和区块之间不相邻,  标注0
3.  给四色标号
4.  按顺序给区块标色, 再加一个无色
5.  给所有区块初始化为无色
6.  遍历色块, 给区块 i , 标色号
7.  用邻接矩阵检查是否有重合: 无重合, 进入6, 向下一个色块遍历; 否则, 进入8
8.  获取地i个区块的色号, 更换色号表
9.  把获取到的色块及其对应色号, 进入7, 直到找到能通过邻接矩阵的色号: 找到了, 进入6; 如果没有找到, 进入9
10.  获取i-1的色块, 获取i-1色块的色号, 遍历色号, 更换i-1色号的色块, 进入8

## 电路板布线

![布线问题-CSDN博客](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Design/Data-Structure/container/Day05-Stack&Queue/20200527131114375.png)

### 栈实现

有四种走法: 上, 下, 左, 右

优先走上, 不断走, 遇到不能走的, 退回一格, 选择下走, 可以了, 继续; 不可以, 退回, 走左....

这个思路, 有所谓"优先走上", 此谓之"深度优先搜索"


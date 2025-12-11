# KMP

>   **K**nuth，**M**orris和**P**ratt三位学者发明

## 问题来源

在长字符串A中找pattern串B出现的第一个位置, 如果要退回, 希望不是退到头, 而是尽可能少退一点, 怎么退能少退一点呢?

![KMP](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Design/Data-Structure/container/Day04-KMP/KMP精讲2.gif)

不止是子串, 子数组也是一样的

## 前缀表

记录了模式串与主串(文本串)不匹配的时候，模式串应该从哪里开始重新匹配

### 记录方式

最长公共前后缀

-   前缀
    -   不包含最后一个字符的所有以第一个字符开头的连续子串
    -   `aabaaf`->`aabaa`, `aaba`, `aab`, `aa`, `a`
-   后缀
    -   不包含第一个字符的所有以最后一个字符结尾的连续子
    -   `aabaaf`->`abaaf`, `baaf`, `aaf`, `af`, `f`
-   最长公共前后缀/最长相等前后缀(部分说法)
    -   `a`->pre=post:` ` -> 最长相等前后缀长度为0
    -   `aa`->pre=post:`a`-> 最长相等前后缀长度为1
    -   `aaa`->pre=post:`aa`-> 最长相等前后缀长度为2
    -   `aaba`->pre=post:`a`-> 最长相等前后缀长度为1
    -   `aabaa`->pre=post:`aa`-> 最长相等前后缀长度为2
    -   `aabaaf`->pre=post:` `-> 最长相等前后缀长度为0

从前往后查看, 得出前缀表

![KMP](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Design/Data-Structure/container/Day04-KMP/KMP精讲8.png)

## 前缀表的使用

![KMP](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Design/Data-Structure/container/Day04-KMP/KMP精讲2.gif)

1.  一一比对pattern的 j 索引所在和 src 的 i 索引所在
2.  值不相等, 进入3
3.  此时的前缀是`aabaa`, 在前缀表的索引4的地方记载了`aabaa`的*最长相等前后缀长度*
4.  看前一位的前缀表的值, 是2
5.  说明`aabaa`的*最长相等前后缀长度* 是2
6.  依据2, 若要取`aabaa`的长度2的前缀`aa`和长度2的后缀`aa`, 可以保证前缀`aa`和前缀`aa`是相等的
7.  如果我们**把 pattern 的 j 索引退回**到parttern索引为**2** 的位置, **意味着`aabaa`后缀`aa`此时被抛弃, 前缀`aa`被保留, j指向那个前缀的后一个位置**
8.  能保证此时的位置就是合适的退回位置(待证)
    1.  考虑此时退回到的位置的值和src 的 i 索引所在的值相等
    2.  设退回到的索引位置是m, 那么parttern[0:m+1]的前面的最大前缀就是parttern[0:m]
    3.  下证, src中的 i 索引前面 src[i-m:i] 和 parttern[0:m] 总是完全相等
    4.  
9.  比较此时这个 **pattern的 j 索引所在和 src 的 i 索引所在**进行比较, 这个比较工作交给Step1. 来完成

## 构建前缀表

将构建的过程把pattern看作src, 和pattern进行比对

```cpp
// 初始化前缀表
for (int i = 1, j = 0; i < pattern.length(); i++) { // 注意i从1开始
    while (j > 0 && pattern[i] != pattern[j]) { 
        // 前后缀不相同了, 需要依据前缀表后退
        // 一直不同就一直后退
    }
    if (pattern[i] == pattern[j]) { // pattern[i]看作src, pattern[j]看作pattern
        // 找到相同的前后缀时
    }
    // 给这个前缀表赋值
}
```

初始化前缀表

```cpp
int pre[pattern.length()];
pre[0] = 0;  // 一定是0
```

给这个前缀表赋值, 此时 j 就是需要退回的位置, 就是 *最长相等前后缀长度* 

```cpp
pre[i] = j;
```

找到相同的前后缀时

```cpp
if (pattern[i] == pattern[j]) {
    // 找到相同的前后缀时
    j++; // parttern 往后走一格
}
```
前后缀不相同了, 需要依据前缀表后退

```cpp
while (j > 0 && pattern[i] != pattern[j]) { 
    // 前后缀不相同了, 需要依据前缀表后退
    // 一直不同就一直后退
    j = pre[j-1];
}
```

## Next数组

网传的next版本, 是更改 j 和next[j] 的定义, 来达到next[j]是回退位置的目的

这样降低了可读性, 使变量意义不明😓

但是我就是想要:

在构建前缀表的时候, 所有*最长相等前后缀长度*往后移一格, 第一个置成-1

改变next数组的定义, 来达到next[j]是回退位置的目的

```cpp
int next[pattern.length()];
next[0] = -1; // 不需要定义, 随便他, 反正不会被访问到
next[1] = 0;  // 一定是0
for (int i = 1, j = 0; i < pattern.length(); i++) { // 注意i从1开始
    while (j > 0 && pattern[i] != pattern[j]) { // 前后缀不相同了
        j = next[j] ; // 向前回退
        // 不必担心j=0, 因为while(j>0)
    }
    if (pattern[i] == pattern[j]) { // 找到相同的前后缀
        j++;
    }
    next[i + 1] = j /*- 1*/; // 将j（前缀的长度）赋给next[i+1], 就是next后移一位
}
```

## 时间复杂度

src只遍历一遍O(len(src))

构建Next数组O(len(pattern))

## 局限

仔细想想, 这对parttern的要求还挺高的, 极大概率下, 前缀表的每个元素都是0, 或绝大部分元素都是0, 极少部分是1, 其余有2及以上的都可以忽略不记了

如果数据是随机的, parttern越长, 前缀表出现有价值的较大值的可能性就月底

如果pattern越短, 少犯心思从头开始遍历, 效率说不定更高

## 整体实现

```cpp
int getFirst(const string &src, const string &pattern) {
    int next[pattern.length()];
    next[0] = -1; // 不需要定义, 随便他, 反正不会被访问到
    next[1] = 0;  // 一定是0
    for (int i = 1, j = 0; i < pattern.length(); i++) { // 注意i从1开始
        while (j > 0 && pattern[i] != pattern[j]) { // 前后缀不相同了
            j = next[j]; // 向前回退
            // 前后缀不相同了, 需要依据前缀表后退
            // 一直不同就一直后退
        }
        if (pattern[i] == pattern[j]) { // 找到相同的前后缀
            j++;
        }
        next[i + 1] = j /*- 1*/; // 将j（前缀的长度）赋给next[i+1], 就是next后移一位
    }
    for (int i = 0, j = 0; i < src.length(); i++) {
        while (j > 0 && src[i] != pattern[j]) {
            j = next[j];
            // 前后缀不相同了, 需要依据前缀表后退
            // 一直不同就一直后退
        }
        if (src[i] == pattern[j]) {
            // 找到相同的前后缀
            j++;
        }
        if (j == pattern.length()) {
            // 此时匹配, 由于被匹配, 此时j指向pattern最后的下一个
            return i - j + 1;
        }
    }
    return -1;
}
```

## 重复的字串

```cpp
int * getNext(const string& pattern, const int* next){
    next[0] = -1; // 不需要定义, 随便他, 反正不会被访问到
    next[1] = 0;  // 一定是0
    for (int i = 1, j = 0; i < pattern.length(); i++) { // 注意i从1开始
        while (j > 0 && pattern[i] != pattern[j]) { // 前后缀不相同了
            j = next[j]; // 向前回退
            // 前后缀不相同了, 需要依据前缀表后退
            // 一直不同就一直后退
        }
        if (pattern[i] == pattern[j]) { // 找到相同的前后缀
            j++;
        }
        next[i + 1] = j /*- 1*/; // 将j（前缀的长度）赋给next[i+1], 就是next后移一位
    }
    return next;
}
int getFirst(const char* src,int begin,int len, const string &pattern, const int* next) {
    for (int i = 0, j = 0; i < len; i++) {
        while (j > 0 && src[i] != pattern[j]) {
            j = next[j];
            // 前后缀不相同了, 需要依据前缀表后退
            // 一直不同就一直后退
        }
        if (src[i] == pattern[j]) {
            // 找到相同的前后缀
            j++;
        }
        if (j == pattern.length()) {
            // 此时匹配, 由于被匹配, 此时j指向pattern最后的下一个
            return i - j + 1; // 找重复字符串也可以这里改变-> 不return而是count++
        }
    }
    return -1;
}
```


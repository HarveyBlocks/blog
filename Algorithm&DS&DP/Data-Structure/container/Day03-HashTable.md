# HashTable

一个数组, 数组元素是一个个链表的头指针

链表的节点里真正存储数据

-   判断是否存在

##Hash值和Hash函数

相同的数据总能得出相同的Hash值

$$
hashFunction(obj)->hashCode\\
index = hashCode \% tableLen
$$


```C
typedef unsigned long long t_HashCode;
template<typename T>
t_HashCode Hash::hashFunction(const T *obj) {
    if (obj == nullptr || sizeof(*obj) == 1L) {
        return 0;
    }
    int len = sizeof(*obj) / 8 + (sizeof(*obj) % 8 != 0);
    auto *p = (t_HashCode *) obj;
    return Hash::doXor(p, len);
}
t_HashCode Hash::doXor(const t_HashCode *p, int len) {
    t_HashCode hashCode = 0L;
    for (int i = 0; i < len; ++i) {
        hashCode ^= p[i];
    }
    return hashCode;
}

```

## Hash冲突/Hash碰撞

要存到一张表里, 不同的值hashCode却一样了

应对方式:

-   拉链法 节点存链表
-   线性探测法
    1.  开辟大小为m的数组
    2.  p为最接近m的质数
    3.  index=hashCode%p
    4.  index没数据, 填入
    5.  index有数据, 往后一格, 返回Step.4

##例题

###有效的字母异位词

判断一个字符串A能不能把字母重组成字符串B, 只考虑全小写

一开始想着

1.  创建长度26的数组A, 字母做索引(这一步其实就是Hash)
2.  遍历字符串A, 数组A[字符]++
3.  创建长度26的数组B, 字母做索引
4.  遍历字符串B, 数组B[字符]++
5.  比对数组A, 数组B

其实只要

1.  创建长度26的数组, 字母做索引(这一步其实就是Hash)
2.  遍历字符串A, 数组[字符]++
3.  遍历字符串B, 数组[字符]--, 出现数组[字符]<0, 就是直接返回不符合了
4.  遍历数组, 出现非0就是不符合了



###求交集

同理, 两个数组(一个数组里也含重复元素), 求交集

数组元素X范围是0-1000

8*125, 开辟大小125的字节数组

```C
if(!字节数组[X%125]&(1<<(X/125))){ // 第(X/125)位是0的情况
    字节数组[X%125] =字节数组[X%125]+1<<(X/125); // 置为1
}
```

### 快乐数

[快乐数](https://leetcode.cn/problems/happy-number/)

```
输入：19
输出：true
解释：
1^2 + 9^2 = 82
8^2 + 2^2 = 68
6^2 + 8^2 = 100
1^2 + 0^2 + 0^2 = 1
```

这种运算可能存在无限循环

有必要存储过程中出现过的值

任意一个int值, 在第一轮运算之后, 顶多三位数, 然后第二次运算后最多300, 第三次最多三十..或许开辟的空间可以小一点, 例如在运行5次之后再存, 这样即使循环, 也不会遗漏

### 两数之和

[力扣](https://leetcode.cn/problems/two-sum/)

给定一个整数数组 nums 和一个目标值 target

在数组中找出和为目标值的那两个**整数，并返回他们的数组下标。

假设每种输入只会对应一个答案。但是，数组中同一个元素不能使用两遍。

**示例:**

给定 nums = [2, 7, 11, 15], target = 9

因为 nums[0] + nums[1] = 2 + 7 = 9

所以返回 [0, 1]



思路:

依据数组构建Hash表hash[nums.length]

hash.find(target-nums[i]); 如果存在, 在nums中找到target-nums[i], 返回[i,j]

时间复杂度O(n+1)

###四数相加

[力扣](https://leetcode.cn/problems/4sum-ii/)

给定四个包含整数的数组列表 A , B , C , D ,计算有多少个元组 (i, j, k, l) ，使得 A[i] + B[j] + C[k] + D[l] = 0。

为了使问题简单化，所有的 A, B, C, D 具有相同的长度 N，且 0 ≤ N ≤ 500 。所有整数的范围在 -2^28 到 2^28 - 1 之间，最终结果不会超过 2^31 - 1 。

**例如:**

输入:

-   A = [ 1, 2]
-   B = [-2,-1]
-   C = [-1, 2]
-   D = [ 0, 2]

输出:

2

**解释:**

两个元组如下:

1.  (0, 0, 0, 1) -> A[0] + B[0] + C[0] + D[1] = 1 + (-2) + (-1) + 2 = 0
2.  (1, 1, 0, 0) -> A[1] + B[1] + C[0] + D[0] = 2 + (-1) + (-1) + 0 = 0

**思路**

1.  map，key放 a+b，value 放a+b的值出现的次数。
2.  遍历大A和大B数组，key-value到map中。
3.  count，统计 a+b+c+d = 0 出现的次数。
4.  遍历大C和大D数组，如果 0-(c+d) 在map中存在，count+=map.get(key)
5.  返回count

如题: **最终结果不会超过 2^31 - 1** 如果A+B>2^31 - 1为之奈何? 如果C+D>2^32-1为止奈何?

### 赎金信

[力扣](https://leetcode.cn/problems/ransom-note/)

canConstruct("a", "b") -> false
canConstruct("aa", "ab") -> false
canConstruct("aa", "aab") -> true

第一个字符串里的所有字母都可以从第二个字符串中找到, 思路参考有效字母异位词

### 第15题. 三数之和

[力扣题目链接](https://leetcode.cn/problems/3sum/)

给你一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？请你找出所有满足条件且不重复的三元组。

**注意：** 答案中不可以包含重复的三元组。

示例：

给定数组 nums = [-1, 0, 1, 2, -1, -4]，

满足要求的三元组集合为： [ [-1, 0, 1], [-1, -1, 2] ]



-   同一个数组里要求不能有重复的三元组, 用双指针(多指针)
-   不同数组里要求不能有重复的元素, 用Hash表

```python
for i in range(2,len(nums)):
    for j in range(1,i):
        for k in range(0,j):
            if(sum(nums[i],nums[j],nums[j])==0):
                # 就是的, 存i,j,k
```

-   这题的Hash, 就是找是否存在0-nums[i]-nums[j], map\<Num,Count\> 要另外开辟空间
-   时间复杂度都是O(n^2^)
-   如果排序之后, 重复的元素以个数的形式存在, 那么在找数据就会快一些(例如在三个数中最小值>0的都不用判断), 只是这个排序需要消耗一定的时间了, 怎么取舍主要看数据特点
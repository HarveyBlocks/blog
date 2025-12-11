# 基数排序

>   Radix Sort

基于计数排序

## 思路

![image-20240619145529865](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Design/algorithm/sort/Day03-基数排序/image-20240619145529865.png)

低位先排, 高位后排

高位相等看低位, 但是低位先排, 再由计数排序的特点, 保证了低位一定是升序的

## 实现

对于int, 其由四个字节组成, 一个字节看作一个计数, 创建计数数组, 计数数组的长度为256(基数为256)

```cpp
/**
 * @example
 * sortByLabel(4,0)-><p>
 * label = 0xff_ff_ff_ff<p>
 *          3   2   1   0<p>
 * sorted by element&label
 */
void sortByLabel(int startByteIndex, int endByteIndex) {
    if (startByteIndex <= endByteIndex || startByteIndex < 0 || endByteIndex < 0) {
        throw IllegalArgumentException(
                "startByteIndex=" + to_string(startByteIndex)
                + " & endByteIndex=" + to_string(endByteIndex));
    }
    int scope = 256;
    int len = this->arr.length();
    int indexLib_[scope];
    Array<int> indexLib(indexLib_, scope);
    for (int x = endByteIndex; x < startByteIndex; ++x) {
        for (int i = 0; i < scope; ++i) {
            indexLib[i] = 0;
        }
        for (int i = 0; i < len; ++i) {
            indexLib[catchByte(this->arr[i], x)]++; // 统计数量
        }
        indexLib[0]--;
        for (int i = 1; i < scope; ++i) {
            indexLib[i] += indexLib[i - 1];
        }
        unsigned int result_[len];
        Array<unsigned int> result(result_, len);
        for (int i = len - 1; i >= 0; --i) {
            unsigned int element = this->arr[i]; // 210ms
            result[indexLib[catchByte(element, x)]--] = element; // 530ms
        } // 740ms
        Array<unsigned int>::copy(result, arr); // 200ms
    }
}

void sort() override {
    sortByLabel(4, 0); // sortByLabel(sizeof(element), 0);
    // 即可以底层编码为值排序(应该很少会有这种情况)
    // 所以也可以让所有实现了code()方法的类作为可排序的对象, 
    // 或者提供一个参数, 参数提供一个函数mapper(), 能让元素对象映射转化为code
    // code里是用户自定义的值至于返回值必须限定为无符号...还是有点...
    // 要不多做几步调整正负数数量, 问题在于找到正负数边界要遍历一遍, 转换正负数要遍历两遍....
}
```

```cpp
static unsigned char catchByte(unsigned int ele, unsigned int byteIndex) {
    return (unsigned char) ((ele >> (byteIndex * 8U)) & 0xffU);
}
```

由于是基于int的底层编码进行排序的, 所以如果比较负数的化, 负数会普遍比正数大, 而正数和负数各自小范围内是递增的

其实只要在最后互换正负数的位置即可, 算法参考[右旋字符串](..\..\Data-Struction\container\Day03-String.md)

缺点在于不是原址排序, 消耗了两倍内存

最小分辨单位是字节

```cpp
template<class O, class C=unsigned long long>
class Encoder {
public:
    virtual C encode(O obj) = 0;
};
```

```cpp
class IntegerEncoder : public Encoder<unsigned int, int> {
public:
    int encode(unsigned int obj) override {
        // 通过这种负相关的编码形式实现倒序排序
        return 256-(int) obj;
    }
};
```


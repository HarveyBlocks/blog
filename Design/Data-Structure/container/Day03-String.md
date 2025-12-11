# 字符串

## 替换数字

字符串`a1b2c`中所有数子替换成`number`

思考, 数组增长元素, 如何减少移动次数

```python
numberLen: int = len("number")
for i in range(len(string)):
    if i is number:
        for j in range(i, i+6):

```

## 右旋字符串

```text
2
abcdefg 
```

->

```text
fgabcde
```

要求:

1.  不申请新内存
2.  减少移动次数(`第一轮g移到最前, 第二轮f移到最前`这种策略不能采用)

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Design/Data-Structure/container/Day03-String/image-20240615203841717.png" alt="image-20240615203841717" style="zoom:50%;" />

1.  reverse(string[0:length])
2.  reverse(string[0:n])
3.  reverse(string[n:len])
4.  倒转可以用头尾各一个指针, 双指针交换, 逐渐向中间靠近


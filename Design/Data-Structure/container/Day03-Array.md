# 数组

-   内存连续存储(申请大片内存)
-   擅长随机查找
-   增删慢

## 操作

### 查

```c
char getEle(char *arr, int len, int index) {
    if (len <= index) {
        return -1;
    }
    return arr[index];
}
```

### 遍历

```c
void showArr(char *arr, int len) {
    for (int i = 0; i < len - 1; ++i) {
        printf("%d,", arr[i]);
    }
    printf("%d\n", arr[len - 1]);
}
```

### 删

**数组的元素是不能删的，只能覆盖。**

#### 依据索引删

```c
void removeEle(char *arr, int len, int index) {
    if (len <= index) {
        return;
    }
    for (int i = index; i < len - 1; ++i) {
        arr[i] = arr[i + 1];
    }
}
```

#### 依据元素删

考虑到一个数组里有相同的元素

双指针法

```c
int removeEleByValue(char *arr, int len, char value) {
    int j = 0;
    for (int i = 0; i < len; ++i) {
        if (arr[i] != value) {
            arr[j] = arr[i];
            j++;
        }
    }
    return len - j; // 返回删除元素个数
}
```

### 增

```c
void insertEle(char *arr, int len, int index, char value) {
    if (len <= index) {
        return;
    }
    for (int i = len - 1; i > index; --i) {
        arr[i] = arr[i - 1];
    }
    arr[index] = value;
}
```

## 二维数组

```c
char arr[M][N] = {};
char (arr[M])[N] = {};
```

`array[m][n] `== `array[m*N+n]` 

## 滑动窗口

[长度最小的子数组](https://leetcode.cn/problems/minimum-size-subarray-sum/)

```c
int minSubArrayLen(int target, int *nums, int numsSize) {
    int end = 0, start = 0;
    int result = numsSize + 1;
    int sum = 0;
    while (end < numsSize) {
        if (sum >= target) {
            int temp = end - start;
            if (temp < result) {
                result = temp;
            }
            sum -= nums[start++];
        } else {
            sum += nums[end++];
        }
    }
    while (start <= end && sum >= target) {
        int temp = end - start;
        if (temp < result) {
            result = temp;
        }
        sum -= nums[start++];
    }
    return result == numsSize + 1 ? 0 : result;
}
```


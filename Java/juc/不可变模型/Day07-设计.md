# 设计不可变类

1.  final修饰该类
2.  类中所有属性设置final

## 保护性拷贝

```java
public String(char value[]) {
    this(value, 0, value.length, null);
}
String(char[] value, int off, int len, Void sig) {
	// 健壮性判断
    this.value = StringUTF16.toBytes(value, off, len);
}
```

外界传入数组, 为了防止数组的元素在外界发生改变影响类内的字段

```java
@HotSpotIntrinsicCandidate
public static byte[] toBytes(char[] value, int off, int len) {
    byte[] val = newBytesFor(len);
    for (int i = 0; i < len; i++) {
        // 拷贝
        putChar(val, i, value[off]);
        off++;
    }
    return val;
}
```

使用拷贝保证不可变类的引用对象值的不可变性

### 存在问题

反复拷贝导致性能降低

解决: [享元模式](../设计模式/Day07-享元.md)

## 无状态

没有字段的类自然是不可变类, 自然是线程安全的了


# 泛型

## 声明泛型

```cpp
template<typename P,class R,typename T>
```



如果方法实现和声明分开来了

实现泛型类的方法的时候需要声明该类使用了泛型

```cpp
template<typename T> // 名字可以不一样
T *MyPtr<T>::get() {
    cout << "get" << endl;
    if (valuePtr != nullptr) {
        count++;
    }
    return valuePtr;
}
```




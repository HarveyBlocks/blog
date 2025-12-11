# 生成器

生成数据, 每次生成的数据都使用同一块内存, 就不会占用太多资源



## 创建生成器



### 生成器推导式

与列表推导式类似

```python
data = (i for i in range(1000))
```



-   节省空间

    ```python
    if __name__ == '__main__':
        data = (i for i in range(1000))
        print(data)  # <generator object <genexpr> at 0x00000195FF303510>
        print(type(data))  # <class 'generator'>
        print(data.__sizeof__())  # 96
    
        ls = [i for i in range(1000)]
        print(ls)  
        print(type(ls))  #<class 'list'>
        print(ls.__sizeof__())  # 8840
    ```

-   获取其中的数据

    ```python
    if __name__ == '__main__':
        data = (i for i in range(1000))
        print(next(data))
        print(next(data))
        print(next(data))
    ```

    ```python
    if __name__ == '__main__':
        data = (i for i in range(1000))
        for i in data:
            print(i)
        print(next(data))  # 报错: StopIteration
    ```







### yield关键字

在函数中出现`yield`

-   在代码执行到`yield`时会暂停生成, 然后将值返回出去
-   下次启动生成器时会在暂停的位置继续往下执行
-   生成器如果把数据生成啊我那次, 再次获取生成器的下一个数据会抛一个`StopInteration`异常, 表示停止迭代异常
-   wile循环内部如果没有处理异常操作, 需要手动添加处理异常操作
-   **for循环内部自动处理了停止迭代异常**, 使用起来更加方便

```python
def generator(limit: int):
    for i in range(limit):
        print("开始生成")
        yield i
        print("生成完成")


if __name__ == '__main__':
    g = generator(3)
    print(g)
    print(next(g))  # 0
    print(next(g))  # 1
    print(next(g))  # 2
    # print(next(g)) # StopIteration
```

可用调试看看执行的顺序

其实生成器存储了一个代码执行状态, 记录了上一次执行到的位置


# Tensor

>   张量

###创建

-   创建空张量

    ```python
    x = torch.empty(5,4)
    print(x)
    ```

    ```python
    tensor([[0., 0., 0., 0.],
            [0., 0., 0., 0.],
            [0., 0., 0., 0.],
            [0., 0., 0., 0.],
            [0., 0., 0., 0.]])
    ```

-   全0张量

    ```python
    x = torch.zeros(5,4,detype = torch.float32)
    ```

-   随机

    ```python
    x = torch.rand((5,3))
    ```

-   自定张量

    ```python
    x = torch.tensor([1,1,3],dtype = torch.float32)
    ```

-   np转tensor

    ```python
    x = torch.tensor(np.random.rand(2,4))
    ```

    



### 填充

-   填充1

    ```python
    x = torch.empty(5,3,dtype = torch.float32)
    x = x.new_ones(5,3)
    ```



### 返回大小

```python
x = torch.rand((5,3))
print(x.size())
# torch.Size([5, 3])
```


### 计算

加法

```python
x = torch.rand((5,3))
y = torch.rand((5,1))
print(x+y)
print(torch.add(x, y))
```

矩阵乘法

```python
x = torch.rand((5,3))
y = torch.rand((3,6))
result = torch.mm(x, y)
print(result) 
print(result.size()) # [5, 6]
```



### 索引

```python
x = torch.rand((5,3))
print(x[:,:])
print(x[:3])
print(x[:,:2])
```




### 改变矩阵维度

```python
x = torch.rand((5,3))
y = x.view(-1,5)
print(x.size(),y.size()) 
# torch.Size([5, 3]) torch.Size([3, 5])
```


```python
x = torch.rand((5,3))
print(x)
print(x.T)
print(x.H)
```

## Tensor的常见形式

-    `scalar ` 数

    ```python
    x = torch.tensor(42)
    ```

-   `vector`

-    `martix`

-   `n-dimensional tensor`


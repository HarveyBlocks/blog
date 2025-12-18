# Geometry

显式表示

- 例子
  - 点云
  - 多边形表格
  - 细分曲面
- 使用场景

隐式表示

- 水平集
- 代数曲面
- L系统

## 隐式

### Boolean 融合

构造实体几何, 使用Boolean表达式链接多个简单的几何体, 来描述一个复杂的几何体

![image-20251210152738210](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210152738210.png)

### Blobby Surfaces

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210154756152.png" alt="image-20251210154756152" style="zoom:50%;" />

P点的高斯中心

$$
\phi_p(x) := e^{-(x-p)^2}
$$

使用高斯中心之和来表示某两个点的融合

$$
f := \phi_p + \phi_q
$$

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210154230482.png" alt="image-20251210154230482" style="zoom:50%;" />

在 $f$ 上, 取不同的高度, 来投影到二维平面上, 查看不同半径的圆的融合

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210154811807.png" alt="image-20251210154811807" style="zoom:50%;" />

如果想融合两个非圆形的实体, 可以使用距离公式

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210160041252.png" alt="image-20251210160041252" style="zoom:50%;" />

末尾的 $0.5$ 可选择其他合适的值

$$
f(x) := e^{d_1(x)^2}+e^{d_2(x)^2}-0.5
$$

### 水平集

依据解析式对采样点采样, 获取到图像信息

在其中有混叠(aliasing)的问题

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210160330500.png" alt="image-20251210160330500" style="zoom:50%;" />

水平集存储数据量的成本也大幅提升, 一般存储表面的一部分信息, 并不存储所有信息, 以此降低计算量和存储压力

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210160524110.png" alt="image-20251210160524110" style="zoom:50%;" />

### 使用场景

- 使用场景
  - 测试是否在表面上, 是否在体内, 是否在体外
  - 描述目标占用的空间非常小
  - 容易获取到便面的距离, 可以用于测试是否碰撞
  - 完全精确的描述, 不需要考虑混叠 
  - 采用网格(水平集), 较大的存储成本, 但是能描述比较复杂的目标, 例如流体
- 缺点
  - 难以绘制形状中的所有点
  - 复杂图形的建模困难

## 显式

### 点云

点的集合

可以依据需求,对每个点附带特殊信息, 例如法向量, 颜色

可以表示任何几何体

对点的采样如果足够密集 ( $\gg 1 \cdot poxel$ ), 则可以达到比较好的效果

如果采集不够密集, 则考虑如何填充空白空间

难以描述点之间的关系, 因此能做到的进一步计算(例如目标的变化) 就及其困难

### 多边形网络

典型就是用足够多的三角形取近似一个复杂的目标几何

相较于点云, 不止存储点, 还存储了点之间的关系(三角形的边)

有助于点知道另一个点在哪里

可以只在需要的地方放置大量细节, 不需要的地方可以简化

例如下面的球壳部分是比较细致的, 柱体部分就用细长的三角形简化

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210162236414.png" alt="image-20251210162236414" style="zoom: 33%;" />

缺点是数据结构变得更加复杂, 需要考虑如何链接, 要考虑各种情况, 特别是特殊的边界情形

使用 $(x,y,z)$ 来描述顶点的位置, 使用 对顶点的引用来描述三角形, 三角形表面的内容可以使用 **重心插值** 来描述

可以理解成**对点云的线性插值**

## Bézier Curve

贝塞尔曲线

### Hermite 样条

设参数区间 $ u_0 = 0, \; u_1 = 1 $ 在端点处给定位置和导数值 $x(u_0) = x_0, \; x(u_1) = x_1$
$$
\frac{\partial}{\partial u} x(u) \bigg|_{u_0} = d_0, \;
\frac{\partial}{\partial u} x(u) \bigg|_{u_1} = d_1
$$

多项式系数向量 $\mathbf{P}$ 与 Hermite 数据向量 $\mathbf{h}$ 之间的关系为：

$$
\mathbf{h} = \begin{bmatrix}
x_0 & x_1  & d_0  & d_1 
\end{bmatrix}^T
$$


三次多项式可表示为：
$$
x(u) = c_0 + c_1 u + c_2 u^2 + c_3 u^3
$$

定义基向量：
$$
\mathscr{P}_3(u) = \begin{bmatrix} 1 & u & u^2 & u^3 \end{bmatrix}
$$

矩阵 $ \beta_H $ 为将 Hermite 形式（端点位置与导数）转换为三次贝塞尔曲线多项式系数的转换矩阵
$$
\beta_H = \begin{bmatrix} 
1 & 0 & 0 & 0 \\ 
0 & 0 & 1 & 0 \\
-3 & 3 & -2 & -1 \\ 
2 & -2 & 1 & 1 
\end{bmatrix} 
$$

则：
$$
x(u) = \mathscr{P}_3(u) \cdot \beta_H \cdot \mathbf{h}
$$

如果把 $\mathcal{P}_3(u) \cdot \beta_H$ 提出来, 可以获取Hermite 样条
$$
\left\{\begin{aligned}
H_0(t) \;&=\; 2t^3-3t^2+1\\
H_1(t) \;&=\; -2t^3+3t^2\\
H_2(t) \;&=\; t^3-2t^2+t\\
H_3(t) \;&=\; t^3-t^2\\

\end{aligned}\right.
$$
那么多项式 $P(t)$ 可以写成
$$
P(t) = h_0H_0(t)+h_1H_1(t)+h_2H_2(t)+h_3H_3(t)
$$

### Catmull-Rom

点的导数往往难以获取, 故可以使用 Catmull-Rom 的生成切线

对于**连续** (间隔 $\Delta = 1$ ) 控制点 $\mathbf{P}_0, \mathbf{P}_1, \mathbf{P}_2, \mathbf{P}_3$ 定义的曲线段，实际插值的是从 $\mathbf{P}_1$ 到 $\mathbf{P}_2$ 的部分

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251217230329526.png" alt="image-20251217230329526" style="zoom: 25%;" />

可以使用以下方式生成切线: 
$$
\begin{aligned}
\mathbf{T}_1 &= \frac{\mathbf{P}_2 - \mathbf{P}_0}{2} \quad &\text{(起点 } \mathbf{P}_1 \text{ 处的切线)} \\
\mathbf{T}_2 &= \frac{\mathbf{P}_3 - \mathbf{P}_1}{2} \quad &\text{(终点 } \mathbf{P}_2 \text{ 处的切线)}
\end{aligned}
$$

将上述切线代入 Hermite 曲线公式：

$$
\mathbf{C}(t) = H_0(t) \mathbf{P}_1 + H_1(t) \mathbf{P}_2 + H_2(t) \mathbf{T}_1 + H_3(t) \mathbf{T}_2
$$



### 伯恩斯坦基底

一个选取概率的操作

$$
B^n_k(t) := C_n^k t^k(1-t)^{n-k}
$$

使用伯恩斯坦基底可以构造曲线

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210165144789.png" alt="image-20251210165144789" style="zoom: 50%;" />

贝塞尔曲线就是使用伯恩斯坦基底构造的曲线

### 分段贝塞尔

定义控制点$p_k$

$$
\gamma(s) := \sum_{k=0}^{n} B^n_k(s)p_k
$$

- $n=1$: 一条线段

- $n=3$: 三次贝塞尔曲线

  <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210165656868.png" alt="image-20251210165656868" style="zoom:50%;" />

  经过点 $p_0, p_3$

  在端点和线 $p_0p_1, p_2p_3$相切

  包含在控制点的**凸包**中

- $n=k$: 控制点太多, 难以控制

  <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210165928658.png" alt="image-20251210165928658" style="zoom:50%;" />

一般偏向于使用多个分段的三次贝塞尔曲线拼接起来



### 构建分段三次贝塞尔曲线/曲面

目标, 连续平滑的曲线

- 线段端点相遇
- 端点切线相遇

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210170314783.png" alt="image-20251210170314783" style="zoom:50%;" />

对于曲面构建, 伯恩斯坦基底

$$
B_{i,j}^3 := B_{i}^3 B_{j}^3 
$$

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210170835510.png" alt="image-20251210170835510" style="zoom:50%;" />

获取贝塞尔曲面片, 定义空间中的控制点 $p_{ij}$

$$
S(u,v) := \sum_{i=0}^3\sum_{j=0}^3B^3_{i,j}(u,v)p_{i,j}
$$

### Cubic Bézier

三次贝塞尔曲线由 4 个控制点 $\mathbf{P}_0, \mathbf{P}_1, \mathbf{P}_2, \mathbf{P}_3$ 定义，是参数 $t \in [0, 1]$ 下的向量函数。

$\mathbf{P}_0, \mathbf{P}_3$ 定义曲线的起点和终点

$\mathbf{T}_0 = 3(\mathbf{P}_1 - \mathbf{P}_0)$， $\mathbf{T}_3 = 3(\mathbf{P}_3 - \mathbf{P}_2)$ 定义曲线在两段的切线

曲线用三次伯恩斯坦多项式作为混合函数：

$$
\mathbf{C}(t) = \sum_{i=0}^{3} B_i^3(t) \, \mathbf{P}_i
$$

其中：

$$
\begin{aligned}
B_0^3(t) &= (1-t)^3 \\
B_1^3(t) &= 3t(1-t)^2 \\
B_2^3(t) &= 3t^2(1-t) \\
B_3^3(t) &= t^3
\end{aligned}
$$

给定 Hermite 端点 $\mathbf{P}_0, \mathbf{P}_3$ 及切向量 $\mathbf{T}_0, \mathbf{T}_3$，对应贝塞尔控制为：

$$
\mathbf{P}_1 = \mathbf{P}_0 + \frac{\mathbf{T}_0}{3}, \quad \mathbf{P}_2 = \mathbf{P}_3 - \frac{\mathbf{T}_3}{3}
$$

矩阵形式
$$
\mathbf{B}(u) = \mathcal{P}_3(u) \cdot \beta_z \cdot \mathbf{p}
$$

其中：


$$
M_{Z \to H} = \begin{bmatrix} 
1 & 0 & 0 & 0 \\ 
0 & 0 & 0 & 1 \\ 
-3 & 3 & 0 & 0 \\ 
0 & 0 & -3 & 3 
\end{bmatrix}
$$

以及
$$
\beta_z =\beta_H\times M_{Z \to H} = \begin{bmatrix} 
1 & 0 & 0 & 0 \\ 
-3 & 3 & 0 & 0 \\ 
3 & -6 & 3 & 0 \\ 
-1 & 3 & -3 & 1 
\end{bmatrix}
$$

### de Casteljau's算法

对于伯恩斯坦基底存在高次运算, 容易造成精度丢失

de Casteljau's 算法避免了高次运算造成的精度丢失

见算法

### 细分

可以采用的细分比例是杨辉三角中的 $\frac{1}{4},\frac{2}{4},\frac{1}{4}$

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210171912242.png" alt="image-20251210171912242" style="zoom:50%;" />

迭代地去做, 就可以极限出一条比较平滑的曲线/曲面

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210171950173.png" alt="image-20251210171950173" style="zoom: 50%;" />

曲面也可以近似

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210172034587.png" alt="image-20251210172034587" style="zoom: 50%;" />
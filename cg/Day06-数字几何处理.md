# 数字几何处理

1. scan
2. process
3. print

- **重构** **creconstruction** 例如 point clout->polygon mesh/level set

- **上采样** **Upsampling** 双线性插值

  <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210215556997.png" alt="image-20251210215556997" style="zoom:50%;" />

- **下采样** **Downsampling** 迭代的边塌陷算法, 用于希望将一个多变的集合体变得粗糙一些来存储

- **重采样** **Resampling** 用于提高各个元素的质量, 例如在多边形网格中, 如果将每个三角形单元都构建成等边三角形, 可能更有利于进行某些计算

- **滤波** **Filtering** 去除噪声, 强调某些特征, 进行边缘检测

- **压缩** **Compression** 有损压缩和无损压缩 

## Subdivision

细分曲面

用于在网格中进行Upsampling

- 将每个元素重复地分割成更小的部分
- 用相邻顶点位置的加权平均来替换顶点位置
- 新生成的点是规则的, 同时把旧的点的坐标拉向规则
- 目标
  - 插值 or 近似, 是否需要总是经过细分前的所有点(看需求)
  - 极限曲面需要有怎样的连续性($C^1, C^2...$)
  - 在不规则顶点出的表现, 如果出现了非常高阶的顶点怎么办?
- 实现方法
  - 对于四边形网格,  Catmull Cark
  - 对于三角形网格, Loop, Buterrfly, Sqrt

### Loop

极限细分后的曲面曲率在不规则顶点之外将是连续的 $C^2$

1. 每个边取中点, 分割

   <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211025735751.png" alt="image-20251211025735751" style="zoom:50%;" />

2. 将每一个新边进行翻转

   <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211025830351.png" alt="image-20251211025830351" style="zoom:50%;" />

   此时, 每一个原三角形分成四个新三角形

   <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211024534013.png" alt="image-20251211024534013" style="zoom: 50%;" />

3. 新点的坐标是周围老坐标的加权平均

   <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211024747069.png" alt="image-20251211024747069" style="zoom: 50%;" />
   
   $$
   p := \sum_i\phi_ip_i
   $$

4. 对于旧点的新位置, 设 $n$ 为点的度数

   <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211024925893.png" alt="image-20251211024925893" style="zoom:50%;" />

   定义中间量 $u$
   
   $$
   u := \left\{\begin{align}
     \frac{3}{16} & &n = 3\\
     \frac{3}{8n} & &otherwise
   \end{align}\right.
   $$
   
   最终计算得旧点的新位置坐标为
   
   $$
   p'_0 = \sum_{i=1}^n{u \cdot p_i} + (1-n\cdot u) \cdot p_0
   $$

### Catmull Cark

不仅只适用于四边形网格, 对于任意多边形

1. 在其中插入一个点 $m$

   其坐标是周边各个点的加权平均
   
   $$
   p_m = \frac{1}{n} \sum_ip_i
   $$

2. 每个边取中点, 分割

3. 将每条边的中点和这个点 $m$ 相连

   效果: 利用插入的点将多边形变成多个四边形的组合

   <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211001125189.png" alt="image-20251211001125189" style="zoom:50%;" />

4. 计算顶点的新坐标

   - **n** : 顶点的度数, 即顶点旁边有几条边
   - **Q** : 顶点周围所有的表面上新增加的点(步骤1)的坐标的平均值
   - **R** : 顶点周围所有的边上新增加的点(步骤2)的坐标的平均值
   - **S** : 原始顶点位置

   $$
   Coords  := \frac{Q+2R+(n-3)S}{n}
   $$

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211023808342.png" alt="image-20251211023808342" style="zoom:67%;" />

Catmull Cark 也可以用于三角形网格, 但是效果不如Loop

因为Catmull Cark会将三角形网格转化成四边形网格, 而好的三角形网格的度数是6, 这放到四边形网格中是非规则的网格了

四边形网格好的度数是4

## Mesh Simplification

下采样, 简化多边形网格

边坍缩

迭代地坍缩, 使用贪心算法

给每个边赋予一个 $cost$ 值, 表示这条边对表面的改变量

计算出哪个边的 $cost$ 最小, 则删除这条边

使用合并来对网格进行坍缩

1. 找到新点 $m$

   评判影响使用点到平面的距离之和
   
   $$
   dist_i(x) := < \vec{N} ,  \vec{x}- \vec{p}>
   $$
   
   <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211032817419.png" alt="image-20251211032817419" style="zoom:50%;" />
   
   $$
   dist(x) := \sum_i{< \vec{N_i} ,  \vec{x}- \vec{p}>}
   $$
   
   <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211032945329.png" alt="image-20251211032945329" style="zoom:50%;" />

   下面给出运算最小值 $\vec{x}$ 的方法, 证明略(大概就是矩阵正定, 开平方这些)

   1. 设目标坐标 $\vec{x} = (x,y,z)$, 法向量坐标 $\vec{n} = (a,b,c)$, 偏移量 $d := <\vec{n},\vec{p}>$

   2. 在齐次坐标中定义 $\vec{u} = (x,y,z,1), \vec{v} = (a,b,c,d)$ 

   3. 定义矩阵 $K$, $cost = <u,v>^2 = u^T(vv^T)u =: u^TKu$

   4. 再分解 $K$, 定义 $B,\vec{w}$
      
	$$
   K = \left(\begin{array}{ll}
       a^2&ab&ac&ad\\
       ab&b^2&bc&bd\\
       ac&bc&c^2&cd\\
       ad&bd&dc&d^2
      \end{array} \right) :=\left(\begin{array}{ll}
       B&\vec{w}\\
       \vec{w}^T&d^2
      \end{array} \right)
   $$

   5. 在 $\vec{x}$ 满足下式时, $cost$ 最小
      
	$$
      \vec{x} = -B^{-1}\vec{w}
   $$

   6. 对于点 $p_i$ 的损失 $K_i$, 边 $e_{ij}$ 的损失 $K_{ij}$
      
	$$
      K_{ij} = K_i+K_j
   $$

   7. 比较每一个边的 $K_{ij}$, 直到找到最小的

2. $e_{ij}$ 两边的点 $v_i, v_j$ 关联的半边, 都与新点 $m$ 进行关联

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211032509066.png" alt="image-20251211032509066" style="zoom:50%;" />

从 $e_{ij}$ 坍缩到 $m$ 时存在问题

如果 $m$ 的位置不够好, 将会导致坍缩之后的三角形不规则

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211035245985.png" alt="image-20251211035245985" style="zoom:50%;" />

解决方法是如果坍缩之后的 $i$ 导致 $<\vec{N_{ijk}}, \vec{N_{kjl}}>$ 小于0, 则不进行坍缩

## Mesh Regularization

网格正规化

点的位置, 点的位置接近目标集合体的表面不一定就是好的, 希望法线也能和目标几何体的特定部分的法线近似

下面是一个构建网格时表面存在大量锯齿的例子, 这将导致网格估算出来的表面积和实际目标的表面积相差甚远

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210221628708.png" alt="image-20251210221628708" style="zoom: 67%;" />

表面的形状接近正边形的(例如等边三角形), 是比较好的单元形状

有助于提高数值精度和稳定性

### Delaunay

存在一种 $Delaunay$ 的特殊网格, 具有一些较好的性质

 $Delaunay$ 即所有三角形表面的外接圆内不存在其他三角形的顶点

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210222033580.png" alt="image-20251210222033580" style="zoom:50%;" />

好的Mesh应该拥有规则的**顶点的度** (degree), 有助于并发计算, 

### 度

**顶点的度**, 指接触该顶点的边的数量

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210222526173.png" alt="image-20251210222526173" style="zoom:50%;" />

subdivision 时, 过高的degree会导致瑕疵

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210222809493.png" alt="image-20251210222809493" style="zoom:50%;" />

合适的degree可以避免这一点

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210222741976.png" alt="image-20251210222741976" style="zoom:50%;" />

### 翻转优化

使用边翻转来优化度数, 使其接近六

方法是, 如果与6的总偏差减小了, 则进行牌男装

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211040011918.png" alt="image-20251211040011918" style="zoom:50%;" />

总偏差的计算如下

$$
|d_i - 6|+|d_j - 6|+|d_k - 6|+|d_l - 6|
$$

上图中是从5降到了1, 则进行翻转

### 更接近等边三角形

 $Delaunay$ 不保证三角形是接近正三角形的, 可能满足 $Delaunay$ , 但依旧存在长长的三角形

可以重复地中心化顶点来使三角形根接近等边三角形

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211040602379.png" alt="image-20251211040602379" style="zoom:50%;" />

原来位置是 $p$ , 目标位置是 $p'$, 为例保证网格描述的几何体的形状不变,那么在移动时的方向应当移除法线方向上的分量

==这里提到的法线是哪个面的法线????==

是周边三角形平面的法线的加权平均, 权重是对应三角形的面积

三角形面越大, 法线占比越大, 法线指向偏多的那个三角形

消除法线方向上的分量, 就是向中心移

### 策略

循环下列操作

- Splite 超过 $\frac{4}3 \cdot mean(edges)$ 长度的边
- Collapse 小于 $\frac{4}5 \cdot mean(edges)$ 长度的边
- Flip 边以接近最佳度数
- 沿切线方向将点进行居中优化

## 信息丢失

如果反复使用upsampling和downsampling, 会导致信号丢失

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211041535447.png" alt="image-20251211041535447" style="zoom:50%;" />

因为upsampling和downsampling的操作是凸包的, 总是先一个更圆滑的方向去逼近

## 保留锐利边

旧点A, 调整光滑程度, 依靠给锐利边上的临近点加大比重, 给非锐利边上的临近点减少比重

- 不连接锐利边, 则大幅度光滑
- 链接一个锐利边, 在锐利边的那个方向小幅度光滑
- 链接两条及以上锐利边, 更加保持光滑变化减少, 甚至保持原位置不变, A' = A

新点M

- 如果在锐利边上, 则直接是两个旧点的中点
- 否则依照原有的算法大幅度变光滑




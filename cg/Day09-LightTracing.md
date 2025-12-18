# 光线追踪

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215033557501.png" alt="image-20251215033557501" style="zoom:50%;" />

1. 从眼睛发出射线(Primary Rays)
2. 遇到第一个遮挡物, 则射线终止
3. 在遮挡物上发生折射和反射(Secondary Rays)
4. 对于每个碰到的点, 都向光源发送 Shadow Rays 以测试光线可见度
5. 当遇到非镜面表面（或达到最大期望的递归水平）, 停止递归, 进入5, 否则, 对于每个射线, 返回2

## 检查和光线的交集

### 三角形面

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215035215159.png" alt="image-20251215035215159" style="zoom:50%;" />

射线 $r$ 方程, 其中出发点 $\boldsymbol{o}$, 时间 $t \in [0,\infty ) $ 和发射的单位方向向量  $\vec{d}$ 


$$
\boldsymbol{r}(t) =  \boldsymbol{o} \; + \; t \cdot \vec{d}
$$


平面 $p$ 方程, 其中 $\boldsymbol{p}$ 表示平面上的任意一点,  $\boldsymbol{p}_i$ 表示确定平面的一点,  $\vec{n}$ 表示平面的法向量, 则平面为


$$
p:\; (\boldsymbol{p}-\boldsymbol{p}_i) \cdot \vec{n}  \;=\; 0
$$


射线 $r$ 和平面 $p$ 相交表示为, 存在一个点 $x$, 同时满足 $p$ 的定义和 $r$ 的定义


$$
\left\{\begin{align}
  \boldsymbol{o} \; + \; t \cdot \vec{d} &=  \boldsymbol{x} \\
  (\boldsymbol{x} - \boldsymbol{p}_i) \cdot \vec{n} \;&=\; 0 
\end{align}\right.
$$
运算可得
$$
\left\{\begin{align}
  t \;&=\; \frac{\boldsymbol{o} - \boldsymbol{p}_i }{\vec{d}\cdot\vec{n}} \\
 \boldsymbol{x} \;&=\; \boldsymbol{o} \; + \; t \cdot \vec{d} 
\end{align}\right.
$$
然后确认交点 $x$ 是否在三角形网格内

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215040122573.png" alt="image-20251215040122573" style="zoom:50%;" />

[查看判断一个点是否在三角形内](#Point In Triangle Test)

使用向量等值线, 换一种对平面的表达

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215040828774.png" alt="image-20251215040828774" style="zoom:50%;" />


$$
\boldsymbol{p}  = \boldsymbol{p_0} + u \vec{P_0 P_1} + v \vec{P_0P_2}
$$
再次连理方程组

$$
\left\{\begin{align}
 \boldsymbol{x} \;&=\; \boldsymbol{p_0} + u\vec{P_0P_1}+v\vec{P_0P_2} \\
 \boldsymbol{x} \;&=\; \boldsymbol{o} \; + \; t \cdot \vec{d} 
\end{align}\right.
$$
即


$$
\boldsymbol{p_0} + u\vec{P_0P_1}+v\vec{P_0P_2} \;=\; \boldsymbol{o} \; + \; t \cdot \vec{d}
$$




转化成矩阵运算


$$
(-\vec{d}, \vec{P_0P_1},\vec{P_0P_2}) \cdot \left(\begin{array}{ll}
    t\\
    u\\
    v
   \end{array} \right) \;=\; \boldsymbol{o} -\boldsymbol{p_0}
$$




这一次只要看 $t \in [0, \infty ), u + v \in(0,1)$ 即表示在三角形内了

### 球面

转换成球心和射线的距离公式

- 从球心作垂直光线的垂线, 垂足为V, 设$\vec{P_{ro}V} = k\cdot\vec{d}$ , $P_{ro}$ 为射线原点

  
  $$
  (k\cdot \vec{d} + \vec{OP_{ro}})\cdot \vec{OP_{so}} = 0
  $$

- 球心和射线的距离d

  - 小于r, 两个交点
  - 大于r, 无交点
  - 等于r, 一个交点

- 对于几个交点, 使用V和r, 判断交点是否在射线路径上, 哪个离射线原点更近

### 包围体

要测试光线是否打到一个网格的某个(或某些)三角形上

1. 遍历每个三角形
2. 计算三角形和光线射线是否相交

时间复杂度 $O(n)$ 

要提高速度, 就用一个包围盒包围目标

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215104103794.png" alt="image-20251215104103794" style="zoom:50%;" />

1. 测试光线是否和包围盒相交, 没有, 就放弃
2. 确实和包围盒相交, 遍历每个三角形测试

这样就过滤了一些光线

### 射线和包围盒相交

和三角形一样, 使用法向量


$$
\left\{\begin{align}
  \boldsymbol{o} \; + \; t \cdot \vec{d} &=  \boldsymbol{x} \\
  (\boldsymbol{x} - \boldsymbol{p}_i) \cdot \vec{n} \;&=\; 0 
\end{align}\right.
$$
然后对六个面进行分别测试

由于包围盒的特殊性, $\vec{n}$ 总是 $(0,0,1)$ , $(0,1,0)$ , $(1,0,0)$ , 比较简单
$$
\boldsymbol{x} \;=\; \left\{\begin{align}
  \boldsymbol{p_0} + u\vec{P_0P_1}+v\vec{P_0P_2} \\
  \boldsymbol{o} \; + \; t \cdot \vec{d} 
\end{align}\right.
$$
可得
$$
(-\vec{d}, \vec{P_0P_1},\vec{P_0P_2}) \cdot \left(\begin{array}{ll}
    t\\
    u\\
    v
   \end{array} \right) \;=\; \boldsymbol{o} -\boldsymbol{p_0}
$$
要判断点是否落在长方形内, 要求 $t \in [0, \infty ), u \in (0,1) , v \in(0,1)$ 

假设 $p: (x,y,z)$ 是包围盒内一点, 且 $x\in (x_{min},x_{max}), y \in (y_{min},y_{max}), z \in (z_{min},z_{max})$

在于六个面分别测试相交的过程中, 六个面的 t 的范围一定是存在交集的

如果不存在交集, 就是不与包围盒相交

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215110647801.png" alt="image-20251215110647801" style="zoom:50%;" />



我们还需要获取到射线与包围盒相交的 $t$ 的取值范围, 用于检查不同的包围盒之间的重叠问题

## 均匀空间划分

1. 划分网格

   <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215113300092.png" alt="image-20251215113300092" style="zoom: 33%;" />

2. 存储每个对象的单元

   <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215113523492.png" alt="image-20251215113523492" style="zoom:33%;" />

3. 射线步进遍历

   <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215113611290.png" alt="image-20251215113611290" style="zoom:33%;" />

存在问题

- 效率不够

- 网格太细导致大量的步进是浪费的

  <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215120720487.png" alt="image-20251215120720487" style="zoom:33%;" />

  一般在3D中使用 $27*count$ 个网格

- 网格太粗导致无法辨别交点的对象

  <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215120603088.png" alt="image-20251215120603088" style="zoom:33%;" />



在均匀图元下效果好

## KD-Tree

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215124753081.png" alt="image-20251215124753081" style="zoom: 33%;" />

- 找到边界

- 递归拆分单元格A->B->C->D

- 当分裂无法降低射线交集的预期成本时停止递归, 具体的**简单**实现方案有

  直到满足条件 (最大拆分次数 or 单元格最小对象数)

  定义最大深度, 经验上采用 $ 8 + 1.3 \; log N,\; N = count(objs)$

- 将对象存储在叶子节点上

递归进行查询

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215125225328.png" alt="image-20251215125225328" style="zoom:50%;" />

## BVH

> Bounding Volume Hierarchy

用于应对非均匀图元的场景

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215120936911.png" alt="image-20251215120936911" style="zoom:33%;" />

 如何判断一个划分是好的

- 越往树的深处走, 对象的排列就越紧密, 出现射线打空的概率就更小(如果出现打空可以更快地跳出)

划分时, 每个包围盒内的对象不重叠, 但是包围盒可以存在重叠

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215121905750.png" alt="image-20251215121905750" style="zoom:50%;" />

但是, 如果射线射中了重叠部分, 就需要测设两个包围盒; 重叠部分越大, 射中重叠部分概率越大, 效果越差

### 查询

查询击中点算法



### 构建

- 选择一个空间维度进行划分（例如 x，y，z）
  - 围绕空间中点分割物体
  - 在中间物体位置分割
- 当节点元素较少（例如 5）时停止


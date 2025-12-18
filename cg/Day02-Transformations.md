# Spatial Transformations

为什么是线性变换

- 容易实现
- 容易求解,也容易进行反向操作
- 线性变换的组合依旧是线性的

## 基础变换

### 投影

将2D空间的向量 经过三维的矩阵变换, 再投影到2维的空间上

$$
A \vec{v} \to \vec{m} := A\left(\begin{array}{ll}
v_x \\
v_y \\
1
\end{array} \right) \to \vec{v'} = \left(\begin{array}{ll}
m_x/m_z \\
m_y/m_z
\end{array} \right) 
$$


### 旋转

每个点之间的距离不变 

$$
\left(\begin{array}{ll}
\cos{\theta} & -\sin{\theta} & 0\\
\sin{\theta} & \cos{\theta} & 0 \\
0 & 0 & 1
\end{array} \right)
$$

旋转$R$的反操作是其转置 $R^T$ , 即 $R^{-1} = R^T$

三维空间下的旋转

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251209212017193.png" alt="image-20251209212017193" style="zoom:50%;" />

### 缩放


$$
\left(\begin{array}{ll}
a & 0 & 0\\
0 & b & 0 \\
0 & 0 & 1
\end{array} \right)
$$


当a, b异号, 则发生reflect对称

### 剪切

在某个方向上的距离不变

向 $\vec{e} = (e_1,e_2)$ 方向进行程度为$\vec{u} = (u_1,u_2)$的剪切

其变换矩阵应该为

$$
A_{u,e} = I + \vec{u}\cdot\vec{e}^T
$$


$$
A_{u,e} = \left(\begin{array}{ll}
1 & 0 & 0\\
0 & 1 & 0 \\
0 & 0 & 1
\end{array} \right)+\left(\begin{array}{ll}
u_1 e_1	& u_1 e_2 & 0\\
u_2 e_1 & u_2 e_2 & 0 \\
0 		& 0 	  & 0
\end{array} \right)=\left(\begin{array}{ll}
u_1 e_1 + 1	& u_1 e_2 		& 0\\
u_2 e_1 	& u_2 e_2 + 1   & 0 \\
0 			& 0 	  		& 1
\end{array} \right)
$$

### 平移

原理是三维的剪切操作, 投影到二维上

向 $\vec{u} = (u_1,u_2)$ 方向平移(三维空间在 $x-y$ 平行的平面上的投影)

$$
\left(\begin{array}{ll}
1 & 0 & u_1\\
0 & 1 & u_2 \\
0 & 0 & 1
\end{array} \right)
$$

### 转换复合

$$
T \times R_0\times S \times R_1 \times \vec{v}
$$

-  几何上 $R_1$ 的变换先起作用, 因为左边的矩阵作用在右边的矩阵上
- 运算上从左往右运算

1. $R_1$ 旋转
2. $S$ 缩放
3. $R_0$ 旋转
4. $T$ 平移

## 坐标变换管线概览

从几何目标到二维图像平面的一系列转化流程

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210163340038.png" alt="image-20251210163340038" style="zoom:50%;" />

### 空间定义

| 空间名称                  | 简要说明                                                     |
| ------------------------- | ------------------------------------------------------------ |
| **Object Space**          | 模型自身坐标系，定义几何体的原始形状和顶点位置。             |
| **World Space**           | 全局统一坐标系，所有物体置于其中，定义其在场景中的绝对位置和方向。 |
| **Camera Space**          | 以相机（观察者）为原点的坐标系，**视线通常朝向 -Z 轴方向**。 |
| **Canonical View Volume** | 标准化视景体，是一个规则立方体（如 $[-1,1]^3$），用于后续裁剪和统一处理。 |
| **Screen Space**          | 二维屏幕像素坐标系，原点通常在左上角，坐标以像素为单位。     |

### 变换

| 变换名称                      | 简要说明（作用与起点/终点空间）                              |
| ----------------------------- | ------------------------------------------------------------ |
| **Modeling Transformation**   | 将物体从 **Object Space** 变换到 **World Space**，包括缩放、旋转、平移。 |
| **View Transformation**       | 将整个场景从 **World Space** 变换到 **Camera Space**，使相机位于原点并对齐轴向。 |
| **Projection Transformation** | 将 **Camera Space** 中的3D物体投影到2D成像平面，并变换到 **Canonical View Volume**。 |
| **Viewport Transformation**   | 将 **Canonical View Volume** 内的坐标映射到 **Screen Space** 的像素坐标。 |

## 相机

### 相机变换

相机移动变换矩阵 $A$ 转换成物体进行变换矩阵为 $A^{-1}$ 的移动

$$
Camera(A) \to Object(A^{-1})
$$

相机镜头对准的旋转

已知相机的镜头方向单位向量是$\vec{w}$, 希望最终能指向$-z$轴

1. 求出向量 $\vec{w}$ 的正交单位向量 $\vec{u}, \vec{v}$

   1. 随便搞一个差不多一点的向量 $\vec{u_0}$
   2.  $\vec{u_0} $ 减去  $\vec{u_0}$ 在  $\vec{w}$ 方向上的投影向量, 即可获得与  $\vec{w}$ 向量正交的向量  $\vec{u_1}$
   3. 对向量 $\vec{u_1}$ 做归一化得到 $\vec{u}$
   4. 只要求出$\vec{u}$, 使用叉乘 $\vec{w} \times \vec{u}$, 就可以得到 $\vec{v}$

2. 即可获取摄像机的方向旋转到 $-z$ 轴矩阵 $R$
   
   $$
   R = \left(\begin{array}{ll}
   u_x & v_x & -w_x\\
   u_y & v_y & -w_y \\
   u_z & v_z & -w_z
   \end{array} \right)
   $$

3. 目标是旋转世界坐标轴, 即求矩阵 $R^{-1}$, 由于 $R$ 是正交的, 故 $R^{-1} = R^T$

   证明 $R^{-1} = R^T$ 只需要证明  $R^T R = E$ 即可

4. 即获取到坐标轴变换矩阵 $R_A$ 
   
   $$
   R_A = \left(\begin{array}{ll}
   u_x & u_y & u_z \\
   v_x & v_y & v_z \\
   -w_x & -w_y & -w_z
   \end{array} \right)
   $$

### 视锥体

习惯上, 我们让相机镜头对准-z轴方向, 以保证**视锥体**的投影在等价的屏幕上

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251209194004812.png" alt="image-20251209194004812" style="zoom:50%;" />

原点是相机镜头, 白板是相机屏幕, 灰色是物体在屏幕上的投影的还原

### 裁剪 Clipping

裁剪就是消除不在这个视锥体内的三角形的过程

如果三角形落在区域外, 就不绘制, 如果落在区域内就绘制, 避免在不需要的三角形上浪费时间

对于只有一部分落在区域内的三角形, 选择将其分为更小的子三角形

![image-20251209200137545](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251209200137545.png)

我们定义, 剔除比 `z-near` 近, 比 `z-far` 远的物体

这么做是为了 `z-buffer`, z 缓冲这一技术, 由于在计算机上使用浮点数存储三角形信息

如果 `z-near` 过小, 或 `z-far` 过大, 浮点数将会导致精度丢失, 最终导致光栅化时产生肉眼可见的误差

![image-20251209200640031](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251209200640031.png)

## 视锥体到单位正方体的映射

![image-20251209200825480](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251209200825480.png)

为什么要这么做?

便于裁切

- 以x轴为例, 本来是运算一个三角形是否在一个斜面之外, 现在只需要查看一个三角形的x轴坐标是否在`(-1,1) `之外

- 只有一部分在视锥体的三角形, 这种特殊情况, 划分为子三角形这一步骤也将简单一些

1. 将视锥体的灰色部分, 一个锥台, 变成一个长方体, 转 **透视投影** 为 **正交投影**

   <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251209213603492.png" alt="image-20251209213603492" style="zoom: 33%;" />

   定义变量

   - **n** : near
   - **f** : far

   我们希望结果满足

   - 近平面不变
   - 非近平面等比例缩小, 比例为 $\frac{n}z$ , 即 $x \to x\cdot \frac{n}z, y \to y\cdot \frac{n}z$
   - 最远景平面距离不变

   $$
   \left(\begin{array}{ll}
   x\\y\\z\\1
   \end{array} \right) \to \left(\begin{array}{ll}
   x\cdot \frac{n}z\\y\cdot\frac{n}z\\?\\1
   \end{array} \right)
   $$

   由于在结果中x和y的位置出现了z, 而线性运算不会达到这种效果, 因此, 这需要最后一步做特殊处理

   最后一步, 向量除以第四维的数值, 表示映射回到三维空间上
   
   $$
   M\times\left(\begin{array}{ll}
   x\\y\\z\\1
   \end{array} \right) = \left(\begin{array}{ll}
   x\cdot n \\y\cdot n\\? \cdot z \\z
   \end{array} \right) \to \left(\begin{array}{ll}
   x\cdot \frac{n}z\\y\cdot\frac{n}z\\?\\1
   \end{array} \right)
   $$

   变换矩阵如下
   
   $$
   M = \left(\begin{array}{ll}
   n &  &   &  \\
    & n &   &  \\
    &   & A & B \\
    &   & 1 & 0 
   \end{array} \right)
   $$
   
   代入得
   
   $$
   M\times\left(\begin{array}{ll}
   x\\y\\z\\1
   \end{array} \right) = \left(\begin{array}{ll}
   x\cdot n \\y\cdot n\\ A\cdot z + B \\z
   \end{array} \right) \to \left(\begin{array}{ll}
   x\cdot \frac{n}z\\y\cdot\frac{n}z\\A+\frac{B}z\\1
   \end{array} \right)
   $$
   
   依据条件最远景平面距离不变, 即对于 $F(z) = A+\frac{B}z$ , 有 $F(n)=n$ 且 $F(f)=f$

   则得出 $A= n+f, B=- n \cdot f$

   代入 $M$ 得
   
   $$
   M = \left(\begin{array}{ll}
   n &  &   &  \\
    & n &   &  \\
    &   & n+f & -n \cdot f \\
    &   & 1 & 0 
   \end{array} \right)
   $$

2. 由于Z指向负轴, 因此需要进行一个$z \to -z $ 的映射

   $$
   M \times  \left(\begin{array}{ll}
   x \\ y \\ z \\ 1
   \end{array} \right)
   $$

   

3. 定义正交容器上八个点有关的参数 

   - **l** : left
   - **r** : right
   - **b** : bottom
   - **t** : top
   - **n** : near
   - **f** : far

4. 平移到原点

   $$
   T = \left(\begin{array}{ll}
   1 &  &   & -\frac{r+l}{2}  \\
    & 1 &   & -\frac{t+b}{2}  \\
    &   & 1 & -\frac{n+f}{2}  \\
    &   &   & 1 
   \end{array} \right)
   $$

5. 使用scale进行归一( 其实是(-1,1), 长度2 )化, 由于是 -z 轴, 所以 n 比 f 大

   $$
   S = \left(\begin{array}{ll}
   \frac2{r-l} &  &   &  \\
    & \frac2{t-b} &   &  \\
    &   & \frac2{n-f} &  \\
    &   &   & 1 
   \end{array} \right)
   $$

6. 对空间中的任意向量 $\vec{v}$ 复合两个操作

   $$
   \vec{v'} = S \times T \times M \times \left(\begin{array}{ll}
   v_x \\
   v_y \\
   v_z \\
   1
   \end{array} \right)
   $$

   对于 $S \times T$, 复合矩阵即

   $$
   S \times T = \left(\begin{array}{ll}
   
   \frac2{r-l} & 0 & 0 & -\frac{r+l}{r-l}  \\
   0 & \frac2{t-b} & 0 & -\frac{t+b}{t-b}  \\
   0 & 0 & \frac2{n-f} & -\frac{n+f}{n-f}  \\
   0 & 0 & 0 & 1 
   
   \end{array} \right)
   $$

   定义$M_{ortho}=S \times T$ , $M_{persp\to -ortho} = M$, $M_{persp}=R_x \times M_{ortho}\times M_{persp\to -ortho} \times R_x$

   其中 $R_x$ 表示关于 $z$ 轴对称需要反射

   $$
   R_x = \left(\begin{array}{ll}
   1&&& \\ 
   &1&& \\ 
   &&-1& \\ 
   &&&1
   \end{array} \right)
   $$
   

   对于 $M_{persp}$, 复合矩阵即

   $$
   M_{persp}=M_{ortho}\times M_{persp\to ortho} = \left(\begin{array}{ll}
   \frac{2n}{r-l} & 0 & \frac{r+l}{r-l} & 0  \\
   0 & \frac{2n}{t-b} & \frac{t+b}{t-b} & 0 \\
   0 & 0 & -\frac{f+n}{f-n} & \frac{-2fn}{f-n}  \\
   0 & 0 & -1 & 0 
   \end{array} \right)
   $$

7. 对于现代计算机的图形渲染, 因此从空间到数组的索引, 也存在一个映射

   <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251209223532674.png" alt="image-20251209223532674" style="zoom:50%;" />

   - 前面正方体的边长是2
   - **w** : width
   - **h** : height

   $$
   V = \left(\begin{array}{ll}
   \frac{w}{2} & 0 & 0 & \frac{w}{2}  \\
   0 & \frac{h}{2} & 0 & \frac{h}{2} \\
   0 & 0 & 1 & 0  \\
   0 & 0 & 0 & 1 
   \end{array} \right)
   $$


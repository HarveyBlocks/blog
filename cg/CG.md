
# Vector Space

向量乘积的笛卡尔坐标系求法, 使用Determinant定义

$$
\vec{u} \times \vec{v} := det\left(\begin{array}{ll}
\vec{e_1} & \vec{e_2} & \vec{e_3}\\
u_1 & u_2 & u_3\\
v_1 & v_2 & v_3
\end{array} \right) = \left(\begin{array}{ll}
u_2 v_3 - u_3 v_2 \\
u_1 v_3 - u_3 v_1 \\
u_1 v_2 - u_2 v_1
\end{array} \right)
$$

设置一个单位向量 $\vec{n}$,  现将其作为一个平面 $\alpha$ 的法向量, $\vec{u}$ 是平面 $\alpha$ 上的一个向量

![image-20251208220509763](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251208220509763.png)

求 $\vec{n} \times \vec{u}$ 相当于将 $\vec{u}$ 这个向量在平面 $\alpha$ 上旋转了 $90^{\circ}$

同理可得, 求 $\vec{n} \times (\vec{n} \times \vec{u})$ 相当于将 $\vec{n} \times \vec{u}$ 这个向量在平面 $\alpha$ 上旋转了 $90^{\circ} $, 即将 $\vec{u}$ 这个向量在平面 $\alpha$ 上旋转了 $180^{\circ}$

$$
det\left(\begin{array}{ll}
\vec{u} \\ \vec{v} \\ \vec{w}
\end{array} \right) = 
(\vec{u}\times\vec{v})\cdot\vec{w}=
(\vec{v}\times\vec{w})\cdot\vec{u}=
(\vec{w}\times\vec{u})\cdot\vec{v}
$$

例如$\vec{u}\times\vec{v}$就是底面积, 同时也是u-v平面的法向量, $(\vec{u} \times \vec{v}) \cdot \vec{w}$ 就是求这个菱形的体积

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251208223852460.png" alt="image-20251208223852460" style="zoom:50%;" />

## Jacobi identity

雅可比式在三角形的法向量运算中的使用

![image-20251208222609328](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251208222609328.png)

$$
\vec{u}\times(\vec{v}\times\vec{w}) +
\vec{v}\times(\vec{w}\times\vec{u}) +
\vec{w}\times(\vec{u}\times\vec{v}) = 0
$$

## Lagrange's identity

$$
\vec{u}\times(\vec{v}\times\vec{w}) = \vec{v} (\vec{u}\cdot\vec{w})-\vec{w} (\vec{u} \cdot \vec{v})
$$

## Gradients on Matrix

在矩阵上求梯度

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251208230010947.png" alt="image-20251208230010947" style="zoom: 67%;" />

例证第一个

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251208230045747.png" alt="image-20251208230045747" style="zoom:50%;" />

## Gradients on Function

对于 $F(f)$ 回归定义

$$
\langle\langle \nabla F,u \rangle\rangle := D_uF
$$

$$
D_uF := \lim_{\epsilon \to 0} \frac{F(f+\epsilon u)-F(f)}{\epsilon}
$$

对于 $F(f) = \langle\langle f,g \rangle\rangle$ 函数内积作为 $F$ 的定义, 其梯度类比可得 $\nabla F = g$

对于 $F(f) = |f|^2$ 函数的范数作为 $F$ 的定义, 其梯度类比可得 $\nabla F = 2f_0$, 证明如下

$$
\langle\langle \nabla F(f_0),u \rangle\rangle= \lim_{\epsilon \to 0} \frac {F(f_0+\epsilon u)-F(f_0)} {\epsilon}
$$

即

$$
\begin{aligned}
F(f_0+\epsilon u) \;&=\; |f_0+\epsilon u|^2 \\
\;&=\; |f_0|^2 + \epsilon^2 |u|^2 + 2 \epsilon\langle\langle f_0,u\rangle\rangle\\
\;&=\; F(f_0)+\epsilon^2F(u)
\end{aligned}
$$


带入得

$$
\langle\langle\nabla F(f_0), u\rangle\rangle = 2\langle\langle f_0,u\rangle\rangle
$$


# Sampling

采样点和像素点

- 像素点是最终展示在屏幕上的
- 采样点不一定和像素点等同, 比如超采样, 每一个像素点可能对应多个采样点

## Why Triangle?

- 三角形可以组成任意图形

  <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251208180956394.png" alt="image-20251208180956394" style="zoom: 50%;" />

- 三角形由三个点组成, 三个点总是在一个平面=>便于计算这个单元(三角形单元就构成一个平面)的法线

- 便于利用三个点进行**线性插值**(权重使用重心的概念)

## Sampling

### superposition frequency

复杂信号可以表示为不同频率的简单信号的和

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251208182920178.png" alt="image-20251208182920178" style="zoom:50%;" />

将这个概念扩展到二维图像上

如果只采样低频的数据, 就会获取比较糊的图像

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251208183345552.png" alt="image-20251208183345552" style="zoom:33%;" />

如果只采样中间频率的数据, 就会有一个模糊的轮廓

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251208183448924.png" alt="image-20251208183448924" style="zoom:33%;" />

如果只采样高频率的数据, 就会有一个清晰的轮廓

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251208183544417.png" alt="image-20251208183544417" style="zoom:33%;" />

上述三个频率的采样加起来, 就是全段频率的采样, 同理, 图片加起来也会比较清晰

![image-20251208183641212](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251208183641212.png)

采样低频率数据+采样较低频率数据+采样较高频率数据+采样超高频率数据

### 高频信号失真

Origin频率高于采样频率太多, 将导致采样结果反而接近低频下的Origin

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251208183052084.png" alt="image-20251208183052084" style="zoom:50%;" />

在相同频率的采样率下, 频率的信号高到一定程度, 就会高度失真

在$f_5(x)$中, 明明源信号是高频的, 但采样得到的信号看起来好像是低频的(蓝色虚线)

 <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251208183741433.png" alt="image-20251208183741433" style="zoom:50%;" />

随着X增大, 源数据频率增高, 发现出现了低频的点, 这就是因为采样频率不足导致的

依据**香农定理**, 需要源数据的最高频率的两倍来采样, 可以完全还原源数据

## 对一个矢量三角形采样

### Cover

判断一个像素是否被三角形Cover, 看这个Pixel的中心点

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251208181431002.png" alt="image-20251208181431002" style="zoom:50%;" />

- 1 not Covered Pixel
- 2 not Covered Pixel
- 3 Covered Pixel
- 4 Covered Pixel

### Point In Triangle Test

看一个是否在一个三角形中



<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251216155516063.png" alt="image-20251216155516063" style="zoom:50%;" />

1. 对于三个三角形的三边, 按循环顺序构建三条向量

   - $\vec{p_0},\;\vec{p_1},\;\vec{p_2}$
   - 对于向量 $\vec{p_i}$ ,  $i$ 表示向量起点的下标
   - 设 $j$ 为 $\vec{p_i}$ 的终点的下标

   $$
   \vec{p_i} = (x_j-x_i,\; y_j-y_i)
   $$

   

2. 计算与 $\vec{p_i}$ 垂直的向量 $\vec{n_i}$, 一般垂直有两个方向, 下面的解析式规定了方向
   $$
   \vec{n_i} = (y_j-y_i,\; -x_j+x_i)
   $$
   
3. 遍历所有采样点

4. 当前采样点 $P = (x,y)$, 计算 $L_i$
   $$
   \begin{aligned}
   L_i \;&=\; \vec{P_iP} \cdot \vec{n_i} \\
   \;&=\; (x-x_i,\; y-y_i)\cdot(y_j-y_i,\;-x_j+x_i) \\
\;&=\; (x-x_i)\cdot(y_j-y_i)-(y-y_i)\cdot(x_j-x_i) 
   \end{aligned}
   $$
   
5. 三个 $L$ 的值同号, 则表示在三角形内



<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251216155639569.png" alt="image-20251216155639569" style="zoom:50%;" />




### Aliasing

走样

采样会导致走样（伪影），因为采样率低于原始信号频率

可能产生锯齿, 车轮效应

### Anti-Aliasing

反走样

预滤波在采样前去除原始信号中的高频

(如果先采样再过滤高频, 会出现模糊的锯齿, 锯齿依旧存在)

超采样

## 卷积过滤

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251216162153067.png" alt="image-20251216162153067" style="zoom:33%;" />



### 定义 

卷积函数定义 
$$
(f * g)(t) = f(t) * g(t) = \int_{-\infty}^{+\infty} f(\tau)g(t - \tau)d\tau
$$

对两个函数重叠长度的乘积的积分, $g(n)$ 为滤波器(卷积核)

离散形式
$$
f(n) * g(n) = \sum_{m=-\infty}^{+\infty} f(n-m) g(m)
$$

例子： $ g(n) = 1/3, -1 \leq n \leq 1 $ 

卷积后 $f(n)$ 变为 
$$
(f * g)(n) = f(n-1)g(-1) + f(n-0)g(0) + f(n+1)g(1)
$$

如果将参加卷积的一个函数看作区间的指示函数（只在一个区间有定义，其余地方为0），卷积还可以被看作是“滑动平均”（定义域上滑动，重叠区域平均）。



### 定理

时域卷积定理： 
$$
F[f(t) * g(t)] = F_f(\omega) \cdot F_g(\omega)
$$

两信号在时域的卷积积分对应于在频域中该两信号的傅立叶变换的乘积。

频域卷积定理： 
$$
F[f(t) \cdot g(t)] = \frac{1}{2\pi} F_f(\omega) * F_g(\omega)
$$

两信号在时域的乘积对应于这两个信号傅立叶变换的卷积除以 $ 2\pi $。



### 时域定理证明

首先，卷积定义为 

$$
f_1(t) * f_2(t) = \int_{-\infty}^{+\infty} f_1(\tau) f_2(t - \tau) d\tau
$$

然后，代入傅立叶变换公式 

$$
F[f(t)] = F(\omega) = \int_{-\infty}^{+\infty} f(t) e^{-j\omega t} dt
$$

- $j$ 是虚数单位，$j^2 = -1$
- $\omega$ 是角频率 $rad\cdot s^{-1}$
- $t$ 是时间

由此可得
$$
\begin{align*}
F[f_1(t) * f_2(t)] &= \int_{-\infty}^{+\infty} \left[ \int_{-\infty}^{+\infty} f_1(\tau) f_2(t - \tau) d\tau \right] e^{-j\omega t} dt \\
&= \int_{-\infty}^{+\infty} f_1(\tau) \left[ \int_{-\infty}^{+\infty} f_2(t - \tau) e^{-j\omega t} dt \right] d\tau \\
&= \int_{-\infty}^{+\infty} f_1(\tau) \left[ \int_{-\infty}^{+\infty} f_2(t - \tau) e^{-j\omega (t - \tau)} dt \right] e^{-j\omega \tau} d\tau \\
&= \int_{-\infty}^{+\infty} f_1(\tau) F_2(\omega) e^{-j\omega \tau} d\tau \\
&= F_2(\omega) \int_{-\infty}^{+\infty} f_1(\tau) e^{-j\omega \tau} d\tau \\
&= F_1(\omega) \cdot F_2(\omega)
\end{align*}
$$

### 频域定理证明

$ \mathcal{F}^{-1} $ 表示傅立叶逆变换
$$
\mathcal{F}^{-1}[F(\omega)] = f(t) = \frac{1}{2\pi} \int_{-\infty}^{\infty} F(\omega) e^{j\omega t} d\omega
$$


设 $ F_1(\omega) = F[f_1(t)] $，$ F_2(\omega) = F[f_2(t)] $，则 

$$
\begin{align*}
\mathcal{F}^{-1} [F_1(\omega) * F_2(\omega)] &= \mathcal{F}^{-1} \left[ \int_{-\infty}^{+\infty} F_1(\mu) F_2(\omega - \mu) d\mu \right] \\
&= \frac{1}{2\pi} \int_{-\infty}^{+\infty} \left[ \int_{-\infty}^{+\infty} F_1(\mu) F_2(\omega - \mu) d\mu \right] e^{j\omega t} d\omega \\
&= \frac{1}{2\pi} \int_{-\infty}^{+\infty} F_1(\mu) \left[ \int_{-\infty}^{+\infty}  F_2(\omega - \mu) e^{j(\omega - \mu)t} d(\omega-\mu) \right] e^{j\mu t}  d\mu \\
&= f_2(t) \int_{-\infty}^{+\infty} F_1(\mu) e^{j\mu t} d\mu \\
&= 2\pi f_1(t) f_2(t)
\end{align*}
$$



因此有 
$$
f_1(t) f_2(t) = \frac{1}{2\pi} IF [F_1(\omega) * F_2(\omega)] = IF \left[ \frac{1}{2\pi} F_1(\omega) * F_2(\omega) \right]
$$



## early out

使用**快速退出**, 避免对一些明显不被覆盖的点进行无用的检查

从左到右, 遇到第一个不在三角形内的点, 这一行就可以break了

同理, 也可以依据$P_0 ,P_1, P_2$ 三个点的横坐标, 找到最左边的一个点, 来减少行的开始时对Point的无用检查

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251208184025324.png" alt="image-20251208184025324" style="zoom:33%;" />

## Depth-buffer/z-buffer

深度缓冲

用于解决遮挡关系

在每个采样点, 除了颜色之外, 还要追踪到目前为止所见的最接近的三角形

1. 遍历所有三角形, 对于某个三角形:
2. 遍历所有像素, 对于某个像素点: 
3. 判断是否在三角形内, 需要被渲染, 如果不是, 进入6, 否则进入4
4. 判断是否在之前被其他三角形渲染, 如果不是, 直接渲染, 进入6, 否则进入5
5. 判断之前的三角形的渲染的深度, 如果旧三角形的更近, 则什么都不做, 否则, 覆盖颜色, 更新深度
6. 进入2

```cpp
draw_sample(x,y,depth,color){
	if(pass_depth_test(depth,z_buffer[x][y])){
		// triangle now is closer
        z_buffer[x][y] = depth;
        color_buffer[x][y] = color
	}else{
		// do nothing
	}
}
```

z-buffer能解决画家算法无法解决的循环遮挡的问题

## Depth-buffer+Supersampling

1. 准备一个$W\cdot H\cdot S \cdot S$ 大小的color数组, 用于存储超采样过程中的像素
2. 准备一个$W\cdot H\cdot S \cdot S$ 大小的float数组, 用于存储z-buffer, 元素初始化为无限远
3. 对于$W\cdot H\cdot S \cdot S$ 的数组进行采样
4. $W\cdot H\cdot S \cdot S$ 的数组取平均映射到$W\cdot H$ 的结果数组上

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

# 纹理映射

Texture mapping

1. 重心插值从屏幕样本 $(x,y)$ 计算纹理 $(u,v)$ 坐标

2. 从 $(u,v)$ 坐标上的值, 插值到屏幕的 $(x,y)$ 坐标上

   通过获取屏幕相邻样本的差异, 来近似U, V坐标方向上的变化值

3. 利用插值来估算mip map的等级D

4. 将归一化的纹理图标UV 映射到屏幕的像素位置

## 重心插值

$$
\hat{f}=f_i+\frac{x-x_i}{x_{i+1}-x_i}(f_{i+1}-f_i)
$$

![image-20251209234859076](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251209234859076.png)

### 平面插值

在平面上进行线性插值

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251209234934712.png" alt="image-20251209234934712" style="zoom:50%;" />

使用解析式法, 如下

$$
\hat{f}(x,y)  = ax+by+c
$$

![image-20251209235132800](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251209235132800.png)

如果转化思路, 变为比例的概念, 重心<->面积比例<->高

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210013254123.png" alt="image-20251210013254123" style="zoom:33%;" />

### 投影问题

由于三角形从三维空间投影到了二维平面上, 在二维平面上计算得出的插值仿射函数不等同于原来的三角形的插值仿射函数

造成的问题:

![image-20251210012134774](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210012134774.png)

解决方法:

![image-20251210012836310](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210012836310.png)



## Texture

纹理映射

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210014347227.png" alt="image-20251210014347227" style="zoom:50%;" />

为什么要重复性地(周期性地)贴同一种这种红绿渐变的材质?

将来会贴砖块的贴图, 在进行贴图阶段之前, 使用这种插值的纹理, 进行初步的检查

纹理->3D单元->2D投影

- 放大的情形, 一个纹理的像素会占据好几个结果的像

- 缩小的情形, 一个结果像素中存在好几个纹理的单元

## 双线性插值

放大的情形, 一个纹理的像素会占据好几个结果的像素

问题: 会看见大的色块

解决: 使用双线性插值进行颜色的混合

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210015245032.png" alt="image-20251210015245032" style="zoom:33%;" />

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210015220216.png" alt="image-20251210015220216" style="zoom:50%;" />

### 摩尔纹

纹理插值会模糊网格的锐利边缘，因为它的设计是平滑过渡的，而网格需要像素级精确。

网格它是高频周期信号，在3D透视中容易被屏幕像素欠采样，导致信号混叠成低频条纹, 即摩尔纹。

对网格类纹理使用**最近邻（Nearest Neighbor）滤波**，并配合正确的**Mipmap**与**各向异性过滤**来抗走样。

## mipmap

### 预过滤

缩小的情形, 一个结果像素中存在好几个纹理的单元

不考虑运算的效率损耗, 可是使用类似超采样的方法, 对这个结果像素对应的多个目标的纹理单元取平均值

但是包含的目标单元个数可能非常之多

解决方案: 预存储原图片的低分辨率版本(降低分辨率使用Aliasing混叠法), 这个过程称为 **预过滤**

### mipmap

MipMap思路大概如此, 在每个可能的尺度上存储一个预过滤的图像

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210020221613.png" alt="image-20251210020221613" style="zoom:50%;" />

### 选择等级

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210021047580.png" alt="image-20251210021047580" style="zoom:50%;" />

左图是屏幕空间, 右图是纹理空间

要展示蓝色区域, 应当使用更详细的mipmap(由于两个区域大小差不多, 可以直接拿最详细的上)

要展示红色区域, 从纹理空间到屏幕空间, 大小缩小了不少, 因此使用 $level$ 更低的mipmap

![image-20251210021607292](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210021607292.png)

上图中, 屏幕空间的 $(u,v)_{0,0}$ 这一块空间, 对应了多大的mipmap纹理空间?

$$
level = Log_2 \sqrt{max(L_x^2,L_y^2)}
$$

$$
L_x^2 = (\frac{du}{dx})^2+(\frac{dv}{dx})^2, L_y^2=(\frac{du}{dy})^2+(\frac{dv}{dy})^2
$$

屏幕空间的 $(u,v)_{0,0}$ 和相邻区域在坐标系 $x-y$ 下的差就是  $dx=1, dy=1$

$$
\frac{du}{dx} = u_{10}-u_{00} ,
\frac{dv}{dx} = v_{10}-v_{00} 
$$

$$
\frac{du}{dy} = u_{01}-u_{00} , 
\frac{dv}{dy} = v_{01}-v_{00} 
$$

### 存储方法

原本一个RGB位图, 存储是$W\cdot H\cdot 3$, 现在, 将RGB摊开了存储, 存储成本并不会增加多少

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210020746549.png" alt="image-20251210020746549" style="zoom:50%;" />

### 三次线性插值

如果两个相邻的屏幕空间如果对应的level A和level B, A 和 B 都是整形, 两者之间存在跳跃, 看起来就会不自然

三次线性插值

对于空间中的任意点 $(u,v,w)$, 其相邻的八个取值点 

$f_{000},f_{001},f_{010},f_{011},f_{100},f_{101},f_{110},f_{111}$



<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210022917654.png" alt="image-20251210022917654" style="zoom:50%;" />

$f$ 相邻取值点之间两两配对, 插值出点 $g$, $g$ 再两两配对, 插值出 $h$, 最终 两个 $h$ 配对, 按比例取得 $(u,v,w)$的插值

![image-20251210022904038](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210022904038.png)

对非整型的 $level$ : $D \in R$ , 

1. 取两个相邻的mipmap $\lfloor D \rfloor$ 和 $\lfloor D \rfloor-1$

2. 取权重 $w = D - \lfloor D \rfloor$

3. 在同级mipmap中, 使用双线性插值

4. 在两个mipmap的插值结果之间再次进行插值

### 掠射角-各向异性过滤

对于这种屏幕空间场景, 某一个方向比另一个方向拉长的比例相差过大

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210024346282.png" alt="image-20251210024346282" style="zoom: 67%;" />

如果采用的取法依旧是正方形, 就不适合了

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210024411454.png" alt="image-20251210024411454" style="zoom:50%;" />

由于纵向拉长, 导致mipmap level 增大, 但是横向却不应该是这么大的 level 最终导致横向上糊成一片

解决方法是别取正方形的, 取多个低level的mipmap组合而成....







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



# 网格表示

正方形网格

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210194011972.png" alt="image-20251210194011972" style="zoom:50%;" />

为何计算机上选择正方形作为pixel单元的形状

- 总是有四个单元相邻
- 容易使用索引来对应
- 容易进行平均这种附加操作
- 易于存储

## 表面Surface

- 仅仅是表层, 是边界, 是空心的

- 是流形(Manifold)的, 流形的每一个部分放大后应该趋于平面

  存在一些图形是非流形的

  <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210194631064.png" alt="image-20251210194631064" style="zoom:50%;" />

  中间部分无法趋于平面, 下面是其他的例子

  <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210194806637.png" alt="image-20251210194806637" style="zoom:50%;" />

- 流形连通性, 是"扇面" ,而不是鳍

  - 每条边只包含在两个多边形中
  - 汇聚点四周应该以多个扇面的形式存在. 汇聚点周边的图形应该在同一个多边形环/扇里

  <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210195335057.png" alt="image-20251210195335057" style="zoom:50%;" />

流形保证目标几何体是简单的, 不需要考虑太多的特殊情况

边界Boundary

- 是表面结束的地方
- 每个边界都是闭环的

## 半边三角形网格

polygon mesh的一种

基于链表实现, 便于写操作

半边三角形只能在流形的前提下构建

```cpp
struct Halfedge {
    /**
     * twin->twin == this
     * twin != this
     */
    Halfedge* twin;
    /**
     * this 必是某个 halfedge 的 next
     */
    Halfedge* next;
    Vertex* start;
    Edge* edge;
    Face* face;
};
struct Edge {Halfedge* halfedge;};
struct Face {Halfedge* halfedge;};
struct Vertex {
    Vector3 point;
    Halfedge* halfedge;
};
```

但Halfedge构建时, `twin` 和 `next` 符合要求, 则满足改网络描述的是一个流形

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210202751136.png" alt="image-20251210202751136" style="zoom:50%;" />

遍历面的所有顶点

```cpp
Halfedge* h = f->halfedge;
do{
    // h->start...
    h = h->next;
}while(h != f->halfedge);
```

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210203511738.png" alt="image-20251210203511738" style="zoom:50%;" />

获取 `Halfedge` 的 `end ` `Vertex`

```cpp
Vertex* end = h->twin->start;
```

访问`Vertex`周边所有的`halfedge`

```cpp
Halfedge* h = v->halfedge;
do{
    // Vertex* end = h->twin->start;...
    // h->edge...
    // h->face...
    h = h->twin ->next;
}while(h != v->halfedge);
```

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251210203451586.png" alt="image-20251210203451586" style="zoom: 50%;" />

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

# 辐射度学

> 辐射度, *Radiance*

几何光学撇开光的波动本性, 仅考虑光的 **直线传播**, **独立传播**, **反射/折射**

## 辐射能量

$Radiant$ $Energy$ 光子击中物体表面的次数

单个光子的能量 $Q$

$$
Q = h \frac{c}{\lambda}
$$

其中, 普朗克常量 $h \approx 6.626 \times 10^{-34} J \cdot s$ , 光速 $c \approx 3.00 \times 10^{8} m/s$ , 波长 $\lambda$ 是可变参数, 关系到颜色

## 辐射通量

$Radiant$ $Flux$ 每单位时间, 光子击中表面的次数, 单位 瓦特

$$
\Phi = \lim_{\Delta \to 0} \frac{\Delta Q}{\Delta t} = \frac{dQ}{dt}
$$

对于辐射能量 $Q$

$$
Q = \int_{t_0}^{t_1}{\Phi(t)}dt
$$

## 辐射能量密度

$Radiant$ $Density$ 每单位面积, 光子击中表面的次数

## 辐照度

$Radiant$ $Flux$ $Density$ / $Irradiance$ 每单位面积, 单位时间, 光子击中表面的次数

$$
E(p) = \lim_{\Delta \to 0} \frac{\Delta \Phi(p)}{\Delta A} = \frac{d\Phi(p)}{dA}
$$

其中所照表面面积为 $A$, 应当是直面光线的面积

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211143406134.png" alt="image-20251211143406134" style="zoom:50%;" />

$$
cos(\theta) = \vec{n} \cdot \vec{l}
$$

其中 $\vec{n}$ 是表面的单位法向量, $\vec{l}$ 是法向量起点指向光源的单位方向向量

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211143803069.png" alt="image-20251211143803069" style="zoom:50%;" />

又要考虑到, 如果 $cos(\theta) < 0$, 则不渲染

```cpp
double surfaceColor(Vector3 n, vector3 l){
    return max(0., dot(n,l));
}
```

## 光强

$Intensity$, **光源**发出的光的能量在各方向上的密度(**特定方向**, **单位长度**)

和 $Irradiance$ 的区别在于 $Irradiance$ 描述的是**物体接受**的光

考虑点光源模型

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211154549109.png" alt="image-20251211154549109" style="zoom:33%;" />

光强和辐射通量之间的关系

$$
\Phi = \int_{S^2} I d \omega = 4 \pi I
$$

得出光强

$$
I = \frac{\Phi}{4\pi}
$$

在某一**特定方向**(角度 $\omega$) 的**单位长度**上的光强

$$
I(\omega) = \frac{d\Phi}{d\omega}
$$

又有 $E = \frac{d\Phi}{dA}$, 得

$$
E = \frac{I(\omega)}{r^2}
$$

## 立体角

### 定义

这里光强的定义使用到了立体角 $\Omega $ (弧度)的概念

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211160427217.png" alt="image-20251211160427217" style="zoom: 33%;" />

在平面上的角(弧度) $\theta := \frac{l}{r}$, 立体角的定义为:

$$
\Omega := \frac{A}{r^2}
$$

其中 $A$ 是球的**部分表面积**

由定义, 球的立体弧度为 $4\pi$

下图是立体角的一个应用, "两个天体到地球上的投影有多大", 这个问题考虑投影而不考虑天体的远近

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211160726609.png" alt="image-20251211160726609" style="zoom:50%;" />

"太阳和月亮在地球上看起来差不多大", 这句话转换成"太阳和月亮的光在地球表面上的人的眼睛上的投影差不多大", 即"两者对于地球的立体弧度差不多大"

### 和平面角的关系

考虑立体角 $\Omega$ 和 $x-y$ 平面上的角 $\phi$, 与 $z$ 轴的夹角 $\theta$ 之间存在的关系

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211161830550.png" alt="image-20251211161830550" style="zoom:33%;" />

首先从微风的角度上考虑, $dA = (r\cdot d\theta)(rsin(\theta)\cdot d\phi)$, 与 $\omega$ 的定义联立

$$
dA  = \left\{\begin{array}{ll}
r^2 sin(\theta)\cdot d\theta d\phi\\
r^2d\omega
\end{array} \right.
$$

可得

$$
d\omega = sin(\theta) d\theta d\phi
$$

积分得

$$
\Omega = \int_{S^2} d\omega = \int_0^{\theta_0}\int_0^{\phi_0} sin(\theta) d\theta d\phi
= (1-cos(\theta))\phi
$$

==有时, $\omega$ 也会被用作方向向量, 存在符号滥用的现象== 

## 光强和距离的关系

到点光源的距离和**辐照度**之间的关系

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211154958645.png" alt="image-20251211154958645" style="zoom: 33%;" />

$$
E = \frac{\Phi}{4\pi r^2 } \to \Phi = 4\pi r^2 E
$$

得出关系是**平方反比**的

$$
\frac{E_1}{E_2} = (\frac{r_2}{r_1})^2
$$

## 辐射率

$Radiance$

表示辐射度在立体角上的密度, 单位时间, 单位面积, 单位立体角上的光的能量

$$
L(p,\omega) := \lim_{\Delta\to0}\frac{\Delta E_{\omega}(p)}{\Delta_\omega} = \frac{dE_w(p)}{d\omega}
$$

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251211164054615.png" alt="image-20251211164054615" style="zoom:50%;" />

- $\omega$ : 方向向量
- $\Delta_\omega$ : 在$\omega$ 方向上的变化量
- $d\omega$ : 在$\omega$ 方向上的立体角的微元

但被照射到的平面的法向量和光线的照射存在一定夹角$\theta$时, 要除$cos\theta$ 进行调整
$$
L(p,w) = \frac{dE(p)}{d\omega \cdot cos\theta} = \frac{d^2\Phi(p)}{dA d\omega \cdot cost\theta}
$$


<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251212084048965.png" alt="image-20251212084048965" style="zoom:50%;" />

但需要求一个表面 $H$ 上所有的光强时

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251212085935070.png" alt="image-20251212085935070" style="zoom: 33%;" />


$$
E(p,\omega) = \int_{H^2} L_i(p,\omega)  \cdot cos\theta \cdot d\omega
$$



对于上图的半球
$$
E(p) = \int_{H^2} L d\omega = L \int_{H^2}d\omega = L \int_0^{2\pi} \int_0^{\frac{\pi}{2}} cos\theta \cdot sin\theta \cdot d\theta d\phi = L\pi
$$

对于辐射率的结果, 我们往往将其存储起来作为贴图

##  入射光线和出射光线

入射光线 $L_i$ $\ne$ 出射光线 $L_o$

强度不同, 比如绿色的物体只反射绿光, 吸收其他所有光

方向不同

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251212085520326.png" alt="image-20251212085520326" style="zoom:33%;" />


$$
L_i(p_1,\omega_1) \ne L_o(p_1,\omega_1)
$$

## 亮度

$Luminance$

这个量忽略颜色, 只表达亮度, 是将波长 $\lambda$ 所被人肉眼接收能力 $V(\lambda)$ 的积分


$$
Y(p,\omega) = \int_0^{\infty} L(p,\omega,\lambda) V(\lambda) d\lambda
$$



下图是人肉眼接收能力 $V(\lambda)$ 的图标, 两个不同的曲线是在亮环境和暗环境中, 肉眼接收的亮度有所不同

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251212092628653.png" alt="image-20251212092628653" style="zoom:50%;" />

# 渲染方程

$Rending$ $Equation$

物体亮度=作为光源发出的亮度+反射光发出的亮度

$$
L_0 = L_e+L_r
$$

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251214012650996.png" alt="image-20251214012650996" style="zoom:50%;" />

$$
L_0(\boldsymbol{p},\omega) \; = \; L_e(\boldsymbol{p},\omega_{0}) \;
+ \; \int_{H^2}f_r(\boldsymbol{p},\omega_i \to \omega_0)\; L_i(\boldsymbol{p},\omega_i) 
\; \cos(\theta) \; d \omega_i
$$

- $L_0$ 感知到的物体亮度

- $L_e$ 物体作为光源发出的亮度

- $L_i$ 入射光光强, 需要递归地获取

- $\boldsymbol{p}$ 点的位置

- $\omega$  眼睛的方向

- $\omega_i$ 入射角

- $\omega_0$ 出射角, 也就是眼睛的方向

- $f_r$ 散射函数, 有多上光从 $\omega_i \to \omega_0$ 反射出去

- $\theta$ 入射方向和表面法线之间的角度

- $H$ 能接收到其他物体发来的光的平面

- $\cos\theta$ 入射方向 $\omega_i$ 与法线 $\vec{n}$ 的夹角余弦, 

  光以倾角 $\theta$ 打到表面上时, 单位面积上实际接收到的光通量减少为原来的 $\cos\theta$ 倍

对于 $L_i$ , 使用反向光路最终, 从相机出发, 不断反射, 直到达到光源

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251214221955209.png" alt="image-20251214221955209" style="zoom:50%;" />

## 反射

反射率 $f_r$ 的选择将最终影响到物体看起来的样子(材质)

- 吸收了什么波长的光?
- 反射了多少什么波长的光?

- 哪个方向上反射更加强烈?
- 反射的出射方向(比如粗糙表面的漫反射, 导致宏观上光路没有依照反射规律)

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251214222918786.png" alt="image-20251214222918786" style="zoom:33%;" />

镜面反射

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251214223311681.png" alt="image-20251214223311681" style="zoom:50%;" />

漫反射

反射方向和入射方向无关, 再任何方向上均匀反射

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251214223318977.png" alt="image-20251214223318977" style="zoom:50%;" />

两者结合, 某个方向上看反光得像镜子, 另一个方向看就是一个漫反射表面

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251214223326643.png" alt="image-20251214223326643" style="zoom:50%;" />

逆向反射材料, 光从一个特定方向打过来, 会全部打回去

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251214223332980.png" alt="image-20251214223332980" style="zoom:50%;" />

 比如自行车尾灯

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251214223356053.png" alt="image-20251214223356053" style="zoom: 67%;" />

## 双向反射分布函数 BRDF

> **B**idirectional **R**eflectance **D**istribution **F**unction

反射问题转换成数学语言后, 即一个光粒子从给定方向达到表面后向另一个方向散射的**概率**

- 对于完全的镜面, 我们看见反射过来的图像倒立了
- 对于完全的漫反射, 我们无法看见反射过来的图像
- 对于介于两者之间的表面, 我们可以看见一个模糊的道理过来的图像

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251214224358905.png" alt="image-20251214224358905" style="zoom:50%;" />

绿色的线表示入射的光, 红色的部分表示出射的方向和各个方向对应的概率

上图描述的表面是, 有小小的漫反射现象, 但整体是镜面反射的

对于每一个方向上的概率, 概率大于0


$$
f_r (\omega_i \to \omega_o) \ge 0
$$



能量守恒, 所有方向上的概率积分在一起不超过一

小于一的, 表示光被表面吸收, 变成热

$$
\int_{H^2} f_r(\omega_i \to \omega_o)\; cos\theta \; d\omega_i \le 1
$$

光路具有对称性( **亥姆霍兹互易性** *Helmholtz reciprocity* )

这种思想是将物体表面看作了无数个细分的小镜子组成

$$
f_r( \omega_i \to \omega_o) = f_r( \omega_o \to \omega_i)
$$

$f_r$ 的单位是 $rad^{-1}$


### 漫反射 BRDF



入射光均匀地从四面八方反射出去

对于一个单位半球, 其在平面上的投影是个单位圆


$$
\int_{H^2} \cos\theta \; d \boldsymbol{\omega}_i = \pi
$$


积分等于 $\pi$ 表示单位圆的面积


$$
f_r = \frac{\rho}{\pi}
$$


- $\rho$ 物体材质的反射率, 小于等于1, 因为可能有一部分光被吸收

### 镜面反射 BRDF

可以使用向量计算入射光线 $\boldsymbol{\omega_i}$ 和出射光线 $\boldsymbol{\omega_o}$ 之间的关系

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251214230125786.png" alt="image-20251214230125786" style="zoom:50%;" />



$$
\frac{\boldsymbol{\omega_o} + \boldsymbol{\omega_i}}{2} \; = 
 \; (\boldsymbol{\omega_i} \cdot \vec{n} ) \; \vec{n}
$$



因此镜面反射 BRDF 可以表示为

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251214231322429.png" alt="image-20251214231322429" style="zoom:50%;" />

$$
f_r(\theta_i, \phi_i;\theta_o, \phi_o) \; = \; 
\frac{\delta (\cos\theta_i - \cos\theta_o)}{\cos\theta_i} \;
\delta(\phi_i - \phi_o \pm \pi)
$$


- $\delta$ Dirac delta 狄拉克 delta

  - 指示函数

  - 具有 **筛选性**, 即

    $$
    \int \delta (x−a) \; f(x) \; dx=f(a)
    $$

    对于非零值, 其结果会等于0

    对于积分, 有 $\int \delta(x)\; dx=1$ 因此对于零值, 其值会趋于无穷大

    以此筛选出 $x = a$ 的情形

  - 具有 **缩放性**

    $$
      \delta(g(x))=\sum_{i}\frac{\delta(x−x_i)}{|g'(x_i)|}
    $$

- $\pm \pi$  表示入射光线和出射光线在 $x-y$ 平面上应该是在同一条直线上的概率才不为0



证明其积分等于1

方向-半球反射率:


$$
E(\theta_i, \phi_i) = \int_{0}^{2\pi} \int_{0}^{\pi/2} f_r(\theta_i, \phi_i; \theta_o, \phi_o) \cos\theta_o \sin\theta_o \, d\theta_o \, d\phi_o
$$


代入给定BRDF: 


$$
\begin{aligned}
E &= \int_{0}^{2\pi} \int_{0}^{\pi/2} \frac{\delta (\cos\theta_i - \cos\theta_o)}{\cos\theta_i} \delta(\phi_i - \phi_o \pm \pi) \cos\theta_o \sin\theta_o \, d\theta_o \, d\phi_o \\
&= \int_{0}^{\pi/2} \frac{\delta (\cos\theta_i - \cos\theta_o)}{\cos\theta_i} \cos\theta_o \sin\theta_o \left[ \int_{0}^{2\pi} \delta(\phi_i - \phi_o \pm \pi) \, d\phi_o \right] d\theta_o \\
&= \int_{0}^{\pi/2} \frac{\delta (\cos\theta_i - \cos\theta_o)}{\cos\theta_i} \cos\theta_o \sin\theta_o \, d\theta_o
\end{aligned}
$$


利用 $\delta$ 函数性质 $\delta(g(x)) = \sum_i \frac{\delta(x-x_i)}{|g'(x_i)|}$ , 其中 $ g(\theta_o)=\cos\theta_i-\cos\theta_o $ , 零点 $\theta_o=\theta_i $ , $g'(\theta_o)=\sin\theta_o$ :


$$
\delta(\cos\theta_i-\cos\theta_o) = \frac{\delta(\theta_o-\theta_i)}{\sin\theta_o}
$$


代入: 


$$
\begin{aligned}
E &= \int_{0}^{\pi/2} \frac{1}{\cos\theta_i} \cdot \frac{\delta(\theta_o-\theta_i)}{\sin\theta_o} \cdot \cos\theta_o \sin\theta_o \, d\theta_o \\
&= \int_{0}^{\pi/2} \frac{1}{\cos\theta_i} \delta(\theta_o-\theta_i) \cos\theta_o \, d\theta_o \\
&= \frac{1}{\cos\theta_i} \int_{0}^{\pi/2} \delta(\theta_o-\theta_i) \cos\theta_o \, d\theta_o
\end{aligned}
$$


由 $\delta$ 函数筛选性: 

$$
\int_{0}^{\pi/2} \delta(\theta_o-\theta_i) \cos\theta_o \, d\theta_o = \cos\theta_i
$$


因此: 


$$
E = \frac{1}{\cos\theta_i} \cdot \cos\theta_i = 1
$$



## 折射

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215022556369.png" alt="image-20251215022556369" style="zoom:50%;" />

折射率公式


$$
\eta_i \sin\theta_i =  \eta_t sin\theta_t
$$


另一方向 $\phi$ (与上图垂直的轴上)保持不变





## 双向折射分布函数 BTDF

> **B**idirectional **T**ransmitted  **D**istribution **F**unction


$$
f_t(\omega_i,\omega_o) = \frac{1-F(\omega_i)}{|\cos\theta_i|} \delta(\omega_o - refract(\omega_i))
$$


对于反射有
$$
f_r(\omega_i,\omega_o) = \frac{F(\omega_i))}{|\cos\theta_i|}\delta(\omega_o-reflect(\omega_i))
$$


BSDF = BTDF + BRDF

$$
\int_{H^2} (f_t+f_r) \; \cos\theta_o \; d\omega_o \le 1
$$

对于 $F(\theta_i)$

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215031407623.png" alt="image-20251215031407623" style="zoom:50%;" />

## 双向次表面反射分布函数 BSSDF

出射点和入射点

拓展BRDF, 不仅考虑入射方向和出射方向, 还考虑入射点和出射点

引入由于另一点的入射微分辐照度而导致的一点意外辐照度  $S(x_i, \omega_i, x_o, \omega_o)$


$$
L(x_o,\omega_o) =  \int_A\int_{H^2} S(x_i, \omega_i, x_o, \omega_o) \; L_i(x_i,\omega_i) \; \cos\theta \; d\omega_i \: dA
$$

- $A$ : 表示整个表面积

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215032344847.png" alt="image-20251215032344847" style="zoom:50%;" />

# 光线追踪

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215033557501.png" alt="image-20251215033557501" style="zoom:50%;" />

1. 从眼睛发出射线(Primary Rays)
2. 遇到第一个遮挡物, 则射线终止
3. 在遮挡物上发生折射和反射(Secondary Rays)
4. 对于每个碰到的点, 都向光源发送 Shadow Rays 以测试光线可见度
5. 当遇到非镜面表面（或达到最大期望的递归水平）, 停止递归, 进入5, 否则, 对于每个射线, 返回2

## 检查光线和三角形网格的交集

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



## 包围体

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



我们还需要获取到射线与包围盒相交的 $t$ 的取值范围, 用于检查不同的包围盒之间的重叠问题

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215110647801.png" alt="image-20251215110647801" style="zoom:50%;" />

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



# 蒙托卡罗渲染

> Monte Carlo Rendering

输出一个图像

## 和光栅化的比较

都是输入几何体, 输出一个图像

对于光栅化

1. 遍历每个几何体
2. 遍历每个采样点
3. 采样, 测试是否在几何体内, 依据深度覆盖
4. 进行颜色渲染

对于光线追踪

1. 遍历每个采样点(一条光线路径)
2. 遍历每个几何体
3. 测试是否在光线可达
4. 进行颜色渲染

光线追踪的效果更好, 因为光栅化难以描述亮度, 除非其材质已经提前考虑到了亮度

但是光线追踪对性能的损耗更大

## 数值积分

$$
\int_a^b f(x) \;dx  \;\approx\; \sum^N_{i=0} w_i\: f(x_i);
$$

1. 对多项式进行积分, 积分结果对函数进行估计

2. 评判近似的多项式的标准是

   依次取 $x=x^0,x=x^1,x=x^2,x=x^3,x = x^4$ ... 直到等式两段不相等

   通过测试的幂次数越高, 对函数的积分估计效果越好

3. 对于一般的复杂函数, 一般进行分段的多级样条, 然后分别积分

一般就是有梯形法  $\int_a^b f(x) \;dx  \;\approx\; \frac{b-a}{2}[f(a)+f(b)]$

或者辛普森法则  $\int_a^b f(x) \;dx  \;\approx\; \frac{b-a}{6}[f(a)+4f(\frac{a+b}{2})+f(b)]$

对于 $n\to\infty, h := \frac{1}{n} \to 1 $ 存在误差$O(h^2)$



$$
\int_a^b f(x) \;dx  \;=\; \sum^N_{i=0} w_i\: f(x_i) + O(h^2)
$$


对于二重积分
$$
\begin{aligned}
\int_{a_y}^{b^y}\int_{a_x}^{b^x} f(x,y) \;dxdy 
&=  \int_{a_y}^{b^y} (\sum^N_{i=0} A_i\: f(x_i,y) + O(h^2)) \;dxdy \\
&=  \sum^N_{i=0} A_i \int_{a_y}^{b^y} f(x_i,y) \;dxdy + O(h^2) \\
&=  \sum^N_{i=0} A_i (\sum^N_{j=0} A_j\: f(x_i,y_j) + O(h^2))  + O(h^2) \\
&=  \sum^N_{i=0} \sum^N_{j=0}  A_i A_j\: f(x_i,y_j)  + O(h^2)
\end{aligned}
$$
在光线追踪中, 我们需要递归地计算光线的亮度, 而这个递归的深度, 可能是很高的, 积分的层次也会非常高

导致产生的误差也会随着递归深度的增加而增加

此乃**积分诅咒**, 积分维度越高, 误差越大

## 蒙托卡罗积分

在图中引入随机

已知一次对数值积分的估计存在误差

使用不同的随机值做积分, 都得到的结果不同

这多次结果做平均, 我们希望接近最终的结果, 并减少误差

适合在==足够高维度==的函数积分上使用



### 已知概率分布函数, 生成随机样本

概率分布函数 PDF Probability Distribution Function

累积概率分布函数, CDF, Cumulative probability Distribution Function. $P_j$ 就是前 $j$ 个小事件的概率总和

我们希望我们生成的样本服从已知的概率分布函数

1. 计算 $CDF$

2. 计算 $CDF$ 的反函数 $CDF^{-1}$

3. 生成一个随机的样本 $\xi$ , 服从取值范围在 $[0,1)$ 的 Uniform均匀分布

4. 看生成的随机样本落在 $CDF$ 的哪个样本上, $x := CDF^{-1}(\xi)$

   <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215182850602.png" alt="image-20251215182850602" style="zoom:33%;" />

   对于离散的概率分布, 不求 $CDF^{-1}$

   只需要比较  $CDF(x_i)$ 找出 $\xi$ 满足 $ CDF(x_{i-1}) \le \xi \le CDF(x_i)$ , 则 $x_i$ 即所求

5. 返回获取到的 $x$ , 保证 $x$ 服从分布 $CDF$



### 如何均匀地采样圆盘

累积分布函数 $CDF$ 无法计算, 或无法计算出 $CDF^{-1}$, 甚至是一个二维的分布函数, 为之奈何?

- $\boldsymbol{p} = (r\cos\theta,r\sin\theta)$ , $r$ 均匀分布在 $[0,1)$,  $\theta$ 均匀分布在 $[0,2\pi)$

  并不是均匀分布, $r$ 越接近 1, 应该概率越大

由几何概型, 面积能代表概率的发生, 设全事件 $\Omega$, 即事件全体发生的概率, 即1
$$
\Omega \;=\; \int_0^{2\pi}\int_0^{1} p(\theta,r) \;dr\,d\theta \;=\;1
$$
$r, \theta$ 独立, 故有 $p(\theta,r) = p(\theta)\cdot p(r) $

在面积上积分, 有
$$
A \;=\; \int_0^{2\pi}\int_0^{1} r \;dr\,d\theta \;=\; \pi
$$




已知, $\theta$ 应当是均匀分布的, $p(\theta)=\frac{1}{2\pi}$, 则 $p(r) = 2r$

则有$CDF$:  $P(\theta)=\frac{\theta}{2\pi}$, 则 $p(r) = r^2$,

求出两者的$CDF^{-1}$: $\theta = 2\pi\xi_1$ 和 $r = \sqrt{\xi_2}$



## 蒙托卡罗估计

用离散的函数的期望值, 估计函数的积分

$$
\lim_{N\to\infty}\frac{|\Omega|}{N}\sum^N_{i=1}f(x_i) \;=\; \int_{\Omega}f(x)\; dx
$$

依据大数定理, N 足够多, 方差 V 就足够小


$$
V[\frac{1}{N}\sum_{i=1}^NY_i] = \frac{1}{N^2} \sum_{i=1}^{N}V[Y_i] = \frac{1}{N^2} NV[Y] = \frac
{1}{N} V[Y]
$$


则有**蒙托卡罗估计**: 
$$
\int_\Omega f(x) \,dx \approx \frac{1}{N} \sum_{i=1}^{N} \frac{f(X_i)}{p(X_i)}
$$
对于 $p(X_i)$ 表示对 $X_i$ 取值的分布, 假设$X_i$ 是均匀分布的, $\Omega$ 为 $X \sim U(a,b)$

则 $p(X_i) = \frac{1}{b-a}$, 则蒙托卡罗估计则变为
$$
\int_a^b f(x) \,dx \approx \frac{b-a}{N} \sum_{i=1}^{N} f(X_i)
$$
就是在二维坐标平面上对积分的估算方法

当然 $\Omega$ 可以表示其他分布, 由于几何概型, 几何之间的关系可以转换成概率关系

例如 $\Omega$ 表示单位圆的时候, 即表示几何概型中点均匀落在单位圆中的概率, 这一问题了

对于均匀关照的半球

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251215213504556.png" alt="image-20251215213504556" style="zoom: 33%;" />

其辐照度为


$$
E(p) = \int_\Omega L(p,\omega) \cos\theta \, d\omega
$$
其中 $\Omega$ 代表的单位半球面面积为 $2\pi$

使用蒙托卡罗估计来计算这个积分, 对于半圆面上的任一点, 被取样的概率是 $p(x)=\frac{1}{2\pi}$


$$
E(p) \approx \frac{1}{N} \sum^N_{i=1}\frac{L(p,\omega_i)\cos\theta_i}{p(x)}
\;=\;\frac{2\pi}{N} \sum^N_{i=1}L(p,\omega_i)\cos\theta_i
$$
如何在单位半球面上均匀采样? 使用"拒绝采样"

1. 随机在$[-1,1]^3, z \ge 0$ 的空间里采样 $p=(x,y,z)$, $z\ge 0$ 
2. 如果 $p$ 在球体内, 则接收, 否则拒绝
3. $p$ 映射到点 $p' = \frac{1}{R}(x,y,z), R = \sqrt{x^2+y^2+z^2}$, 保证 $p'$ 在球面上且均匀分布



设最终取点与 $z$ 轴的夹角为 $\theta \in [0,\frac{\pi}{2})$ , 在 $x-y$ 平面上的方向角为 $\phi \in [0,2\pi)$
$$
A = \int_0^{2\pi}\int_0^{\frac{\pi}{2}} \sin \theta \; d\theta \, d\phi 
= \int_0^{2\pi}d\phi\int_0^{\frac{\pi}{2}}\sin\theta\;d\theta 
= (-\cos\theta)|^{\frac{\pi}{2}}_0\phi|^{2\pi}_0
$$
可以得出联合分布
$$
p(\theta,\phi) = \frac{\sin\theta}{2\pi},\;\;\;
\int_0^{2\pi}\int_0^{\frac{\pi}{2}} p(\theta,\phi)\; d\theta\, d\phi = 1
$$
由于, $\theta,\; \phi$ 互相独立
$$
p(\theta,\phi) = p(\theta)\cdot p(\phi)
$$
故有 $z$ 和 $\phi$ 的分布
$$
\left\{\begin{align}
 p(\theta) \;&=\; \sin(\theta), &P(\theta) \;&=\; 1-\cos\theta \\
 p(\phi) \;&=\; \frac{1}{2\pi}, &P(\phi) \;&=\; \frac{\phi}{2\pi}  \\
\end{align}\right.
$$
令 $(\xi_1,\xi_2)$ 在 $[0,1)^2$ 上均匀取值, 则可取
$$
\left\{\begin{align}
\theta\;&=\;\arccos(\xi_1) \\
\phi \;&=\; 2\pi\xi_2
\end{align}\right.
$$
转换到笛卡尔坐标系
$$
\left\{\begin{align}
 x \;&=\; \cos(\theta)\cos\phi \\
 y \;&=\; \cos(\theta)\sin\phi \\
 z \;&=\; \sin(\theta) \\
\end{align}\right.
$$
采样后, 使用被采样的点作为 **入射方向** , 计算出这个方向上的$L_i = L(p,\omega_i)$, 

而后计算一次 $\frac{2\pi}{N}L_i\cos\theta$ 记作一次递归

## 重要性采样

下图是采样了100次光线的结果

由于入射光照估计引入了随机方向, 导致一些在不同方向有不同效果的点变成了噪音

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251216011428576.png" alt="image-20251216011409143" style="zoom:33%;" />

部分指向了光源, 部分指向暗处

当累积足够多了, 就会是光源和暗影的平均, 就是一个恰当的灰了

因此可以通过尽可能多的采样来解决

但是增加采样, 效率太低, 只对对应光源的方向进行积分, 以此来进行优化

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251216042141931.png" alt="image-20251216042141931" style="zoom:50%;" />

那么, 需要采样的面就发生了更改, 积分的方式也要更改
$$
d\omega = \frac{dA}{|p'-p|^2} = \frac{\cos\theta \, dA'}{|p'-p|^2}
$$
代入, 那么就从对半个圆面的积分变成了对光源的面的积分
$$
E(p)= \int L(p,\omega)\,\cos\theta \, d\omega= \int_{A'} L_o(p,\omega)\;V(p,p')
\frac{\cos\theta \; \cos\theta'}{|p-p'|^2} 
\, dA'
$$
对于 $V(p,p')$
$$
V(p,p') \;=\; \left\{\begin{align}
  1 &&  p'\;is\;visible\;form\;p\\
  0 &&  otherwise
\end{align}\right.
$$



### 对半球的重要性采样

注意到, 由于在计算 $dE$ 的时候需要乘 $\cos\theta$, 因此越接近从平面射入的光线, 对表面亮度的影响越小

因此调整 $p(x)$, 使其不是均匀分布, 而是有意地选择垂直照射的光线, 减少水平射入的光线的权重
$$
E(p) \approx \frac{1}{N} \sum^N_{i=1}\frac{L(p,\omega_i)\cos\theta}{\cos\theta / \pi}
\;=\;\frac{2\pi}{N} \sum^N_{i=1}L(p,\omega_i)
$$
这里采用的分布是 $p=\frac{\cos\theta}{\pi}$, 表示光照的**双向反射分布函数(BRDF)**

的在空间的概率分布是一个球, 如下图所示:

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/cg/CG/image-20251216100104549.png" alt="image-20251216100104549" style="zoom:33%;" />



## 对渲染方程的Monte Carlo估计

渲染方程如下
$$
L_0(\boldsymbol{p},\omega) \; = \; L_e(\boldsymbol{p},\omega_{0}) \;
+ \; \int_{H^2}f_r(\boldsymbol{p},\omega_i \to \omega_0)\; L_i(\boldsymbol{p},\omega_i) 
\; \cos(\theta) \; d \omega_i
$$
对入射方向 $\omega_i$ 进行采样
$$
\omega_i \sim p(\omega)
$$
概率可能来自

- 采样方式(均匀采样, 余弦采样)
- 材质的BRDF

递归地
$$
\frac{1}{N} \sum_{j=1}^N \frac{f_r(p,\omega_j\to\omega_r)L_i(p,\omega_j)\cos\theta_j}{p(\omega_j)}
$$

## 俄罗斯轮盘赌

> Russian roulette

在合适停止递归? 如何创造递归条件?

我们注意到, 在一定深度的递归之后, 依旧不断进行递归的话, 收效甚微

于是, 在每次被反弹的时候, **有概率终止这条路径**
$$
L = \frac{fr(\omega_i \to \omega_o) L_i(\omega_i) \cos\theta_i}{p(\omega_i)} V(p,p')
$$
$V(p,p')$ 是可见性项, 表示该点是否被遮挡

可见性项的消耗较大, 需要**几何查询**, 因此应该避免

直接丢弃影响小的采样点的反射, 将导致**偏差**产生, 无法保证收敛到正确的值

我们希望使用一种使这个估计量**无偏差**的方式随机丢弃样本
$$
X' \;=\; \left\{\begin{align}
  \frac{X}{p_{rr}} &&  with\;probabity\;p_{rr}\\
  0 &&  with\;probabity\;1-p_{rr}
\end{align}\right.
$$


下面证明这个分布是无偏的:
$$
E[X'] = p_{rr} \cdot \frac{E[X]}{p_{rr}} + (1-p_{rr})\cdot 0 = E[X]
$$





# TODO

- 校对
- 看作业
- 去图片, 简化

文档处理

1. 删除段落前后的空行
2. 调整字体大小
3. 自定义布局
4. 调整图片大小
   1. 批量[选中](https://zhuanlan.zhihu.com/p/649198144)图片
   2. 批量[调整](https://zhuanlan.zhihu.com/p/42493664)多张图片大小
5. 列表前的序号/`-`[调整](https://support.microsoft.com/zh-cn/office/%E6%9B%B4%E6%94%B9word%E4%B8%AD%E7%9A%84%E9%A1%B9%E7%9B%AE%E7%AC%A6%E5%8F%B7%E7%BC%A9%E8%BF%9B-5ed8b9a0-d44c-4e9a-81b3-47c234e980d3)位置
6. 改变布局为两栏




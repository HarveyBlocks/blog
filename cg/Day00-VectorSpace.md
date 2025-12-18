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


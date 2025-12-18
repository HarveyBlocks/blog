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


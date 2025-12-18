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


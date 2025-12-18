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

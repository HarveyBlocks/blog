# Sampling

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
\mathcal{F}^{-1} [F_1(\omega) * F_2(\omega)] 
&= \mathcal{F}^{-1} \left[ \int_{-\infty}^{+\infty} F_1(\mu) F_2(\omega - \mu) d\mu \right] \\
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

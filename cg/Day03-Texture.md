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







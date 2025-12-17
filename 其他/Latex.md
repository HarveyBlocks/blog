# LaTeX

## 常用公式

| 类型       | LaTeX 代码               | 示例                    |
|------------|--------------------------|-------------------------|
| 上标       | `a^b`                    | $a^b$                   |
| 下标       | `a_b`                    | $a_b$                   |
| 上下标     | `a_b^c`                  | $a_b^c$                 |
| 分数       | `\frac{a}{b}`            | $\frac{a}{b}$           |
| 根号       | `\sqrt{a}`               | $\sqrt{a}$              |
| n次根号    | `\sqrt[n]{a}`            | $\sqrt[n]{a}$           |
| 积分       | `\int_a^b f(x) dx`       | $\int_a^b f(x) dx$      |
| 求和       | `\sum_{i=1}^n a_i`       | $\sum_{i=1}^n a_i$      |
| 极限       | `\lim_{x \to \infty} f(x)` | $\lim_{x \to \infty} f(x)$ |
| 乘积       | `\prod_{i=1}^n a_i`      | $\prod_{i=1}^n a_i$     |
| 微分       | `\frac{dy}{dx}`          | $\frac{dy}{dx}$         |
| 偏微分     | `\frac{\partial y}{\partial x}` | $\frac{\partial y}{\partial x}$ |
| 二项式系数 | `\binom{n}{k}`           | $\binom{n}{k}$          |


## 希腊字母

希腊字母的大写 LaTex 就是将其小写写法的首字母大写

| 字母名称 | 小写 LaTeX | 小写示例 | 大写示例 |
|----------|------------|----------|----------|
| alpha    | `\alpha`   | $\alpha$ | $\Alpha$ |
| beta     | `\beta`    | $\beta$  | $\Beta$  |
| gamma    | `\gamma`   | $\gamma$ | $\Gamma$ |
| delta    | `\delta`   | $\delta$ | $\Delta$ |
| epsilon  | `\epsilon` | $\epsilon$ | $\Epsilon$ |
| zeta     | `\zeta`    | $\zeta$  | $\Zeta$  |
| eta      | `\eta`     | $\eta$   | $\Eta$  |
| theta    | `\theta`   | $\theta$ | $\Theta$ |
| iota     | `\iota`    | $\iota$  | $\Iota$ |
| kappa    | `\kappa`   | $\kappa$ | $\Kappa$ |
| lambda   | `\lambda`  | $\lambda$ | $\Lambda$ |
| mu       | `\mu`      | $\mu$    | $\Mu$    |
| nu       | `\nu`      | $\nu$    | $\Nu$    |
| xi       | `\xi`      | $\xi$    | $\Xi$    |
| omicron  | `\omicron` | $\omicron$ | $\Omicron$ |
| pi       | `\pi`      | $\pi$    | $\Pi$    |
| rho      | `\rho`     | $\rho$   | $\Rho$  |
| sigma    | `\sigma`   | $\sigma$ | $\Sigma$ |
| tau      | `\tau`     | $\tau$   | $\Tau$  |
| upsilon  | `\upsilon` | $\upsilon$ | $\Upsilon$ |
| phi      | `\phi`     | $\phi$   | $\Phi$   |
| chi      | `\chi`     | $\chi$   | $\Chi$  |
| psi      | `\psi`     | $\psi$   | $\Psi$   |
| omega    | `\omega`   | $\omega$ | $\Omega$ |



## 方程组

| 环境     | LaTeX 代码                               | 示例                                    |
|----------|------------------------------------------|-----------------------------------------|
| cases    | `\begin{cases} x + y = 1 \\ x - y = 0 \end{cases}` | $\begin{cases} x + y = 1 \\ x - y = 0 \end{cases}$ |
| aligned  | `\begin{aligned} x &= a + b \\ y &= c - d \end{aligned}` | $\begin{aligned} x &= a + b \\ y &= c - d \end{aligned}$ |
| array    | `\begin{array}{l} x + y = 1 \\ x - y = 0 \end{array}` | $\begin{array}{l} x + y = 1 \\ x - y = 0 \end{array}$ |


## 线性代数

### 向量与表示

| 符号/概念       | LaTeX 代码                     | 示例                                                         |
| --------------- | ------------------------------ | ------------------------------------------------------------ |
| 向量 (粗体)     | `\mathbf{v}`, `\vec{v}`        | $\mathbf{v}$, $\vec{v}$                                      |
| 单位向量 (hat)  | `\hat{\imath}`, `\hat{e}_x`    | $\hat{\imath}$, $\hat{e}_x$                                  |
| 向量点积 (内积) | `\cdot` 或 `\langle , \rangle` | $\mathbf{a} \cdot \mathbf{b}$, $\langle \mathbf{a}, \mathbf{b} \rangle$ |
| 向量叉积 (外积) | `\times`                       | $\mathbf{a} \times \mathbf{b}$                               |
| 向量范数        | `\|\mathbf{v}\|`               | $\|\mathbf{v}\|$                                             |
| 零向量          | `\mathbf{0}`                   | $\mathbf{0}$                                                 |
| 向量集 (空间)   | `\mathbb{R}^n`, `\mathbb{C}^n` | $\mathbb{R}^n$, $\mathbb{C}^n$                               |

### 矩阵与运算

| 矩阵类型   | LaTeX 环境                            | 示例                                           |
| ---------- | ------------------------------------- | ---------------------------------------------- |
| 普通矩阵   | `matrix`                              | $\begin{matrix} a & b \\ c & d \end{matrix}$   |
| 圆括号矩阵 | `pmatrix`                             | $\begin{pmatrix} a & b \\ c & d \end{pmatrix}$ |
| 方括号矩阵 | `bmatrix`                             | $\begin{bmatrix} a & b \\ c & d \end{bmatrix}$ |
| 行列式     | `vmatrix`                             | $\begin{vmatrix} a & b \\ c & d \end{vmatrix}$ |
| 转置       | `A^{\top}`, `A^{T}`, `A^{\mathsf{T}}` | $A^{\top}$                                     |
| 共轭转置   | `A^{\mathsf{H}}`, `A^{\dagger}`       | $A^{\mathsf{H}}$                               |
| 逆矩阵     | `A^{-1}`                              | $A^{-1}$                                       |
| 迹         | `\operatorname{tr}(A)`                | $\operatorname{tr}(A)$                         |
| 秩         | `\operatorname{rank}(A)`              | $\operatorname{rank}(A)$                       |
| 矩阵乘法   | `AB`                                  | $AB$                                           |
| 克罗内克积 | `A \otimes B`                         | $A \otimes B$                                  |
| 矩阵元素   | `a_{ij}`, `[A]_{ij}`                  | $a_{ij}$, $[A]_{ij}$                           |

## 微积分

### 极限与连续

| 符号/概念     | LaTeX 代码                      | 示例                                                       |
| ------------- | ------------------------------- | ---------------------------------------------------------- |
| 极限          | `\lim_{x \to a} f(x)`           | $\lim_{x \to a} f(x)$                                      |
| 趋于正/负无穷 | `x \to \infty`, `x \to -\infty` | $x \to \infty$                                             |
| 上/下极限     | `\limsup`, `\liminf`            | $\limsup_{n \to \infty} a_n$, $\liminf_{n \to \infty} a_n$ |
| 无穷小量      | `o(x)`, `O(x)`                  | $o(x)$, $O(x)$                                             |

### 微分学

| 符号/概念    | LaTeX 代码                                                   | 示例                            |
| ------------ | ------------------------------------------------------------ | ------------------------------- |
| 导数         | `f'(x)`, `\frac{dy}{dx}`                                     | $f'(x)$, $\frac{dy}{dx}$        |
| 高阶导数     | `f^{(n)}(x)`                                                 | $f^{(n)}(x)$                    |
| 偏导数       | `\frac{\partial f}{\partial x}`                              | $\frac{\partial f}{\partial x}$ |
| 梯度         | `\nabla f`                                                   | $\nabla f$                      |
| 散度         | `\nabla \cdot \mathbf{F}`                                    | $\nabla \cdot \mathbf{F}$       |
| 旋度         | `\nabla \times \mathbf{F}`                                   | $\nabla \times \mathbf{F}$      |
| 拉普拉斯算子 | `\Delta f`, `\nabla^2 f`                                     | $\Delta f$, $\nabla^2 f$        |
| 雅可比矩阵   | `J_f`, `\frac{\partial (f_1,...,f_m)}{\partial (x_1,...,x_n)}` | $J_f$                           |

### 积分学

| 符号/概念 | LaTeX 代码                             | 示例                                   |
| --------- | -------------------------------------- | -------------------------------------- |
| 不定积分  | `\int f(x) \, dx`                      | $\int f(x) \, dx$                      |
| 定积分    | `\int_{a}^{b} f(x) \, dx`              | $\int_{a}^{b} f(x) \, dx$              |
| 二重积分  | `\iint_D f \, dA`                      | $\iint_D f \, dA$                      |
| 三重积分  | `\iiint_V f \, dV`                     | $\iiint_V f \, dV$                     |
| 曲线积分  | `\int_C \mathbf{F} \cdot d\mathbf{r}`  | $\int_C \mathbf{F} \cdot d\mathbf{r}$  |
| 曲面积分  | `\iint_S \mathbf{F} \cdot d\mathbf{S}` | $\iint_S \mathbf{F} \cdot d\mathbf{S}$ |
| 主值积分  | `\mathcal{P}\!\int`                    | $\mathcal{P}\!\int$                    |

### 级数

| 符号/概念      | LaTeX 代码                                                 | 示例                       |
| -------------- | ---------------------------------------------------------- | -------------------------- |
| 求和           | `\sum_{n=1}^{\infty} a_n`                                  | $\sum_{n=1}^{\infty} a_n$  |
| 无穷乘积       | `\prod_{n=1}^{\infty} a_n`                                 | $\prod_{n=1}^{\infty} a_n$ |
| 泰勒展开       | `f(x) = \sum_{n=0}^{\infty} \frac{f^{(n)}(a)}{n!}(x-a)^n`  | 略                         |
| 傅里叶级数系数 | `c_n = \frac{1}{T} \int_{0}^{T} f(t) e^{-i n \omega t} dt` | 略                         |

## 概率论

| 符号/概念           | LaTeX 代码                              | 示例                       |
| ------------------- | --------------------------------------- | -------------------------- |
| 概率                | `P(A)`, `\Pr(A)`                        | $P(A)$, $\Pr(A)$           |
| 条件概率            | `P(A \mid B)`                           | $P(A \mid B)$              |
| 随机变量            | `X`, `Y`                                | $X$, $Y$                   |
| 概率分布            | `X \sim F`                              | $X \sim F$                 |
| 期望                | `E[X]`, `\mathbb{E}[X]`                 | $E[X]$, $\mathbb{E}[X]$    |
| 方差                | `\operatorname{Var}(X)`                 | $\operatorname{Var}(X)$    |
| 协方差              | `\operatorname{Cov}(X,Y)`               | $\operatorname{Cov}(X,Y)$  |
| 相关系数            | `\rho_{XY}`                             | $\rho_{XY}$                |
| 概率密度函数        | `f_X(x)`                                | $f_X(x)$                   |
| 累积分布函数        | `F_X(x)`                                | $F_X(x)$                   |
| 独立                | `X \perp Y` 或 `X \perp\!\!\!\perp Y`   | $X \perp Y$                |
| 几乎必然/依概率收敛 | `\xrightarrow{a.s.}`, `\xrightarrow{P}` | $X_n \xrightarrow{a.s.} X$ |
| 特征函数            | `\varphi_X(t)`                          | $\varphi_X(t)$             |

## 数理统计

| 符号/概念          | LaTeX 代码                         | 示例                               |
| ------------------ | ---------------------------------- | ---------------------------------- |
| 样本均值           | `\bar{x}`, `\overline{X}`          | $\bar{x}$, $\overline{X}$          |
| 样本方差           | `s^2`, `S^2`                       | $s^2$, $S^2$                       |
| 总体均值           | `\mu`                              | $\mu$                              |
| 总体方差           | `\sigma^2`                         | $\sigma^2$                         |
| 估计量             | `\hat{\theta}`                     | $\hat{\theta}$                     |
| 极大似然估计       | `\hat{\theta}_{\text{MLE}}`        | $\hat{\theta}_{\text{MLE}}$        |
| 正态分布           | `N(\mu, \sigma^2)`                 | $N(\mu, \sigma^2)$                 |
| 卡方分布           | `\chi^2(k)`                        | $\chi^2(k)$                        |
| t分布              | `t(\nu)`                           | $t(\nu)$                           |
| F分布              | `F(d_1, d_2)`                      | $F(d_1, d_2)$                      |
| 置信区间           | `[\hat{\theta}_L, \hat{\theta}_U]` | $[\hat{\theta}_L, \hat{\theta}_U]$ |
| 假设检验：原假设   | `H_0`                              | $H_0$                              |
| 假设检验：备择假设 | `H_1` 或 `H_a`                     | $H_1$                              |
| 显著性水平         | `\alpha`                           | $\alpha$                           |
| p值                | `p\text{-value}`                   | $p\text{-value}$                   |



## 离散数学

### 逻辑

| 符号名称     | LaTeX 代码                  | 示例              |
| ------------ | --------------------------- | ----------------- |
| 合取 (与)    | `\land` 或 `\wedge`         | $p \land q$       |
| 析取 (或)    | `\lor` 或 `\vee`            | $p \lor q$        |
| 否定 (非)    | `\lnot` 或 `\neg`           | $\lnot p$         |
| 蕴含         | `\implies` 或 `\to`         | $p \implies q$    |
| 等价         | `\iff` 或 `\leftrightarrow` | $p \iff q$        |
| 全称量词     | `\forall`                   | $\forall x P(x)$  |
| 存在量词     | `\exists`                   | $\exists x P(x)$  |
| 唯一存在量词 | `\exists!`                  | $\exists! x P(x)$ |

### 集合论

| 符号名称 | LaTeX 代码               | 示例             |
| -------- | ------------------------ | ---------------- |
| 空集     | `\emptyset`              | $\emptyset$      |
| 子集     | `\subseteq`              | $A \subseteq B$  |
| 真子集   | `\subset`                | $A \subset B$    |
| 并集     | `\cup`                   | $A \cup B$       |
| 交集     | `\cap`                   | $A \cap B$       |
| 补集     | `A^c` 或 `\complement A` | $A^c$            |
| 差集     | `A \setminus B`          | $A \setminus B$  |
| 幂集     | `\mathcal{P}(A)`         | $\mathcal{P}(A)$ |
| 笛卡尔积 | `A \times B`             | $A \times B$     |
| 属于     | `\in`                    | $a \in A$        |
| 不属于   | `\notin`                 | $a \notin A$     |

### 关系与函数

| 符号名称 | LaTeX 代码              | 示例                    |
| -------- | ----------------------- | ----------------------- |
| 关系     | `a R b`                 | $a R b$                 |
| 自反闭包 | `r(R)`                  | $r(R)$                  |
| 传递闭包 | `t(R)`                  | $t(R)$                  |
| 等价类   | `[a]_{R}`               | $[a]_{R}$               |
| 函数映射 | `f: A \to B`            | $f: A \to B$            |
| 定义域   | `\operatorname{dom}(f)` | $\operatorname{dom}(f)$ |
| 值域     | `\operatorname{ran}(f)` | $\operatorname{ran}(f)$ |

### 数论

| 符号名称   | LaTeX 代码                | 示例                      |
| ---------- | ------------------------- | ------------------------- |
| 整除       | `a \mid b`                | $a \mid b$                |
| 不整除     | `a \nmid b`               | $a \nmid b$               |
| 同余       | `a \equiv b \pmod{m}`     | $a \equiv b \pmod{m}$     |
| 最大公约数 | `\gcd(a,b)`               | $\gcd(a,b)$               |
| 最小公倍数 | `\operatorname{lcm}(a,b)` | $\operatorname{lcm}(a,b)$ |
| 模运算     | `a \bmod n`               | $a \bmod n$               |
| 素数       | `p \in \mathbb{P}`        | $p \in \mathbb{P}$        |

### 图论

| 符号名称 | LaTeX 代码        | 示例              |
| -------- | ----------------- | ----------------- |
| 图       | `G = (V,E)`       | $G = (V,E)$       |
| 顶点集   | `V(G)`            | $V(G)$            |
| 边集     | `E(G)`            | $E(G)$            |
| 边       | `\{u,v\}` 或 `uv` | $\{u,v\}$ 或 $uv$ |
| 度       | `\deg(v)`         | $\deg(v)$         |
| 路径     | `P_n`             | $P_n$             |
| 圈       | `C_n`             | $C_n$             |
| 完全图   | `K_n`             | $K_n$             |
| 二部图   | `K_{m,n}`         | $K_{m,n}$         |
| 邻接矩阵 | `A(G)`            | $A(G)$            |
| 关联矩阵 | `M(G)`            | $M(G)$            |
| 树       | `T`               | $T$               |
| 叶子结点 | `\ell(T)`         | $\ell(T)$         |

### 组合数学

| 符号名称          | LaTeX 代码             | 示例           |
| ----------------- | ---------------------- | -------------- |
| 排列数            | `P(n,k)` 或 `{}_n P_k` | $P(n,k)$       |
| 组合数            | `\binom{n}{k}`         | $\binom{n}{k}$ |
| 阶乘              | `n!`                   | $n!$           |
| 二项式系数        | `\binom{n}{k}`         | $\binom{n}{k}$ |
| 卡特兰数          | `C_n`                  | $C_n$          |
| 斯特林数 (第一类) | `s(n,k)`               | $s(n,k)$       |
| 斯特林数 (第二类) | `S(n,k)`               | $S(n,k)$       |

### 布尔代数

| 符号名称 | LaTeX 代码             | 示例                  |
| -------- | ---------------------- | --------------------- |
| 与运算   | `\cdot` 或 `\land`     | $a \cdot b$           |
| 或运算   | `+` 或 `\lor`          | $a + b$               |
| 非运算   | `\bar{a}` 或 `\lnot a` | $\bar{a}$             |
| 异或     | `\oplus`               | $a \oplus b$          |
| 同或     | `\odot`                | $a \odot b$           |
| 蕴涵     | `\Rightarrow`          | $a \Rightarrow b$     |
| 等价     | `\Leftrightarrow`      | $a \Leftrightarrow b$ |

### 群与代数结构

| 符号名称 | LaTeX 代码            | 示例                  |
| -------- | --------------------- | --------------------- |
| 群       | `(G, \cdot)`          | $(G, \cdot)$          |
| 循环群   | `\mathbb{Z}_n`        | $\mathbb{Z}_n$        |
| 子群     | `H \leq G`            | $H \leq G$            |
| 正规子群 | `N \trianglelefteq G` | $N \trianglelefteq G$ |
| 商群     | `G/N`                 | $G/N$                 |
| 同态     | `\phi: G \to H`       | $\phi: G \to H$       |
| 同构     | `\cong`               | $G \cong H$           |

## 数学字体

| LaTeX 命令    | 字体样式 | 示例             |
|---------------|----------|------------------|
| `\mathrm{}`   | 正体     | $\mathrm{ABC}$   |
| `\mathit{}`   | 斜体     | $\mathit{ABC}$   |
| `\mathbf{}`   | 粗体     | $\mathbf{ABC}$   |
| `\mathsf{}`   | 等线体   | $\mathsf{ABC}$   |
| `\mathtt{}`   | 打字机体 | $\mathtt{ABC}$   |
| `\mathcal{}`  | 花体     | $\mathcal{ABC}$  |
| `\mathscr{}`  | 手写体   | $\mathscr{ABC}$  |
| `\mathfrak{}` | 哥特体   | $\mathfrak{ABC}$ |
| `\mathbb{}`   | 黑板粗体 | $\mathbb{ABC}$   |

注: `\mathbb{}` 通常需 `amssymb` 宏包.



## 常用符号

| 符号名称     | LaTeX 代码     | 示例          |
|--------------|----------------|---------------|
| 无穷大       | `\infty`       | $\infty$      |
| 箭头 (右)    | `\to`          | $\to$         |
| 箭头 (左)    | `\leftarrow`   | $\leftarrow$  |
| 箭头 (双)    | `\Leftrightarrow` | $\Leftrightarrow$ |
| 不等于       | `\neq`         | $\neq$        |
| 约等于       | `\approx`      | $\approx$     |
| 大于等于     | `\geq`         | $\geq$        |
| 小于等于     | `\leq`         | $\leq$        |
| 正负号       | `\pm`          | $\pm$         |


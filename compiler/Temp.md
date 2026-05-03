
文法：

$$
\begin{aligned}
E &\to T E' \\
E' &\to + T E' \mid \epsilon \\
T &\to F T' \\
T' &\to * F T' \mid \epsilon \\
F &\to ( E ) \mid id
\end{aligned}
$$

已知 First ：

$$
\begin{aligned}
\text{First}(E)   &= \{ (, id \} \\
\text{First}(E')  &= \{ +, \epsilon \} \\
\text{First}(T)   &= \{ (, id \} \\
\text{First}(T')  &= \{ *, \epsilon \} \\
\text{First}(F)   &= \{ (, id \}
\end{aligned}
$$

### 计算过程（按算法）

**初始化**

$\text{Follow}(E) = \{\$\}$，其余 $\emptyset$。

**第一轮**  

1. $E \to T E'$  
   - $i=0$（$T$）：后缀 $E'$，$\text{First}(E')-\{\epsilon\}=\{+\}$ → $\text{Follow}(T)=\{+\}$； 
     $\epsilon \in \text{First}(E')$ → 加 $\text{Follow}(E)=\{\$\}$ → $\text{Follow}(T)=\{+,\$\}$  
   - $i=1$（$E'$）：后缀空串，$\text{First}(\epsilon)-\{\epsilon\}=\emptyset$； 
     $\epsilon \in \text{First}(\epsilon)$ → 加 $\text{Follow}(E)=\{\$\}$ → $\text{Follow}(E')=\{\$\}$  

2. $E' \to + T E'$
   - $i=1$（$T$）：后缀 $E'$，加 $\{+\}$（已有），加 $\text{Follow}(E')=\{\$\}$ → $\text{Follow}(T)=\{+,\$\}$  
   - $i=2$（$E'$）：后缀空串，加 $\text{Follow}(E')$ 到自身（不变）  

3. $T \to F T'$  
   - $i=0$（$F$）：后缀 $T'$，$\text{First}(T')-\{\epsilon\}=\{*\}$ → $\text{Follow}(F)=\{*\}$； 
     $\epsilon \in \text{First}(T')$ → 加 $\text{Follow}(T)=\{+,\$\}$ → $\text{Follow}(F)=\{*,+,\$\}$  
   - $i=1$（$T'$）：后缀空串，加 $\text{Follow}(T)=\{+,\$\}$ → $\text{Follow}(T')=\{+,\$\}$  

4. $T' \to * F T'$  
   - $i=1$（$F$）：后缀 $T'$，加 $\{*\}$（已有），加 $\text{Follow}(T')=\{+,\$\}$ → $\text{Follow}(F)=\{*,+,\$\}$  
   - $i=2$（$T'$）：后缀空串，加自身（不变）  

5. $F \to ( E )$  
   - $i=1$（$E$）：后缀 `)`，$\text{First}(\text{')'})=\{)\}$ → 加 `)` 到 $\text{Follow}(E)$ → $\text{Follow}(E)=\{\$,)\}$  

第一轮结束：

$$
\begin{aligned}
\text{Follow}(E)   &= \{\$,)\} \\
\text{Follow}(E')  &= \{\$\} \\
\text{Follow}(T)   &= \{+,\$\} \\
\text{Follow}(T')  &= \{+,\$\} \\
\text{Follow}(F)   &= \{*,+,\$\}
\end{aligned}
$$

**第二轮**

- $E \to T E'$： 
  - $i=1$（$E'$）后缀空串 → 加 $\text{Follow}(E)=\{\$,)\}$ 到 $\text{Follow}(E')$ → $\text{Follow}(E')=\{\$,)\}$ 
  - $i=0$（$T$）后缀 $E'$，$\epsilon \in \text{First}(E')$ → 加 $\{\$,)\}$ 到 $\text{Follow}(T)$ → $\text{Follow}(T)=\{+,\$,)\}$ 
- $E' \to + T E'$： 
  - $i=1$（$T$）后缀 $E'$ → 加 $\text{Follow}(E')=\{\$,)\}$ 到 $\text{Follow}(T)$（已有）  
- $T \to F T'$： 
  - $i=0$（$F$）后缀 $T'$，$\epsilon \in \text{First}(T')$ → 加 $\text{Follow}(T)=\{+,\$,)\}$ 到 $\text{Follow}(F)$ → $\text{Follow}(F)=\{*,+,\$,)\}$  
  - $i=1$（$T'$）后缀空串 → 加 $\text{Follow}(T)$ 到 $\text{Follow}(T')$ → $\text{Follow}(T')=\{+,\$,)\}$  
- $T' \to * F T'$： 
  - $i=1$（$F$）后缀 $T'$ → 加 $\text{Follow}(T')=\{+,\$,)\}$ 到 $\text{Follow}(F)$（已包含）  

集合无变化, 终止算法。

### 最终 Follow 集

$$
\boxed{
\begin{aligned}
\text{Follow}(E)   &= \{\$, )\} \\
\text{Follow}(E')  &= \{\$, )\} \\
\text{Follow}(T)   &= \{+, \$, )\} \\
\text{Follow}(T')  &= \{+, \$, )\} \\
\text{Follow}(F)   &= \{*, +, \$, )\}
\end{aligned}
}
$$








# UV 节点

## UV节点

指定纹理图像上的点如何映射到模型的表面上

UV坐标可以同时应用在2D和3D项目

使用U和V来定义横纵坐标轴

如果缺少UV的映射, 纹理无法渲染到屏幕上

默认的UV节点是一个正方形的平面直角坐标系

输出一个四维向量`(U坐标, V坐标,Z,W)`

UV节点可以作为输入,  链接Sample textual 2D 节点来引导纹理贴在物体上

未经任何处理的UV节点和ST2D节点自带的UV(2)的映射效果是一样的

默认的UV节点映射出来的图形是正方形, 一个长宽比非1:1的图像作为纹理输入时, 被强制压缩为1:1的形式

## Sample textual 2D 节点

> ST 2D 节点/ 2D 纹理采样

2D纹理是输入的图片, 通过采样可以映射到物体的表面

2D纹理采样节点有三个输入端和五个输出端

### 输入

- `texture` 接收2D纹理
- `UV` 接收一个用于映射的UV坐标采样器
- `Type` 
  - `Default` 默认纹理
  - 法线图
- `Space` 只在法线图时启用
  - `Tangent`



创建一个ST 2D 节点 

<img src="../../assetss/Day08-UV节点/image-20250929150844895.png" alt="image-20250929150844895" style="zoom:50%;" />





## 像素化

### Posterize

- `Posterize` 分色节点, 实现简单的马赛克效果
  - 输入
    - In 输入数据
    - Steps 分色的数量/格数
  - 输出 Out 分色后的数据



<img src="../../assetss/Day08-UV节点/image-20250929150055911.png" alt="image-20250929150055911" style="zoom:33%;" />



Splite节点, 把四位向量中的某一个值去除, R(1)就是取出U坐标的变化, 也就是横向的颜色变化, 255级灰度

然后通过Posterize分色, Step=3, 就被分成了三像素

但Step>0时, 左侧一定是纯黑, 右侧只能接近纯白. 这是因为下采样, 下采样的点在一个块的最左侧

但Step<0时, 右侧一定是纯白, 左侧只能接近纯黑

Step越大, 过度越平滑





![image-20250929150459246](../../assetss/Day08-UV节点/image-20250929150459246.png)

去除Splite, 发现像素化的方式只和XY有关, 和ZW的Step的变化无关

下采样的方式一直都是左下角的颜色

### 像素画 ST2D

<img src="../../assetss/Day08-UV节点/image-20250929151027513.png" alt="image-20250929151027513" style="zoom:50%;" />

将用Posterize像素画的UV的四位向量注入ST2D中, 完成了对ST2D纹理的像素化



## 创建

Project-> 右键->Create->ShaderGraph->URP->

![image-20250929143930213](../../assetss/Day08-UV节点/image-20250929143930213.png)


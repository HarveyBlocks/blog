# Rotation

## Rotate

旋转输入UV值

<img src="../../assetss/Day08-Rotate/image-20250929161016216.png" alt="image-20250929161016216" style="zoom:50%;" />

- `Center` 旋转的中心
- `Rotation` 旋转角度
- `Unit` `Radians`/`Degree`

<img src="../../assetss/Day08-Rotate/image-20250929161246821.png" alt="image-20250929161246821" style="zoom: 67%;" />

旋转90度

## Twirl

> 旋曲节点

旋转+扭曲

![image-20250929161354789](../../assetss/Day08-Rotate/image-20250929161354789.png)

## 制作漩涡

1. 把一副图扭曲(Twirl)
2. 把扭曲的图旋转(Rotate)

![image-20250929163205228](../../assetss/Day08-Rotate/image-20250929163205228.png)

3. 中间镂空, 四周透明

   1. 准备一个素材1, 中间透明
   2. 准备一个素材2, 中间透明, 透明部分范围更大
   3. 将素材2翻转, 素材二就变成中间不透明, 外面透明
   4. 素材1和2的UV相乘, 获取一个环形上不透明, 环形内外都透明的素材
   5. 这个素材的UV和旋转的图相乘, 即可

   没有素材, 即略


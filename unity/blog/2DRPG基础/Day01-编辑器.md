# 编辑器

## 基础设置

Editor->Preference->Scence View->createObjcect As Origin 创建新对象生成在坐标原点

Editor->Preference->Colors->playmode tint 在运行模式时, 对参数的任何调整都不会生效, 故在运行模式下改变颜色做提醒

设置单栏节省空间

![image-20241016132651297](../../assets/Day01-编辑器/image-20241016132651297.png)

## 插件

上网查Unity Assets Store

Window->Package Manager->Package: UnityRegister->MyAssets

## Hierarchy

>   层级

<img src="../../assets/Day01-导入资源/image-20241016111022224.png" alt="image-20241016111022224" style="zoom:50%;" />

Hierarchy->

## giemo

>   小工具

<img src="../../assets/Day01-导入资源/image-20241016111706023.png" alt="image-20241016111706023" style="zoom:50%;" />

对于图标

<img src="../../assets/Day01-导入资源/image-20241016111745987.png" alt="image-20241016111745987" style="zoom:50%;" />

直接点击giemo, 显示/不显示小工具

可以用giemo设置icon大小等

<img src="../../assets/Day01-导入资源/image-20241016111823758.png" alt="image-20241016111823758" style="zoom: 50%;" />

## 导入资源

拖入assets文件夹下

<img src="../../assets/Day01-导入资源/image-20241016112127575.png" alt="image-20241016112127575" style="zoom:50%;" />

## 缩放锁定

<img src="../../assets/Day01-导入资源/image-20241016113159090.png" alt="image-20241016113159090" style="zoom:50%;" />

锁定之后, 按比例缩放

- 选择图片素材(不是对象)->Inspector->General->pixels Per Unit 单位像素Unit中的像素个数,调整像素比例(调成像素单位

  如图数一灰一白的格子, 就是一个像素Unit), 调成16是因为资源下载网站说资源是16*16的

  <img src="../../assets/Day01-导入资源/image-20241016113952406.png" alt="image-20241016113952406" style="zoom:33%;" />

- 选择图片素材(不是对象)->Inspector->Advanced->>Filter Mode-> 以相邻的模式放大缩小, 使用临近的像素单位点来对像素进行补充, 选择Pointer(None), 关闭

-   选择图片素材(不是对象)->Inspector->Advanced->Compression->None 关闭压缩像素

点击Apply生效

<img src="../../assets/Day01-导入资源/image-20241016114419223.png" alt="image-20241016114419223" style="zoom:33%;" />

## 叠层

可以使用Ctrl选择多个实体, 统一调整SortingLayer

![image-20241016215228912](../../assets/Day01-编辑器/image-20241016215228912.png)

-   不同的SortingLayer之间进行排序
-   统一SortingLayer之间用Order in Layer的值进行排序

<img src="../../assets/Day01-编辑器/image-20241019221523550.png" alt="image-20241019221523550" style="zoom:50%;" />

-   上方的Layer是物理检测的Layer
-   下方的Layer是图形遮挡的Layer

## 图集

完成对图片的基础设置, 图集的Sprite Mode 为Multiple

<img src="../../assets/Day01-编辑器/image-20241016130632307.png" alt="image-20241016130632307" style="zoom:50%;" />

### Sprite Editor

展示图集的详细内容

<img src="../../assets/Day01-编辑器/image-20241016130044315.png" alt="image-20241016130044315" style="zoom:50%;" />

<img src="../../assets/Day01-编辑器/image-20241016130153000-1729054925137-1.png" alt="image-20241016130153000" style="zoom:50%;" />

### Slice切割图集

获取到图集中的单个元素图

<img src="../../assets/Day01-编辑器/image-20241016130957936.png" alt="image-20241016130957936" style="zoom:50%;" />

Slice->Type->Grace By Cell Count, 依照没行/列的格子数目进行等量切割

<img src="../../assets/Day01-编辑器/image-20241016131211994.png" alt="image-20241016131211994" style="zoom:50%;" />

<img src="../../assets/Day01-编辑器/image-20241016131357179.png" alt="image-20241016131357179" style="zoom:50%;" />

Slice->Poivot 锚点, 图片中心轴的位置(图中蓝色小圆圈), 将锚点设置在底部, 方便每次防止人物时进行计算和对齐

![image-20241016131739176](../../assets/Day01-编辑器/image-20241016131739176.png)

设置锚点

<img src="../../assets/Day01-编辑器/image-20241016131915974.png" alt="image-20241016131915974" style="zoom:50%;" />

点击Slice进行切割, 点击Apply完成对图集的操作, 退出后在inspector处确认Apply(Ctrl+S亦可以保存)

![image-20241016132256006](../../assets/Day01-编辑器/image-20241016132256006.png)

点击查看切割后的元素

人物动画, 背景元素皆同理

### Sprite Atlas

合并切片

如果切片将一个个体切成了好几部分, 例如下面[宝箱](####瓦片地图), 被四等分了, 想要他们合在一起

Porject->Asset->Create->2D->SpriteAtlas

## 瓦片地图瓦片调色盘

Window->2D->Tile Palette

新建瓦片调色盘

<img src="../../assets/Day01-编辑器/image-20241016212126180.png" alt="image-20241016212126180" style="zoom:50%;" />

将切割好的图集拖拽到Tile Palette中

### 瓦片地图

<img src="../../assets/Day01-编辑器/image-20241016212727969.png" alt="image-20241016212727969" style="zoom:50%;" />

-   Hexagnal-Flat-Top  六边形(上面平)
-   Hexagonal-Pointed-Top 六边形(上面尖)
-   Isometric 等轴侧(等腰直角三角形)
-   Isometric Z as Y
-   Rectagular 矩形

<img src="../../assets/Day01-编辑器/image-20241016213044867.png" alt="image-20241016213044867" style="zoom:50%;" />

TilePallette 识别到了实体TileMap

### 工具

-   同样的按钮点两次就可以取消

    <img src="../../assets/Day01-编辑器/image-20241016215910058.png" alt="image-20241016215910058" style="zoom:67%;" />

-   笔刷状态下按下Shift键执行擦除

-   Editor->Preference->TilePalette的功能加入

    键盘上的俩中括号对瓦片进行旋转

    鼠标的中键对瓦片进行镜像

<img src="../../assets/Day01-编辑器/image-20241016215635357.png" alt="image-20241016215635357" style="zoom:50%;" />

### Focus

在选中TilePalette的Tilemap之后, 即可在左下角见到TilemapFocus

<img src="../../assets/Day01-编辑器/image-20241016220727417.png" alt="image-20241016220727417" style="zoom:50%;" />

设置成某一Tilemap, 即使被遮挡也可以被看见, 也能忽略本层之后的Tilemap

<img src="../../assets/Day01-编辑器/image-20241016220923753.png" alt="image-20241016220923753" style="zoom:50%;" />

### 带规则的瓦片

<img src="../../assets/Day01-编辑器/image-20241017163822988.png" alt="image-20241017163822988" style="zoom:50%;" />

Project->Create->2D->Tiles->RuleTIle

创建后选择素材

<img src="../../assets/Day01-编辑器/image-20241017164152745.png" alt="image-20241017164152745" style="zoom:50%;" />

创建规则

<img src="../../assets/Day01-编辑器/image-20241017165323980.png" alt="image-20241017165323980" style="zoom:50%;" />

设置出现规则(周围有什么方块时出现), 设置随机选取(从哪几种切片中随机选取)

<img src="../../assets/Day01-编辑器/image-20241017170208171.png" alt="image-20241017170208171" style="zoom:50%;" />

将规则的瓦片拖入

<img src="../../assets/Day01-编辑器/image-20241017170822076.png" alt="image-20241017170822076" style="zoom:50%;" />

折角发生鬼畜

<img src="../../assets/Day01-编辑器/image-20241017171152351.png" alt="image-20241017171152351" style="zoom:50%;" />

微调规则

<img src="../../assets/Day01-编辑器/image-20241017171406253.png" alt="image-20241017171406253" style="zoom:50%;" />

折角还是非常生硬

<img src="../../assets/Day01-编辑器/image-20241017171921255.png" alt="image-20241017171921255" style="zoom:50%;" />

这一部分就是拐角的素材

<img src="../../assets/Day01-编辑器/image-20241017171849541.png" alt="image-20241017171849541" style="zoom:50%;" />

OK

对于类似逻辑的, 可以使用Ctrl+D复制一份Land逻辑, 然后修改图片即可

### 动画瓦片

此三张图一组, 四组图, 看作四帧, 循环播放, 流水作业, 看似流动

<img src="../../assets/Day01-编辑器/image-20241017174819762.png" alt="image-20241017174819762" style="zoom:50%;" />

添加动画瓦片

<img src="../../assets/Day01-编辑器/image-20241017163822988.png" alt="image-20241017163822988" style="zoom:50%;" />

Project->Create->2D->Tiles->AnimatedTail

先制作一条, 从上到下四帧

调整动画各帧, 调整播放速度

<img src="../../assets/Day01-编辑器/image-20241017175739880.png" alt="image-20241017175739880" style="zoom:50%;" />

然后复制, 完成左中右三条

![image-20241017175839835](../../assets/Day01-编辑器/image-20241017175839835-1729159126953-1.png)

拖动到TailPalette

<img src="../../assets/Day01-编辑器/image-20241017175943590.png" alt="image-20241017175943590" style="zoom:50%;" />


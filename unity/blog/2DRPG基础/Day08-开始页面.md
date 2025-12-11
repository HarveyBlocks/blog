# 开始页面

创建新场景, 为新场景创建一个普通Camera



## 文本

Text Mesh Pro

创建UI->TextMeshPro对象, 导入有关包

不支持中文

字体添加略

### 中文字体

Unity中的字体和图片类似, 是渲染出来的, 所以需要有相关的中文字体才能显示中文

素材自己去网上查

字体导入

1.   将字体的`.ttf`的文件拖入Assets目录下

2.   该`.ttf`的文件右键->create->TextMeshPro->Font Asset->创建字体资源

3.   Project->选中该字体资源->Inspector->Generation Settings -> 

     -   Sample Point Size 字体调小
     -   Padding 间距调小
     -   Atlas Width 画布调大

     如果空间不够, 无法渲染, 文字就会变成方框 

4.   Hierachy->选中TMP->Inspector->Font Asset -> 选择字体资源

### 字体样式

-   TMP->Inspector-> Color Gradient 颜色渐变
-   Inspector->Extra Settings 其他设置
-   

### Vertical Layout Group

组件. 统一管理一组文本框



## 按钮

Create->Ui->Button(Text Mesh Pro)或Create->Ui->(Text Mesh Pro)->Add Component->Button

- Inspector->Buttom->
    - Hithlighted Color 设置鼠标靠近的颜色转变
    - Pressed Color 被点击不松开时的颜色
    - Selected Color 被选中后的颜色
    - 该颜色的设置是图像原有的颜色进行叠加



## 游戏进度保存


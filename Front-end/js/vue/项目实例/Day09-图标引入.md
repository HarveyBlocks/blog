# ali(阿里)图标引入

此处不引入, 因为命名不规范(用英文搜至少不会有拼音), 不喜欢

1.   在[图片库](www.iconfont.cn)里搜索一下目标图标

     ![image-20250827050309623](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/vue/项目实例/Day09-图标引入/image-20250827050309623.png)

2.   加入购物车

     ![image-20250827050553779](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/vue/项目实例/Day09-图标引入/image-20250827050553779.png)

3.   添加至项目

     ![image-20250827050621181](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Front-end/js/vue/项目实例/Day09-图标引入/image-20250827050621181.png)

4.   会生成css文件, 在html的head里导入阿里图标库链接

     ```html
     <link rel="stylesheet" href="//at.alicdn.com/t/... 生成的文件地址">
     ```

5.   选择的icon, 会有font class, 选择的图标会有`wechat`提示, 填入下面xxx

     ```html
     <i class="iconfont icon-wechat"></i>
     ```

     在代码中引用, 其中, icon-wechat

     ```html
     <i class="iconfont icon-wechat"></i>
     ```


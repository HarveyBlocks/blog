# APK文件

Android系统要求只有**签名**后的APK文件才可以安装

## 生成

<img src="../assets/Day16-APK文件/image-20250927034957554.png" alt="image-20250927034957554" style="zoom:50%;" />

页面

<img src="../assets/Day16-APK文件/image-20250927035057436.png" alt="image-20250927035057436" style="zoom:67%;" />

- Android App Bundle 用于商家Google Play(不适用于其他应用商店), 可以根据用户的手机, 只下载一部分资源
- APK 能直接安装

<img src="../assets/Day16-APK文件/image-20250927035244613.png" alt="image-20250927035244613" style="zoom:67%;" />

点击Create New... , 弹出下面窗口

填写信息(就是JKS), 以下略

<img src="../assets/Day16-APK文件/image-20250927035525697.png" alt="image-20250927035525697" style="zoom:50%;" />

选择构建类型release, 因为是正式版

<img src="../assets/Day16-APK文件/image-20250927035750412.png" alt="image-20250927035750412" style="zoom:50%;" />

apk文件就在目标目录下了

![image-20250927040047267](../assetss/Day16-APK文件/image-20250927040047267.png)

## Gradle 生成

略


# 深色主题

Android 10.0 引入系统

## Force Dark

让应用程序快速适配深色主题

会分析浅色主题应用下的每一层 View，并且在这些View绘制到屏幕之前，自动将它们的颜色转换成更加适合深色主题的颜色

只有原本使用浅色主题的应用才能使用Force Dark，如果应用原本使用的就是深色主题，Force Dark将失效

由于其在Andoid 10.0 (Api 29)开始才支持, 因此需要进行一些系统差异型编程

1. 在`res`下创建`values-v29`, 版本高于29的, 使用这个文件夹下的文件

2. `values-v29`目录下创建`styles.xml`

3. 编写相关样式

   ```xml
   <resources>
       <style name="Base.Theme.FirstAndroid" parent="Theme.MaterialComponents.Light.NoActionBar">
           <item name="colorPrimary">@color/light_primary</item>
           <item name="colorPrimaryDark">@color/dark_primary</item>
           <item name="android:windowBackground">@android:color/white</item>
           <item name="colorSecondary">@color/accent</item>
           <item name="android:forceDarkAllowed">true</item>
       </style>
       <style name="Theme.FirstAndroid" parent="Base.Theme.FirstAndroid" />

       <style name="Theme.FruitDetail" parent="Base.Theme.FirstAndroid">
           <item name="android:statusBarColor">@android:color/transparent</item>
       </style>
   </resources>
   ```

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/Material Design/Day14-深色主题/image-20250926225206452.png" alt="image-20250926225206452" style="zoom:50%;" />

不尽人意, 因此应该自己详细设计颜色

## 深色Color配色

创建给night的配色

![image-20250926225644191](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/Material Design/Day14-深色主题/image-20250926225644191.png)

light的colors有的, night里有需要的, 都重写一份, 到时候从light换到night的时候, 同样name的color, 会采用night, 而不是light

如果night没有的, 沿用light的

下面是night/colors.xml

```xml
<resources>
    <color name="light_primary">#303030</color>
    <color name="dark_primary">#232323</color>
</resources>
```

![image-20250926230624227](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/Material Design/Day14-深色主题/image-20250926230624227.png)

## 代码中判断主题

```kotlin
fun Context.isDarkTheme(): Boolean {
    return this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}
```


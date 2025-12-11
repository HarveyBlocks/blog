# 使用

![img](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/glide/Day17-使用/v2-35025ef2bd08c19887f6b1cf928f9f17_1440w.jpg)

## load resource

```kotlin
// 加载本地图片
File file = new File(getExternalCacheDir() + "/image.jpg");
Glide.with(this).load(file).into(imageView);

// 加载应用资源
int resource = R.drawable.image;
Glide.with(this).load(resource).into(imageView);

// 加载二进制流
byte[] image = getImageBytes();
Glide.with(this).load(image).into(imageView);

// 加载Uri对象
Uri imageUri = getImageUri();
Glide.with(this).load(imageUri).into(imageView);
```

## placeholder

图片被加载之间显示的图片

```kotlin
Glide.with(this)
     .load(url)
     .placeholder(R.drawable.loading)
     .into(imageView);
```

错误发生(例如网络链接)时产生的占位图

```kotlin
Glide.with(this)
     .load(url)
     .error(R.drawable.error)
     .into(imageView);
```

## 图片格式

Glide自动适配gif格式, 不需要另外的配置

但如果要限制图片格式, 例如必须是静态图片, 此时如果是gif, 那么卡在第一帧

```kotlin
Glide.with(this)
     .load(url)
     .asBitmap()
     .into(imageView);
```

如果要限制必须是动态图片, 此时如果传入的是静态图片, 那么走error(), 加载错误占位图

```kotlin
Glide.with(this)
     .load(url)
     .asGif()
     .into(imageView);
```

## 缓存功能

Glide有自动缓存的功能, 下面是使用`DiskCacheStrategy.NONE`常量禁用缓存

```kotlin
Glide.with(this)
     .load(url)
     .placeholder(R.drawable.loading)
	.diskCacheStrategy(DiskCacheStrategy.NONE)
     .into(imageView);
```

## 图片压缩

Glide自动识别目标ImageView的能显示的图片大小(分辨率), 防止出现占用大量内存而显示体验没有增加的情况

下面是手动设置图片大小的方法

```kotlin
Glide.with(this)
     .load(url)
     .override(100, 100) // 单位为像素
     .into(imageView);
```


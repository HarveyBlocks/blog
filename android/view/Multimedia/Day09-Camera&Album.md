# Camera&Album

> 相机和相册



## Camera

### 布局

布局上有按钮, 用于打开相机, 有ImageView, 用于打开Image

```xml
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

    <Button
            android:id="@+id/takePhotoBtn"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Take Photo" />
    <ImageView
            android:id="@+id/imageView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_horizontal"
            android:contentDescription="no image here" />
</FrameLayout>
```

### 注册FileProvider

在xml文件夹下创建file_paths.xml

<img src="../../assetss/Day09-Camera&Album/image-20250919175218652.png" alt="image-20250919175218652" style="zoom:50%;" />

在其中编写

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path name="my_images" path="/" />
</paths>
```

external-path就是用来指定Uri共享路径的

path属性的值表 示共享的具体路径

name是本路径的标识



在manifest注册FileProvider, 其中`authorities`自定义

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="org.harvey.android.first">

    <!--...-->
    <application
            android:allowBackup="true"
            android:dataExtractionRules="@xml/data_extraction_rules"
            android:fullBackupContent="@xml/backup_rules"
            android:icon="@mipmap/ic_launcher"
            android:label="@string/app_name"
            android:roundIcon="@mipmap/ic_launcher_round"
            android:supportsRtl="true"
            android:theme="@style/Theme.FirstAndroid">
        <provider
                android:name="androidx.core.content.FileProvider"
                android:authorities="org.harvey.android.first.provider.file"
                android:exported="false"
                android:grantUriPermissions="true">
            <meta-data
                    android:name="android.support.FILE_PROVIDER_PATHS"
                    android:resource="@xml/file_paths" />
        </provider>
        <!--...-->
    </application>

</manifest>
```

### 代码部分

#### 使用 FileProvider 存放照片结果

准备一个用于存放照片结果的URI, 照片拍好之后, 将图片写入这个URI对应的文件

```kotlin
// externalCharDir 是指 /sdcard/Android/data/<package name>/cache
// 其中sdcard是个链接文件, 指/storage/emulated/0/
// 也就是说, 实际上是/storage/emulated/0/Android/data/<package name>/cache
// 读写/storage/emulated/0/Android/data/<package name>/ 下的文件不需要另外申请权限
val outputImage = File(externalCacheDir, "output_image.jpg")
if (outputImage.exists()) {
    outputImage.delete()
}
outputImage.createNewFile()
imageUri = if (support(Build.VERSION_CODES.N)) {
    // Android 7.0之后, 认为需要安全, 于是使用FileProvider.getUriForFile封装更安全
    FileProvider.getUriForFile(
        this, // context
        "org.harvey.android.first.provider.file", // 与上面注册的authorities一致
        outputImage // image的File文件
    )
} else {
    // 老版本直接使用Uri
    Uri.fromFile(outputImage)
}
```


#### Intent 的 Action

Intent到相机程序的Action

```kotlin
enum class SystemReceiverAction(override val action: String) : ReceiverAction {
    // ...
    IMAGE_CAPTURE("android.media.action.IMAGE_CAPTURE");
}
```



#### 打开相机

在Extra中设置`MediaStore.EXTRA_OUTPUT`, 就能指定要把目标图片存到哪里

```kotlin
// 启动相机程序
val intent = Intent(SystemReceiverAction.IMAGE_CAPTURE.action)
intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
launcher.launch(intent) // 已经注册了intent的回调的launcher
```


#### 获取图片并展示

编写lancher字段

```kotlin
val launcher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            //  将拍摄的照片显示出来
            val openInputStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(openInputStream)
            binding.imageView.setImageBitmap(bitmap)
        }
    }
```



#### 代码清单

```kotlin
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {


    lateinit var imageUri: Uri

    val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                //  将拍摄的照片显示出来
                val openInputStream = contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(openInputStream)
                binding.imageView.setImageBitmap(bitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.takePhotoBtn.setOnClickListener {
            // externalCharDir 是指 /sdcard/Android/data/<package name>/cache
            // 其中sdcard是个链接文件, 指/storage/emulated/0/
            // 也就是说, 实际上是/storage/emulated/0/Android/data/<package name>/cache
            // 读写/storage/emulated/0/Android/data/<package name>/ 下的文件不需要另外申请权限
            val outputImage = File(externalCacheDir, "output_image.jpg")
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
            imageUri = if (support(Build.VERSION_CODES.N)) {
                // Android 7.0之后, 认为需要安全, 于是使用FileProvider.getUriForFile封装更安全
                FileProvider.getUriForFile(
                    this,
                    "org.harvey.android.first.provider.file",
                    outputImage
                )
            } else {
                // 老版本直接使用Uri
                Uri.fromFile(outputImage)
            }
            // 启动相机程序
            val intent = Intent(SystemReceiverAction.IMAGE_CAPTURE.action)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            launcher.launch(intent)
        }
    }
}
```





## Album

### 布局

增加一个按钮, 从相册中选择

```xml
<Button
        android:id="@+id/searchAlbumBtn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Search Album" />
```

### launcher

```kotlin
val searchAlbumLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val bitmap: Bitmap? = result.data?.data?.let {
                contentResolver.openFileDescriptor(it, "r")
            }?.use {
                BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
            }
            // 开屏
            binding.imageView.setImageBitmap(bitmap)
        }
    }
```

#### 打开图片文件

```kotlin
// 打开文件选择器
val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
intent.addCategory(Intent.CATEGORY_OPENABLE)
//  指定只显示图片
intent.type = "image/*"
searchAlbumLauncher.launch(intent)
```



## 作用域存储与MediaStore

从Android 10开始，每个应用程序只能有权在自己的外置存储空间关联目录下读取和创建文件

使用`context.getExternalFilesDir()`API获取该关联目录

读写本应用的媒体将会自动拥有其读写权限

而访问其他应用的媒体, 需要额外申请`READ_EXTERNAL_STORAGE`和`WRITE_EXTERNAL_STORAGE`权限

`WRITE_EXTERNAL_STORAGE`, 在Android14+废弃

### 获取相册中的图片

permissions



```kotlin
@SuppressLint("InlinedApi")
enum class Permission(val permission: String, val versionRange: IntRange) {
    READ_MEDIA_IMAGES(
        Manifest.permission.READ_MEDIA_IMAGES,
        Build.VERSION_CODES.TIRAMISU..ApiVersion.SUPPORT_UPPER
    ),
    READ_EXTERNAL_STORAGE(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        ApiVersion.SUPPORT_LOWER..Build.VERSION_CODES.S_V2
    ),
    POST_NOTIFICATIONS(
        Manifest.permission.POST_NOTIFICATIONS,
        Build.VERSION_CODES.TIRAMISU..ApiVersion.SUPPORT_UPPER
    )
}
```







在manifest注册permission

```xml
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES"/>

<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
```

```kotlin
safeWrapper(
    arrayOf(Permission.READ_MEDIA_IMAGES,  Permission.READ_EXTERNAL_STORAGE), "image"
) {
    val cursor = contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // Images可以更换成Video等Audio
        null,
        null,
        null,
        "${MediaStore.MediaColumns.DATE_ADDED} desc"
    )
    cursor?.use {
        while (cursor.moveToNext()) {
            val id =
                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
            val uri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
            )
            println("image uri is $uri")
            val bitmap = contentResolver.openFileDescriptor(uri, "r")?.use {
                BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
            }
            binding.imageView.setImageBitmap(bitmap)
        }
    }
}.invoke()
```





## [问题](TODO)

1. 为什么intent后打开的是简陋的照相, 而不是完整的照相功能, 或者为什么手机厂商不给这个intent后的目标搞得高级一点, 或者直接跳到优化过的照相
2. 既然intent打开照相不需要权限, 为什么别的应用不使用intent打开照相, 而是用Read, 为什么一定要侵犯用户隐私


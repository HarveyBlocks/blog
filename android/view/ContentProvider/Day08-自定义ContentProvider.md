# 自定义ContentProvider

保证数据的安全，使得隐私数据不会泄漏出去

## 步骤

### 继承ContentProvider

```kotlin
class MyContentProvider : ContentProvider() {

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun onCreate(): Boolean {
        TODO("Not yet implemented")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor? {
        TODO("Not yet implemented")
    }
    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        TODO("Not yet implemented")
    }
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        TODO("Not yet implemented")
    }
}
```

### URI

`content://org.harvey.android/student_table`表示一张表

`content://+/student_table/1`表示表中id为1的记录

可以用**`UriMatcher`**来匹配URI, 其中`*`表示匹配任意长度的**任意字符**,`#`表示匹配任意长度的**数字**

```kotlin
companion object {
    private const val STUDENT_TABLE = 0
    private const val STUDENT_ITEM = 1
    private const val TEACHER_TABLE = 2
    private const val TEACHER_ITEM = 3
}

private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

init {
    val host = "org.harvey.android"
    val table1 = "tb_student"
    val table2 = "tb_teacher"
    uriMatcher.addURI(host, table1, STUDENT_TABLE)
    uriMatcher.addURI(host, "$table1/#", STUDENT_ITEM)
    uriMatcher.addURI(host, table2, TEACHER_TABLE)
    uriMatcher.addURI(host, "$table2/#", TEACHER_ITEM)
}

override fun query(
    uri: Uri,
    projection: Array<String>?,
    selection: String?,
    selectionArgs: Array<String>?,
    sortOrder: String?
): Cursor? {
    when (uriMatcher.match(uri)) {
        TEACHER_TABLE -> TODO("查询teacher table的所有成员")
        TEACHER_ITEM -> TODO("查询一条teacher记录")
        STUDENT_TABLE -> TODO("查询student table的所有成员")
        STUDENT_ITEM -> TODO("查询一条student记录")
        else -> error("unknown url")
    }
}
```

### 重载函数

- onCreate

  - 初始化ContentProvider的时候调用。
  - 通常会在这里完成对数据库的创建和升级
  - 返回true表示ContentProvider初始化成功，false则表示失败。

- query

  - uri 参数确定查询哪张表
  - projection 确定查询哪些列
  - selection where语句
  - selectionArgs 实参
  - sortOrder, 排序语句
  - 返回结果作为Cursor返回

- getType

  根据传入内容URI返回相应的MIME类型, 

### MIME

getType返回MIME类型, MIME类型有一定要求

1. 必须以`vnd.`开头

2. 如果内容URI以路径结尾, 则后接`android.cursor.dir/`

   如果内容URI以id结尾, 则后接`android.cursor.item/`

3. 最后是`vnd.<authority>.<path>`

例如, `content://org.harvey.android/student_table`这个URI, MIME表示为

```mime
vnd.android.cursor.dir/vnd.org.harvey.android.student_table
```

`content://org.harvey.android/teacher_table/14`, 在MIME表示为

```MIME
vnd.android.cursor.item/vnd.org.harvey.android.teacher_table
```

## 注册

### 提供处注册

使用Android Studio的创建工具, 可以自动注册

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/ContentProvider/Day08-自定义ContentProvider/image-20250918191500156.png" alt="image-20250918191500156" style="zoom:50%;" />

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/ContentProvider/Day08-自定义ContentProvider/image-20250918191651686.png" alt="image-20250918191651686" style="zoom: 33%;" />

- Exported 表示是否开放给外界程序
- Enable表示是否启用

查看manifest上的注册情况

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="org.harvey.android.provider">

    <!--...-->

    <application ...>
        <provider
                android:name=".content.MyContentProvider"
                android:authorities="org.harvey.android.content"
                android:enabled="true"
                android:exported="true" />

        <!--...-->
    </application>

</manifest>
```

这样, 其他程序也能访问(不需要权限)

### 请求处注册

请求处注册对其他应用读取数据的权限

Android 11 后的要求

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="org.harvey.android.requester">

    <!--下面两个配置二选一(也可以都选)-->
    <queries>
        <!--目标应用的package, manifest标签上的属性package-->
        <package android:name="org.harvey.android.provider"/>
        <!--目标的authorities-->
        <provider android:authorities="org.harvey.android.content"/>
    </queries>

    <!--..-->

</manifest>
```

## 自定义Permission

在provider处给ContentProvider设置permission

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        package="org.harvey.android.provider">
    <!--声明一个permission-->
    <permission android:name="org.harvey.android.permission.MyPermission" 
                android:protectionLevel="normal"/>
    <application
            android:allowBackup="true"
            android:dataExtractionRules="@xml/data_extraction_rules"
            android:fullBackupContent="@xml/backup_rules"
            android:icon="@mipmap/ic_launcher"
            android:label="@string/app_name"
            android:roundIcon="@mipmap/ic_launcher_round"
            android:supportsRtl="true"
            android:theme="@style/Theme.OtherApplication">
        <provider
                android:name=".content.MyContentProvider"
                android:authorities="org.harvey.android.content"
                android:permission="org.harvey.android.permission.MyPermission" 
                android:enabled="true"
                android:exported="true" />
        <!--///-->
    </application>

</manifest>
```

如果requester不申请权限, 则直接报错

![image-20250918210123908](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/ContentProvider/Day08-自定义ContentProvider/image-20250918210123908.png)

申请权限

```kotlin
<uses-permission android:name="org.harvey.android.permission.MyPermission" />
```

自定义权限的属性`protectionLevel`可以设置成dangerous, 理论上是希望requester动态申请应用, 但实际不可用.

这种情况, provider可以开放一个Activity(permission normal), 在Activity里确保用户知情和同意, 并以此返回给requester数据

缺点是要渲染一个页面, 增加了系统负担(无中生有系列)


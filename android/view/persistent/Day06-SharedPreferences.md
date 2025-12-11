# SharedPreferences

使用键值对的方式来存储数据

支持多种不同的数据类型存储(int存入, 可以用int读出, string存入, 可以用string读出)



SharedPreferences文件都是存放在`/data/data/<package name>/shared_prefs/`目录下

## 创建SharedPreferences对象

`Context.getSharedPreferences` 

- 参数 指定SharedPreferences文件的名称
  - 如果指定的文件不存在则会创建一个
- 参数 指定操作模式
  - 目前只有默认的  **`MODE_PRIVATE`** 这一种模式可选
  - 只有当前的应用程序才可以对这个SharedPreferences进行读写



`Activity.getPreferences`

使用这个方法时会自动将**当前Activity的类名作为 SharedPreferences的文件名**

- 参数, 操作模式,   **`MODE_PRIVATE`** 

## 写入

1. `SharedPreferences.edit()`方法获取一个SharedPreferences.Editor对象。
2. 向SharedPreferences.Editor对象中添加数据，比如putBoolean()方法/putString()方法
3. 调用apply()方法将添加的数据提交

```kotlin
fun save() {
    val preferences = getPreferences(MODE_PRIVATE)
    val edit = preferences.edit()
    edit.putInt("number", 1)
    edit.putString("message", "msg")
    edit.putBoolean("flag", true)
    edit.apply()
}
```

使用KTX优化

```kotlin
fun save() {
    val preferences = getPreferences(MODE_PRIVATE)
    preferences.edit {
        putInt("number", 1)
        putString("message", "msg")
        putBoolean("flag", true)
    }
}
```

<img src="../../assetss/Day06-SharedPreferences/image-20250916203246324.png" alt="image-20250916203246324" style="zoom:50%;" />

其实就是xml文件

```xml
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
    <int name="number" value="1" />
    <boolean name="flag" value="true" />
    <string name="message">msg</string>
</map>
```

使用`edit.clear()`清空所有数据



## 读取

一个get对应一个put, get的第二个参数是默认参数

```kotlin
fun show() {
    val preferences = getPreferences(MODE_PRIVATE)
    preferences.run {
        Log.i(logTag, "${getInt("number", 0)}")
        Log.i(logTag, "${getString("message", "")}")
        Log.i(logTag, "${getBoolean("flag", false)}")
        Log.i(logTag, "${getString("not_exist", "no data")}")
    }
}
```

![image-20250916204200909](../../assetss/Day06-SharedPreferences/image-20250916204200909.png)


# KTX

Android 库中的一组 Kotlin 扩展程序

可以为 Jetpack、Android 平台及其他 API 提供简洁的惯用 **Kotlin 代码**

这些扩展程序利用了多种 Kotlin 语法糖, 使其更符合 Kotlin 风格

- 扩展函数
- 扩展属性
- Lambda
- 命名参数
- 参数默认值
- 协程

## Fragment

从view获取Fragment

```kotlin
val leftFragment = binding.leftLayout.getFragment<LeftFragment>()
```

- 保证了类型安全
- 避免使用id
- 避免一定要使用FragmentManager

事务

```kotlin
private fun replaceFragment(
    layoutId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = true,
) {
    // KTX
    supportFragmentManager.commit {
        replace(layoutId, fragment)
        if (addToBackStack) {
            addToBackStack(/* name = */ null)
        }
    }
}
```


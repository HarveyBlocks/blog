# 工具类代码清单

## common

### util

#### system

ApiVersion

```kotlin
object ApiVersion {
    /**
     * 和Module配置有关
     */
    const val SUPPORT_LOWER = 24
    /**
     * 和Module配置有关
     */
    const val SUPPORT_UPPER = 36

    /**
     * @return true if support
     */
    fun support(lower: Int): Boolean = Build.VERSION.SDK_INT >= lower

    /**
     * @return true if support
     */
    fun support(range: IntRange): Boolean = Build.VERSION.SDK_INT in range

    /**
     * @param range 如果版本不在此参数内, 则执行block, 否则打印警告日志
     * @param hint 对block的执行任务的语言描述(可选)
     * @param block 如果符合版本则执行的任务(可选)
     * @return true if support
     */
    inline fun support(range: IntRange, hint: String = "the task", block: () -> Unit = {}) =
        if (support(range)) {
            block()
            true
        } else {
            logWarn("do not support $hint for api version not in the range of from api ${range.first} to ${range.last}")
            false
        }

    /**
     * @param lower 如果版本比参数新, 则执行block, 否则打印警告日志
     * @param hint 对block的执行任务的语言描述(可选)
     * @param block 如果符合版本则执行的任务(可选)
     * @return true if support
     */
    inline fun support(lower: Int, hint: String = "the task", block: () -> Unit = {}) =
        if (support(lower)) {
            block()
            true
        } else {
            logWarn("do not support $hint for api version lower than ${lower}")
            false
        }

}
```


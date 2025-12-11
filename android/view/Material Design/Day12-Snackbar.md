# Snackbar

Toast能够通知, 但无法选择, Snackbar, 可以让用户进行一些交互

## 创建

```kotlin
@Suppress("NOTHING_TO_INLINE")
inline fun snackbar(
    view: View,
    msg: String,
    actionDescription: CharSequence? = null,
    actionListener: View.OnClickListener? = null
) {
    // 当前界面布局的任意一个View
    Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).setAction(actionDescription, actionListener)
        .show()
}
```

Snackbar会使用参数View, 从View开始往外, 自动查找外层的布局，用于展示提示信息

## Undo

例如在删除操作之后弹出Snackbar, 而后用户如果希望, 可以取消这个操作

```kotlin
binding.floatingActionButton.setOnClickListener { vo ->
    snackbar(vo, "delete succeed", "undo") { vi ->
        logger.info("${vo === vi}") // false
        toast("undo delete succeed")
    }
}
```

展示出的Snackbar将FloatingActionButton遮挡了, 我们希望CoordinatorLayout弹出时, FloatingActionButton能够提升1

## CoordinatorLayout

一般情况下与FrameLayout一致

但是CoordinatorLayout可以监听其所有子控件(子孙控件)的各种事件

从一个子组件监听某一个事件(一个属于Material Design的事件)的发生, 并联系另一个子组件是否对这个事件做协同的反应

例如, 主页面滚动时, CoordinatorLayout协调这个滚动事件, 并将这种行为发送给Bartool, 咨询其是否要一起滚动

例如自动让FloatingActionButton在被遮挡时上移(Snackbar虽然没有在布局文件里, 但在view的设置上成为了CoordinatorLayout的子控件了)

直接用`androidx.coordinatorlayout.widget.CoordinatorLayout `替换FrameLayout即可

```xml
<androidx.drawerlayout.widget.DrawerLayout ...>

    <androidx.coordinatorlayout.widget.CoordinatorLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent">
        <!--...-->

    </androidx.coordinatorlayout.widget.CoordinatorLayout >
    <!--...-->
</androidx.drawerlayout.widget.DrawerLayout>
```


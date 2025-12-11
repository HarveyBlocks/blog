# 生命周期

## 状态

### 与Activity关联

一般来说, Fragment的状态和其关联的Activity的状态是一致的

- 运行状态

  Fragment关联的Activity处于运行状态时, Fragment就处于运行状态

- 暂停状态

  Activity进入暂停状态时(并非完全不可见时), Fragment进入暂停状态

- 停止状态

  关联的Activity进入停止状态时,Fragment进入暂停状态

- 销毁状态

  Activity 被销毁时, Fragment进入销毁状态

### BackStack中的Fragment

但考虑到Fragment的有关==BackStack==的操作, 其进入 **停止状态**和**销毁状态** 的情况要脱离相关Activity, 另当别论

调用FragmentTransaction的remove(), replace()方法, **将Fragment从Activity中移除时**

- 如果在**事务提交之前**调用`addToBackStack()`方法, 也就是说Fragment进入了==BackStack==, 而没有直接废弃, 则进入停止状态

- 否则, 相关Activity不对Fragment进行关联, 同时BackStack也没有记录这个Fragmentd, 则该Fragment进入销毁状态



## 回调方法

- onAttach() 当Fragment和Activity建立关联
- onCreateView() 为Fragment创建视图（加载布局）时调用。
- onViewCreated() 创建视图完毕后调用
- onActivityCreated() 确保与Fragment相关联的Activity已经创建完毕时调用, **弃用**, 建议使用`onViewCreated()`
- onDestroyView() 当与Fragment关联的视图被移除时调用
- onDetach() 当Fragment和Activity解除关联

<img src="../../assetss/Day05-生命周期/fragment-view-lifecycle.png" alt="Fragment 生命周期状态，以及它们与 Fragment 的生命周期回调和 Fragment 的视图生命周期之间的关系" style="zoom:50%;" />

另一个图

<img src="../../assetss/Day05-生命周期/image-20250915143627269.png" alt="image-20250915143627269" style="zoom: 67%;" />

代码

```kotlin
class RightFragment : BaseFragment<FragmentRightBinding>(FragmentRightBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(logTag, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val onCreateView = super.onCreateView(inflater, container, savedInstanceState)
        Log.d(logTag, "onCreateView")
        return onCreateView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(logTag, "onViewCreated")
    }


    override fun onStart() {
        super.onStart()
        Log.d(logTag, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(logTag, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(logTag, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(logTag, "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(logTag, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(logTag, "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(logTag, "onDetach")
    }
}
```

第一次打开

![image-20250915150700476](../../assetss/Day05-生命周期/image-20250915150700476.png)

加入BackStack的Fragment被replace

![image-20250915150719020](../../assetss/Day05-生命周期/image-20250915150719020.png)

从BackStack中remove

![image-20250915151334000](../../assetss/Day05-生命周期/image-20250915151334000.png)

恢复Fragment

![image-20250915152927595](../../assetss/Day05-生命周期/image-20250915152927595.png)

## 被回收

进入停止状态的Fragment有可能在系统内存不足的时候被回收

在Fragment中也可以通过**`onSaveInstanceState()`**方法来保存数据

保存下来的数据在onCreate()、onCreateView()和onActivityCreated()这3个含有一个Bundle类型的savedInstanceState参数的方法中重新使用


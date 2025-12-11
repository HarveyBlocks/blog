# Lifecycles

在Activity外的程序中感知Activity的生命周期

## LifecycleObserver

钩子方法

```kotlin
/**
 * 钩子方法
 */
class HookLifecycleObserver : DefaultLifecycleObserver {
    val logger = logger()
    override fun onCreate(owner: LifecycleOwner) {
        logger.debug("onCreate")
    }

    override fun onStart(owner: LifecycleOwner) {
        logger.debug("onStart")
    }

    override fun onPause(owner: LifecycleOwner) {
        logger.debug("onPause")
    }
    // ...
}

```

对事件进行分类处理

```kotlin

class WhenLifecycleObserver : LifecycleEventObserver {
    val logger = logger()
    override fun onStateChanged(
        source: LifecycleOwner, event: Lifecycle.Event
    ) {
        when (event) {
            ON_CREATE -> logger.debug("Create")
            ON_START -> logger.debug("Start")
            ON_STOP -> logger.debug("Stop")
            ON_ANY -> logger.debug("Any")
            else -> {/*DO NOTHING*/
            }
        }
    }

}
```

有一个Any, 表示任意的生命周期改变都会触发

## addObserver

```kotlin
fun LifecycleOwner.registerObserver(lifecycleObserver: LifecycleObserver) {
    this.lifecycle.addObserver(lifecycleObserver)
}
```

Activity/Fragilement就是LifecycleOwner的一种实现

然后在Activity的onCreate里注册

```kotlin
class MainActivity : AppCompatActivity() { 
    ... 
    override fun onCreate(savedInstanceState: Bundle?) { 
        super.onCreate(savedInstanceState) 
        ... 
        registerObserver(WhenLifecycleObserver()) 
    } 
    ... 
} 
```

## State和Event

Event和状态之间的关系

![image-20250925145520614](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/Jetpack/Day14-Lifecycles/image-20250925145520614.png)

State.Created表示onCreate方法**已经执行**, 而Event.ON_CREATE表示OnCreate方法**还没有执行**

```kotlin
when (source.lifecycle.currentState) {
    Lifecycle.State.DESTROYED -> TODO()
    Lifecycle.State.INITIALIZED -> TODO()
    Lifecycle.State.CREATED -> TODO()
    Lifecycle.State.STARTED -> TODO()
    Lifecycle.State.RESUMED -> TODO()
}
```


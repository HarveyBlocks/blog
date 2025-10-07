# LiveData

响应式编程组件, 在数据发生变化的时候通知给观察者

LiveData 一般与 ViewModel 结合在一起使用



在ViewModule中的相关数据发生变化, 这种变化或许是异步的, 或许是被封装的, 如何通知外界?

如果ViewModel的内部开启了线程去执行一些耗时逻辑，那么立即去获取最新的数据，得到的可能还是之前的数据

如果把Activity的实例传给ViewModel，让ViewModel主动对Activity进行通知?

==不可以==! ViewModel的生命周期是长于Activity的，如果把Activity的实例传给ViewModel，就很有可能会因为Activity无法释放而造成内存泄漏。





## 基本用法

- getValue() 获取LiveData中包含的数据
- setValue() 在主线程中给LiveData设置数据
- postValue() 在非主线程中给LiveData设置数据
- observe(LifecycleOwner, Observer) 监听数据的变化

```kotlin
class MainViewModel(start: Int) : ViewModel() {
    private val _counter = MutableLiveData(start);
    
    // 这种写法是不好的, 没用啊, 强转一下不就好了
    val counter: LiveData<Int>
        get() = _counter

    fun increment() {
        val count = _counter.value ?: 0
        _counter.value = count + 1
    }
}
```

观察

```kotlin
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(
    ActivityMainBinding::inflate, MainViewModel::class, MainViewModel.Factory(0)
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.plusOneBtn.setOnClickListener {
            model.increment()
        }
        // 能把Lambda移到外面其实是KTX,
        // 本来Java的observe方法, 如果参数是(函数式接口, 函数式接口)的情况下, 要么全部kotlin写法, 要么都不用
        // viewModel.counter.observe(this, Observer { count -> ...}) 
        model.counter.observe(this) { count ->
            binding.infoText.text = count.toString()
        }
    }

}
```

当Activity处于不可见状态的时候（比如手机息屏，或者被其他的 Activity遮挡），此时LiveData中的数据发生了变化，是不会通知给观察者的

只有当Activity 重新恢复可见状态时，才会将数据通知给观察者

也就是说, 如果在Activity处于不可见状态的时候，LiveData发生了多次数据变化，当 Activity恢复可见状态时，只有最新的那份数据才会通知给观察者

### 不可变封装

```kotlin
private class ImmutableLiveData<T>(private val meta: LiveData<T>) : LiveData<T>() {
    override fun observe(
        owner: LifecycleOwner, observer: Observer<in T>
    ) = meta.observe(owner, observer)

    override fun observeForever(observer: Observer<in T>) = meta.observeForever(observer)

    override fun removeObserver(observer: Observer<in T>) = meta.removeObserver(observer)

    override fun removeObservers(owner: LifecycleOwner) = meta.removeObservers(owner)

    override fun getValue(): T? = meta.getValue()
    override fun isInitialized(): Boolean = meta.isInitialized()
    override fun hasObservers(): Boolean = meta.hasObservers()
    override fun hasActiveObservers(): Boolean = meta.hasActiveObservers()
}

fun <T> LiveData<T>.toImmutable(): LiveData<T> = ImmutableLiveData<T>(this)
```

使用方法

```kotlin
class MainViewModel(start: Int) : ViewModel() {
    private val _counter = MutableLiveData(start);
    val counter: LiveData<Int>
        get() = _counter.toImmutable()

    fun increment() {
        val count = _counter.value ?: 0
        _counter.value = count + 1
    }
}
```

缺点在于LiveData不是接口, 而是抽象类, 导致了LiveData不够抽象, 也有自己的字段, 导致有一部分内存浪费

## Transformations

`map()`和`switchMap()`方法, 都是能感知到一个原LiveData的变化, 而映射成另一个LiveData

这里感知的变化是浅层的

- map 在映射过程中, 从原value,转到新value
- switchMap,  在映射过程中, 从原value, 转到新LiveData\<T>, 用于应对拿到的数据本身就是LiveData\<T>的情况

```kotlin
class MainViewModel() : ViewModel() {
    private val userLiveData = MutableLiveData<User>()
    val userName1: LiveData<String> = userLiveData.map { user ->
        // userName1 不被观察就不会触发这里的变化
        // 满足上一条, 且 userLiveData 发生变化了会触发
        // 在映射中构造新的LiveData.value
        "${user.firstName} ${user.lastName}"
    }

    val userName2: LiveData<String> = userLiveData.switchMap() { user ->
        // userName1 不被观察就不会触发这里的变化
        // 满足上一条, 且 userLiveData 发生变化了会触发
        // 在映射中构造新的LiveData
        MutableLiveData("${user.firstName} ${user.lastName}")
    }
}
```

监听变化

```kotlin
model.userName2.observe(this) {
    logInfo("name2")
}
model.userName1.observe(this) {
    logInfo("name1")
}
```

刷新技巧: 将value赋值给value也会触发map

```kotlin
class MainViewModel() : ViewModel() {
    fun refresh() {
        if (userLiveData.value == null) {
            userLiveData.value = User("x", "y", 12)
            return
        }
        userLiveData.value = userLiveData.value // 也会触发map和switchMap
    }

    private val userLiveData = MutableLiveData<User>()
    val userName1: LiveData<String> = userLiveData.map { user ->
        "${user.firstName} ${user.lastName}"
    }

    val userName2: LiveData<String> = userLiveData.switchMap() { user ->
        MutableLiveData("${user.firstName} ${user.lastName}")
    }
}
```

## liveData() 协程优化

KTX 提供, 从阻塞到协程

```kotlin
class Person(val id: String)

fun searchPlaces(id: String): LiveData<Person> = liveData(Dispatchers.IO/*IO密集型*/) {
    // 拥有协程作用域, 可以调用suspend方法了
    val result = getById(id)/*可以suspend获取Person*/
    this.emit(result)
}

private suspend fun getById(id: String): Person = coroutineScope {
    delay(1000) // 比较耗时的IO任务, 可以被挂起, 节省CPU资源
    Person(id)
}
```

返回的`LiveData<Person>`可以进行switchMap, 进行一个对外透明的数据IO过程
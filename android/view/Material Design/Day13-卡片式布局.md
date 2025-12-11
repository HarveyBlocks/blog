# 卡片式布局

> MaterialCardView

用于实现卡片式布局效果的控件

本来是一个FrameLayout，额外提供了圆角和阴影等效果，看上去有立体的感觉

```xml
<com.google.android.material.card.MaterialCardView 
    android:layout_width="match_parent" 
    android:layout_height="wrap_content" 
    app:cardCornerRadius="4dp" 
    app:elevation="5dp"> 
    <TextView 
        android:id="@+id/infoText" 
        android:layout_width="match_parent" 
        android:layout_height="wrap_content"/> 
</com.google.android.material.card.MaterialCardView>
```

- `app:elevation="5dp"` 高度, 影响阴影的效果
- `app:cardCornerRadius="4dp" ` 圆角半径



## 引入有关依赖

```kotlin
implementation("com.github.bumptech.glide:glide:5.0.5")
```

glide是一个图片加载库

## 主界面

添加放卡片布局的RecyleView

```xml
<androidx.drawerlayout.widget.DrawerLayout...>

    <androidx.coordinatorlayout.widget.CoordinatorLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent">
        <!--Bar...-->

        <!--主界面内容-->
        <androidx.recyclerview.widget.RecyclerView
                android:id="@+id/fruitRecyclerView"
                android:layout_width="match_parent"
                android:layout_height="match_parent" />
        <!--设置在右下角...-->
    </androidx.coordinatorlayout.widget.CoordinatorLayout >
    <!--Navigation, 用户滑动菜单...-->
</androidx.drawerlayout.widget.DrawerLayout>
```

## 子项布局

```xml
<com.google.android.material.card.MaterialCardView
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="5dp"
        app:cardCornerRadius="4dp">

    <LinearLayout
            android:orientation="vertical"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

        <!--centerCrop 中心裁剪-->
        <ImageView
                android:id="@+id/fruitImage"
                android:layout_width="match_parent"
                android:layout_height="100dp"
                android:contentDescription="Fruit Image Loading..."
                android:scaleType="centerCrop" />

        <TextView
                android:id="@+id/fruitName"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center_horizontal"
                android:layout_margin="5dp"
                android:textSize="16sp" />
    </LinearLayout>

</com.google.android.material.card.MaterialCardView>
```

## Adapter

```kotlin
data class Fruit(val name: String, val imageId: Int)

typealias FruitViewHolder = ViewHolder<FruitItemLayoutBinding>

class FruitRecyclerAdapter(val context: Context, data: List<Fruit>) :
    BaseAdapter<Fruit, FruitItemLayoutBinding>(data, FruitItemLayoutBinding::inflate) {

    override fun onItemClicked(
        view: View, position: Int, item: Fruit, holder: ViewHolder<FruitItemLayoutBinding>
    ) {
		logInfo("$item clicked")
    }

    /**
     * 初始化ItemView
     */
    override fun onBindViewHolder(holder: FruitViewHolder, position: Int) {
        val item = data[position]
        // 不再直接加载
        // holder.itemBinding.fruitImage.setImageResource(item.imageId)
        // 使用Glide加载
        Glide.with(context).load(item.imageId).into(holder.itemBinding.fruitImage)
        holder.itemBinding.fruitName.text = item.name
    }
}
```

修改代码, 使用Glide加载

```kotlin
/**
 * 初始化ItemView
 */
override fun onBindViewHolder(holder: FruitViewHolder, position: Int) {
    val item = data[position]
    // 不再直接加载
    // holder.itemBinding.fruitImage.setImageResource(item.imageId)
    // 使用Glide加载
    Glide.with(context).load(item.imageId).into(holder.itemBinding.fruitImage)
    holder.itemBinding.fruitName.text = item.name
}
```

当图片像素非常高，如果不进行压缩就直接展示，很容易引起内存溢出

Glide在内部做了许多非常复杂的逻辑操作，包括图片压缩



## MainActivity

```kotlin
val fruits = listOf(
    Fruit("Apple", R.drawable.apple), Fruit(
        "Banana", R.drawable.banana
    ), Fruit("Orange", R.drawable.orange), Fruit(
        "Watermelon", R.drawable.watermelon
    ), Fruit("Pear", R.drawable.pear), Fruit(
        "Grape", R.drawable.grape
    ), Fruit("Pineapple", R.drawable.pineapple), Fruit(
        "Strawberry", R.drawable.strawberry
    ), Fruit("Cherry", R.drawable.cherry), Fruit(
        "Mango", R.drawable.mango
    )
)

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    val fruitList = ArrayList<Fruit>()


    private fun refreshFruitList() {
        fruitList.clear()
        val indexSet = 0 until fruits.size
        repeat(50) {
            val index = indexSet.random()
            fruitList.add(fruits[index])
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // .. 其他
        
        // 配置Fruit到RecycleView
        refreshFruitList()
        binding.fruitRecyclerView.run {
            val context = this@MainActivity
            adapter = FruitRecyclerAdapter(context, fruitList)
            layoutManager = GridLayoutManager(context, 2)
        }
    }

}

```

<img src="../../assetss/Day12-卡片式布局/image-20250923104845891.png" alt="image-20250923104845891" style="zoom: 67%;" />

Toolbar 被遮挡

RecyclerView和Toolbar都是放置在CoordinatorLayout中的, CoordinatorLayout的布局方式也是FrameLayout, 所有元素都默认放在左上角, 于是重叠

解决方法

1. 内部嵌套一个LinearLayout
2. AppBarLayout

## AppBarLayout

实际上是一个垂直方向的LinearLayout

在内部做了很多滚动事件的封装

应用了一些 Material Design的设计理念

解决Toolbar被遮挡, 需要

1. 将Toolbar 放入嵌套的AppBarLayout
2. 给RecycleView设置属性`app:layout_behavior=`

```xml
<androidx.drawerlayout.widget.DrawerLayout...>

    <androidx.coordinatorlayout.widget.CoordinatorLayout ...>

        <com.google.android.material.appbar.AppBarLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content">
            <!--Bar-->
            <androidx.appcompat.widget.Toolbar
                    ...
                    app:layout_scrollFlags="scroll|enterAlways|snap"
                                               />
        </com.google.android.material.appbar.AppBarLayout >
        <!--主界面内容-->
        <androidx.recyclerview.widget.RecyclerView
                android:id="@+id/fruitRecyclerView"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                app:layout_behavior="@string/appbar_scrolling_view_behavior"/>
        <!--设置在右下角...-->
    </androidx.coordinatorlayout.widget.CoordinatorLayout>
    <!--Navigation, 用户滑动菜单...-->
</androidx.drawerlayout.widget.DrawerLayout>
```

当有关内容滚动到`AppBarLayout`下方时，`AppBarLayout` 将以动画方式显示为提升或提升的状态。

要求在滚动同级上(即滚动内容)设置属性`app:layout_scrollFlags="scroll|enterAlways|snap"`

设置滚动发生时, 将出发的行为

- `scroll `当RecyclerView向上滚动的时候，Toolbar会跟着一起向上滚动并实现隐藏
- `enterAlways `当RecyclerView向下滚动的时候，Toolbar会跟着一起向下滚动并重新显示
- `snap `当Toolbar还没有完全隐藏或显示时，会根据当前滚动的距离自动选择隐藏or显示。(效果就是使Toolbar更有弹性)


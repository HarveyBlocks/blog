# MediaPlayer

## 播放音频



| Method            | description                                                  |      |
| ----------------- | ------------------------------------------------------------ | ---- |
| `setDataSource()` | 设置播放音频文件的位置                                       |      |
| `prepare()`       | 开始播放之前, 完成准备工作                                   |      |
| `start()`         | 开始/继续播放音频                                            |      |
| `pause()`         | 暂停播放音频                                                 |      |
| `reset()`         | 将`MediaPlayer`对象重置到刚创建的状态( 方法执行后进度归零, 音频暂停 ) |      |
| `seekTo()`        | 从指定位置开始播放音频                                       |      |
| `stop()`          | 停止播放音频. 调用后的`MediaPlayer`对象无法播放音频          |      |
| `release()`       | 释放`MediaPlayer`对象相关资源                                |      |
| `isPlaying()`     | 判断当前MediaPlayer是否在模仿音频                            |      |
| `getDuration()`   | 获取载入音频文件的时长                                       |      |

创建目录`src\main\assets`, 音频文件放入其目录下

![image-20250920152028202](../../assetss/Day10-MediaPlayer/image-20250920152028202.png)



### 布局

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

    <Button
            android:id="@+id/play"
            android:layout_marginTop="40dp"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Play" />

    <Button
            android:id="@+id/pause"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Pause" />

    <Button
            android:id="@+id/reset"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Reset" />
</LinearLayout>
```


### 代码逻辑

```kotlin
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMediaPlayer()
        binding.play.setOnClickListener({
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start() // 暂停播放
            }
        })
        binding.pause.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause() // 暂停播放
            }
        }
        binding.reset.setOnClickListener {
            mediaPlayer.reset() // 重置进度
            initMediaPlayer()
        }
    }

    private fun initMediaPlayer() {
        val assetManager = assets
        val fileDescriptor = assetManager.openFd("music.mp3")
        mediaPlayer.setDataSource(
            fileDescriptor.fileDescriptor, fileDescriptor.startOffset, fileDescriptor.length
        )
        mediaPlayer.prepare()
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying){
            mediaPlayer.pause() // 暂停播放
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}
```

## 播放视频

使用vedioView来对视频进行控制

| Method           | description                                                  |      |
| ---------------- | ------------------------------------------------------------ | ---- |
| `setVideoPath()` | 设置播放视频文件的位置                                       |      |
| `start()`        | 开始/继续播放视频                                            |      |
| `pause()`        | 暂停播放视频                                                 |      |
| `resume()`       | 将`VedioView`对象从头播放( 方法执行后进度归零, 视频状态不变 ) |      |
| `seekTo()`       | 从指定位置开始播放视频                                       |      |
| `isPlaying()`    | 判断当前VedioView是否在模仿视频                              |      |
| `getDuration()`  | 获取视频文件的时长                                           |      |
| `suspend()`      | 释放`VedioView`对象相关资源                                  |      |

在`res\raw`下存放视频文件

<img src="../../assetss/Day10-MediaPlayer/image-20250920161556537-1758356886299.png" alt="image-20250920161556537" style="zoom:50%;" />



### 布局

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

    <LinearLayout
            android:layout_marginTop="40dp"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal">

        <Button
                android:id="@+id/play"
                android:layout_width="0dp"
                android:layout_weight="1"
                android:layout_height="wrap_content"
                android:text="Play" />

        <Button
                android:id="@+id/pause"
                android:layout_width="0dp"
                android:layout_weight="1"
                android:layout_height="wrap_content"
                android:text="Pause" />
        <Button
                android:id="@+id/replay"
                android:layout_width="0dp"
                android:layout_weight="1"
                android:layout_height="wrap_content"
                android:text="Replay" />
    </LinearLayout>

    <VideoView
            android:id="@+id/videoView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />
</LinearLayout>
```



### 代码逻辑

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initVideoView()
    binding.play.setOnClickListener({
        if (!binding.videoView.isPlaying) {
            binding.videoView.start() // 暂停播放
        }
    })
    binding.pause.setOnClickListener {
        if (binding.videoView.isPlaying) {
            binding.videoView.pause() // 暂停播放
        }
    }
    binding.replay.setOnClickListener {
        binding.videoView.resume() // 重置进度
    }
}

private fun initVideoView() {
    val uri = "android.resource://$packageName/${R.raw.video}".toUri()
    binding.videoView.setVideoURI(uri)
}

override fun onPause() {
    super.onPause()
    if (binding.videoView.isPlaying) {
        binding.videoView.pause() // 暂停播放
    }
}

override fun onDestroy() {
    binding.videoView.suspend()
    super.onDestroy()
}
```


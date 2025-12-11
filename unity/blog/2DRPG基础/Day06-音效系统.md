# 音效系统

## 音乐资源包

音效素材: [FREE Casual Game SFX Pack](https://assetstore.unity.com/packages/audio/sound-fx/free-casual-game-sfx-pack-54116)

BGM素材: [Free Casual Music Pack](https://assetstore.unity.com/packages/audio/music/free-casual-music-pack-242591)



## 使用音效

1.   创建空对象`Audio Manager`

2.   Inspector->Add Component->Audio

     <img src="../../assets/Day06-%E9%9F%B3%E6%95%88%E7%B3%BB%E7%BB%9F/image-20241029161057722.png" alt="image-20241029161057722" style="zoom:50%;" />

     -   Audio Listener 音乐监听

         MainCamera中默认含有Audio Listener

         <img src="../../assets/Day06-%E9%9F%B3%E6%95%88%E7%B3%BB%E7%BB%9F/image-20241029161212181.png" alt="image-20241029161212181" style="zoom:67%;" />

     -   Audio Source 音乐播放

3.   Audio Manager中添加Audio Source

     <img src="../../assets/Day06-%E9%9F%B3%E6%95%88%E7%B3%BB%E7%BB%9F/image-20241029161706706.png" alt="image-20241029161706706" style="zoom:50%;" />

     -   Audio Clip 音效切片, 就是BGM素材直接导入

     -   Play On Awake 在游戏一开始就播放
     -   Loop 循环
     -   Volume 音量
     -   Reverb Zone Mix 左端是2D, 右端的3D, 对音乐做调整以适应不同场景

4.   制作BGM, Play On Awake 打开

5.   制作音效, 再次添加Audio Source组件

     -   Play On Awake, false-关闭

6.   脚本控制, 在什么时候, 将什么音效素材放到哪个Audio Source的Audio Clip里去

7.   AudioManager对象挂载该脚本

8.   编写`AudioManager`脚本代码

     1.   调用`UnityEngineAudio`命名空间

     2.   导入参数

          ```csharp
          public class AudioManager : MonoBehaviour {
              #region 参数
          
              /// Special effects, 音效
              /// 会发生循环的音效, 例如喘气
              [CanBeNull]
              public AudioSource loopableFxAudio;
          
              /// 不会发生循环的音效, 例如跳跃
              [CanBeNull]
              public AudioSource unLoopableFxAudio;
          
              [CanBeNull]
              public AudioSource bgmAudio;
          
              #endregion
          }
          ```

9.   制作攻击音效, 在攻击的对象上挂载要播放的音效, 然后攻击对象生效的时候, 通知`AudioManager`播放音效

     1.   脚本`PlayAudioEvent`

          ```csharp
          [CreateAssetMenu(fileName = "Event/PlayAudioEvent")]
          public class PlayAudioEvent : ScriptableObject {
              public UnityAction<AudioClip> PlayAudio;
          
              public void Execute(AudioClip clip) {
                  PlayAudio?.Invoke(clip);
              }
          }
          ```

     2.   创建对应assets

          <img src="../../assets/Day06-%E9%9F%B3%E6%95%88%E7%B3%BB%E7%BB%9F/image-20241029164344385.png" alt="image-20241029164344385" style="zoom:67%;" />

     3.   创建音乐广播脚本

          ```csharp
          public class AudioDefinition : MonoBehaviour {
              public PlayAudioEvent audioEvent;
              public AudioClip audioClip;
              /// 对象启动时播放
              public bool playOnEnable = true;
          
              private void OnEnable() {
                  // 每次对象生效时播放音乐
                  if (playOnEnable) {
                      PlayAudioClip();
                  }
              }
          
              private void PlayAudioClip() {
                  audioEvent.Execute(audioClip);
              }
          }
          ```

     4.   将音乐广播脚本挂载到各个Attack对象上, 并设置参数, 三段攻击使用素材是47-47-46, 大概是*呼\~呼\~划\~*的音效

          横劈是*呼~*, 竖劈是*划~*

          <img src="../../assets/Day06-%E9%9F%B3%E6%95%88%E7%B3%BB%E7%BB%9F/image-20241029170724133.png" alt="image-20241029170724133" style="zoom:67%;" />

          Play设置为true

     5.   `AudioManager`组件监听PlayAudioEventSO

          ```csharp
          public class AudioManager : MonoBehaviour {
              #region 参数
          
              /// Special effects, 音效
              /// 会发生循环的音效, 例如喘气
              [CanBeNull]
              public AudioSource loopableFxAudio;
          
              /// 不会发生循环的音效, 例如跳跃
              [CanBeNull]
              public AudioSource unLoopableFxAudio;
          
              [CanBeNull]
              public AudioSource bgmAudio;
          
              [CanBeNull]
              public PlayAudioEvent loopableFxPlayEvent;
          
              [CanBeNull]
              public PlayAudioEvent unLoopableFxPlayEvent;
          
              [CanBeNull]
              public PlayAudioEvent bgmPlayEvent;
          
              #endregion
          
              #region 事件函数
          
              private void OnEnable() {
                  RegisterEvent(loopableFxPlayEvent, PlayLoopableFxAudio);
                  RegisterEvent(unLoopableFxPlayEvent, PlayUnLoopableFxAudio);
                  RegisterEvent(bgmPlayEvent, PlayBgmAudio);
              }
          
              private void OnDisable() {
                  UnregisterEvent(loopableFxPlayEvent, PlayLoopableFxAudio);
                  UnregisterEvent(unLoopableFxPlayEvent, PlayUnLoopableFxAudio);
                  UnregisterEvent(bgmPlayEvent, PlayBgmAudio);
              }
          
              private static void RegisterEvent(PlayAudioEvent playAudioEvent, UnityAction<AudioClip> action) {
                  if (playAudioEvent != null) {
                      playAudioEvent.PlayAudio += action;
                  }
              }
          
              private static void UnregisterEvent(PlayAudioEvent playAudioEvent, UnityAction<AudioClip> action) {
                  if (playAudioEvent != null) {
                      playAudioEvent.PlayAudio -= action;
                  }
              }
          
              #endregion
          
              #region 播放事件执行
          
              private void PlayLoopableFxAudio(AudioClip clip) => PlayAudio(loopableFxAudio, clip);
          
              private void PlayUnLoopableFxAudio(AudioClip clip) => PlayAudio(unLoopableFxAudio, clip);
              private void PlayBgmAudio(AudioClip clip) => PlayAudio(bgmAudio, clip);
          
              private static void PlayAudio(AudioSource audio, AudioClip clip) {
                  if (audio == null) {
                      return;
                  }
          
                  audio.clip = clip;
                  audio.Play();
              }
          
              #endregion
          }
          ```

     6.   为AudioManager调整参数

          <img src="../../assets/Day06-%E9%9F%B3%E6%95%88%E7%B3%BB%E7%BB%9F/image-20241029170833047.png" alt="image-20241029170833047" style="zoom:67%;" />

          BGM不需要切换, 所以OnAwake保持, 就会在启动时播放

10.   场景切换时转换BGM播放

      1.   创建空对象BGM
      2.   BGM中挂载AudioDefinition组件
      3.   创建BGM播放事件, 由于场景切换所有Active的对象都会进入Enable状态, BGM播放事件也会进行一次广播
      4.   AudioManager中添加BGM事件



## 混音

将不同的音效输入到不同的轨道进行混音的输出



1.   Window->Audio->AudioMixer 混音台窗口

2.   创建AudioMixer文件

     <img src="../../assets/Day06-%E9%9F%B3%E6%95%88%E7%B3%BB%E7%BB%9F/image-20241029172644496.png" alt="image-20241029172644496" style="zoom:67%;" />

     <img src="../../assets/Day06-%E9%9F%B3%E6%95%88%E7%B3%BB%E7%BB%9F/image-20241029172740459.png" alt="image-20241029172740459" style="zoom:67%;" />

     <img src="../../assets/Day06-%E9%9F%B3%E6%95%88%E7%B3%BB%E7%BB%9F/image-20241029172950543.png" alt="image-20241029172950543" style="zoom:67%;" />

3.   选中Master, 点击+, 添加子轨道(用于人声, 环境音, 白噪音...)

     <img src="../../assets/Day06-%E9%9F%B3%E6%95%88%E7%B3%BB%E7%BB%9F/image-20241029173115455.png" alt="image-20241029173115455" style="zoom:50%;" />

     -   主音轨调节整个游戏声音
     -   子音轨各自调节声音

4.   选择AudioManager对象->Inspector->AudioSource->Output

     <img src="../../assets/Day06-%E9%9F%B3%E6%95%88%E7%B3%BB%E7%BB%9F/image-20241029173213291.png" alt="image-20241029173213291" style="zoom:50%;" />

     选中对应子轨道

     <img src="../../assets/Day06-%E9%9F%B3%E6%95%88%E7%B3%BB%E7%BB%9F/image-20241029173256722.png" alt="image-20241029173256722" style="zoom:50%;" />


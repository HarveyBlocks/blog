# 场景

## 场景叠加

在Hierarchy窗口显示多个场景

![image-20241030201312365](../../assetss/Day07-%E5%9C%BA%E6%99%AF/image-20241030201312365.png)

通过将场景文件拖拽到Hierarchy窗口而不是双击场景文件

拖动过去后场景发生了叠加, 将场景的Tilemap设置active为false

## 持久化场景

场景切换过程中, 只有地图在变化, 摄像机的边界在变化, 人物是没有变的, 敌人也是没有变的

人物, 敌人,摄像机和场景并没有强绑定, 那就创建有一个持久化场景, 将这些和场景没有强绑定的对象放入此场景

<img src="../../assets/Day07-%E5%9C%BA%E6%99%AF/image-20241030202719663.png" alt="image-20241030202719663" style="zoom:50%;" />



让后以Persisten为主要场景

<img src="../../assets/Day07-%E5%9C%BA%E6%99%AF/image-20241030202841193.png" alt="image-20241030202841193" style="zoom:50%;" />

地图切换就是将不同的场景加载到和Persisten场景同级

## 加载和卸载场景

Hierarchy->选中场景->右键选择UnloadSence

<img src="../../assets/Day07-%E5%9C%BA%E6%99%AF/image-20241031103013614.png" alt="image-20241031103013614" style="zoom:50%;" />

<img src="../../assets/Day07-%E5%9C%BA%E6%99%AF/image-20241031103048416.png" alt="image-20241031103048416" style="zoom:80%;" />



此时添加新的对象时, 优先添加到Persistent, 选中场景, 选择set Active sence, 即可将默认添加位置改变

<img src="../../assets/Day07-%E5%9C%BA%E6%99%AF/image-20241031103250374.png" alt="image-20241031103250374" style="zoom:80%;" />





## 场景转换

### 场景转换

1.   安装插件`Addressable` Windows->pacage manager-> Unity Register

2.   Window->Asset Manager->Adressables ->Groups

3.   create adressable settings

4.   多出文件夹 Project->Assets->AddressableAssetsData

5.   重命名Addressables

     <img src="../../assets/Day07-%E5%9C%BA%E6%99%AF/image-20241102221350816.png" alt="image-20241102221350816" style="zoom:50%;" />

6.   将project的sence在inspector中勾选Addressable, 即将场景加载到Addressable中

     <img src="../../assets/Day07-%E5%9C%BA%E6%99%AF/image-20241102221335962.png" alt="image-20241102221335962" style="zoom:50%;" />

     <img src="../../assets/Day07-%E5%9C%BA%E6%99%AF/image-20241102221455905.png" alt="image-20241102221455905" style="zoom:50%;" />

7.   一个场景在Addressable中勾选, 就会自动取消其在Build Setting中的勾选

     ![image-20241102221215053](../../assetss/Day07-%E5%9C%BA%E6%99%AF/image-20241102221215053.png)

     因为Build打包是用全部文件打包的方式, Addressable采用部分打包, 部分寻址的方式

8.   全选, Simplify Addressable names

     <img src="../../assets/Day07-%E5%9C%BA%E6%99%AF/image-20241102221536246.png" alt="image-20241102221536246" style="zoom:50%;" />

9.   Addressable Groups->New -> Packed Asset->创建一个新的要打包的素材

10.   将**预制体**拖拽到(和勾选效果一样)PackedAddset, Addressable插件会分析预制体, 并且只打包一份, 防止重复打包

11.   制作切换场景事件

      ```csharp
      using UnityEngine;
      
      namespace SO {
          [CreateAssetMenu(menuName = "Scene/SceneReference")]
          public class SceneReference : ScriptableObject {
              public AddressableAssets.AssetReference reference;
          }
      }
      ```

12.   在持久化场景处添加GameObject `SceneLoader`

13.    `SceneLoader`加载第一个场景

      ```csharp
      public class SceneLoadManager : MonoBehaviour {
          public SceneReference init;
      
          public void Awake() {
              // 异步加载场景
              Addressables.LoadSceneAsync(init.reference, LoadSceneMode.Additive);
          }
      }
      ```

      ```csharp
      public enum LoadSceneMode
      {
        /// Closes all current loaded Scenes and loads a Scene.
        Single,
        /// Adds the Scene to the current loaded Scenes.
        Additive,
      }
      ```

      持久层不可删

14.   传送门被交互后发送切换场景事件

      ```csharp
      public class TeleportInteract : InteractiveScene {
          public SceneReference to;
          public SceneChangeEvent sceneChangeEvent;
      
          public override void Interact(GameObject other) {
              sceneChangeEvent.Execute(to);
          }
      }
      ```

15.   SceneLoader监听切换场景事件并执行

      ```csharp
      public class SceneLoadManager : MonoBehaviour {
          private SceneReference _current;
          public SceneReference init;
          public SceneChangeEvent sceneChangeEvent;
      
          public void Awake() {
              _current = init;
              LoadScene(init);
          }
      
          private void OnEnable() {
              sceneChangeEvent.Action += ChangeScene;
          }
      
          private void OnDisable() {
              sceneChangeEvent.Action -= ChangeScene;
          }
      
          private void ChangeScene(SceneReference targetScene) {
              UnLoadScene(_current);
              LoadScene(targetScene);
              _current = targetScene;
          }
      
          private static void LoadScene(SceneReference sr) {
              // sr.reference.LoadSceneAsync(LoadSceneMode.Additive, true);
              // 写入对象的
              // Addressables.LoadSceneAsync(sr, LoadSceneMode.Additive,true);
              // 不写入对象的, 也就是sr对象没有被加载, 到时候就不能被删除
              sr.reference.LoadSceneAsync(LoadSceneMode.Additive, true);
          }
      
          private static void UnLoadScene(SceneReference sr) {
      
              sr.reference.UnLoadScene();
          }
      }
      ```

16.   异步优化

      ```csharp
      private void ChangeScene(SceneReference targetScene) {
          StartCoroutine(Inner()); // 开启协程
          return;
      
          IEnumerator Inner() {
              // 执行渐入渐出
              yield return new WaitForSeconds(1);
              if (_current) {
                  yield return UnLoadScene(_current);
              }
      
              LoadScene(targetScene);
              _current = targetScene;
          }
      }
      ```

### 场景加载后逻辑

如何得知场景已经加载完毕

```csharp
var asyncHandle = LoadScene(targetScene);
asyncHandle.Completed += LoadScenePost;
```

```csharp
private void LoadScenePost(AsyncOperationHandle<SceneInstance> aoh) {
    Debug.Log(aoh.Result.ToString());
}
```



### 场景渐入渐出

1.   Create->Ui->Cavens创建UI画布对象FadeCavens 

2.   FadeCavens->Inspector->Cavas->SortOrder 优先级高的遮挡优先级低的UI, 优先级高的, 优先级值大

3.   FadeCavens下(继承优先级)创建Ui->Image, FadeCurtain 消退 幕布

4.   FadeCurtain->Inspector->Rect Transform->Anchor Presets ->Ctrl+Alt+填充整个画面(右下角)

5.   FadeCurtain->Inspector->Image->Color->A (alpha), 调整透明度

6.   FadeCurtain->Inspector->Image->RayCast Target 设置成false, 防止被射线检测

7.   安装插件 `DOTween (HOTween v2)`,用于将参数进行缓慢连续的转换

8.   下载, 导入插件

9.   弹出窗口, 点击`Open DOTween Unlity Panel`

10.   `Set up DoTween`->`Apply` 完成设置

11.   调用代码实现渐变

      ```csharp
      // DG.Tweening [namespace]
      // .DOTweenModuleUI [static class]
      // .DOColor(_image, targetColor,fadeDuration); [Method]
      // 语法糖,与上等价, 可见源码
      _image.DOColor(targetColor,fadeDuration);
      ```

12.   实现脚本

      ```csharp
      public class FadeCurtain : MonoBehaviour {
          public float fadeDuration = 0.5f;
          public VoidInterceptor fadeInterceptor;
          private Image _image;
      
          private void Awake() {
              _image = GetComponent<Image>();
          }
      
          private void OnEnable() {
              fadeInterceptor.PreAction += FadeIn;
              fadeInterceptor.PostAction += FadeOut;
          }
      
      
          private void OnDisable() {
              fadeInterceptor.PreAction -= FadeIn;
              fadeInterceptor.PostAction -= FadeOut;
          }
      
          private void FadeIn() {
              _image.DOBlendableColor(Color.white, fadeDuration);
          }
      
          private void FadeOut() {
              _image.DOBlendableColor(Color.clear, fadeDuration);
          }
      }
      ```

13.   在SceneLoadManager导入SO, 并用SO的API调用代码



## 灯光效果

Hierarchy->右键->Light->Spot Light 2D 点光源

<img src="../../assets/Day07-%E5%9C%BA%E6%99%AF/image-20241111142620531.png" alt="image-20241111142620531" style="zoom:50%;" />

-   Color 光的颜色
-   Intensity 光强
-   Radius 光照范围
-   TargetSorting Layers 照亮的图层

## 后处理效果

URP

通用渲染管线

Post Prosessing

好看的画面效果

Hierarchy->右键->Volume->

-   Global Volume 全局响应 影响当前场景的画面
-   Box Volume 局域响应, 摄像机进入区域后收到画面特效影响
-   Shpere Volume

### Global Volume

<img src="../../assets/Day07-%E5%9C%BA%E6%99%AF/image-20241111143751412.png" alt="image-20241111143751412" style="zoom:50%;" />

1.   profile->New 创建profile设置
2.   


# UGUI

## 画布

###创建

1.   Hierarchy-> ＋->UI->Canvas 创建画布

     ![image-20241026135819895](../../assets/Day05-UGUI/image-20241026135819895.png)

     自动创建EventSystem

     EventSystem , 保证屏幕点击, 滑动条被识别

2.   替换EventSystem的InputSystem

     EventSystem->Inspector->Stand alone input module->Replace with InputSystem UI Input Module

     <img src="../../assets/Day05-UGUI/image-20241026140055642.png" alt="image-20241026140055642" style="zoom:67%;" />

     替换

     <img src="../../assets/Day05-UGUI/image-20241026140115216.png" alt="image-20241026140115216" style="zoom: 33%;" />

     

3.   EventSystem->Inspector->InputSystem UI Input Modul->Action Asset 默认的Actions修改成自己创建的PlayerInputController

4.   打开PlayerInputController, 转换至UI, 即可设置按键

     <img src="../../assets/Day05-UGUI/image-20241026143135871.png" alt="image-20241026143135871" style="zoom:50%;" />

### 设置参数

1.   选中cavens

     <img src="../../assets/Day05-UGUI/image-20241026143528992.png" alt="image-20241026143528992" style="zoom:30%;" />

     左下角的像素点是场景

2.   Inspector->Canvas->RenderMode->Screen Space-Overlay覆盖整个屏幕, 根据设备的不同而调整, 总是完成覆盖

     <img src="../../assets/Day05-UGUI/image-20241026143724316.png" alt="image-20241026143724316" style="zoom:50%;" />

3.   Inspector->Canvas Scale->UI Scale Mode->Scale With Screen Size根据实际游戏画面分辨率调整布局

     <img src="../../assets/Day05-UGUI/image-20241026143931685.png" alt="image-20241026143931685" style="zoom:50%;" />

     <img src="../../assets/Day05-UGUI/image-20241026144248084.png" alt="image-20241026144248084" style="zoom:50%;" />

4.   Inspector->Canvas Scale->Reference Resolution调整画面比例

     Game->查看比例

     <img src="../../assets/Day05-UGUI/image-20241026144335541.png" alt="image-20241026144335541" style="zoom:50%;" />

     <img src="../../assets/Day05-UGUI/image-20241026144408635.png" alt="image-20241026144408635" style="zoom:50%;" />

     

5.   Screen Math Mode按比例优先排列模式, 同时符合高度或宽度/完全拉伸/缩小, 选择Match Width Or Height 不变

     ![image-20241026144547019](../../assets/Day05-UGUI/image-20241026144547019.png)

     调整宽高优先级, 0表示宽度不变拉高度, 1表示高度不变拉宽度, 设置成0.5

6.   Reference Pixels Per Unit, 和素材的像素比一样, 本项目中是16

     ![image-20241026144804655](../../assets/Day05-UGUI/image-20241026144804655.png)

### 使用

1.   在Canvas下创建UI->Image

     Scene下: 

     <img src="../../assets/Day05-UGUI/image-20241026145147622.png" alt="image-20241026145147622" style="zoom:50%;" />

     Game下:

     <img src="../../assets/Day05-UGUI/image-20241026145219933.png" alt="image-20241026145219933" style="zoom:50%;" />

2.   Image->Inspector->Rect Transform 按照设置锚点的位置, 确定跟随的点

     <img src="../../assets/Day05-UGUI/image-20241026145554289.png" alt="image-20241026145554289" style="zoom:50%;" />

     点击预制锚点Anchor Presets

     <img src="../../assets/Day05-UGUI/image-20241026145653456.png" alt="image-20241026145653456" style="zoom:50%;" />

     <img src="../../assets/Day05-UGUI/image-20241026145800086.png" alt="image-20241026145800086" style="zoom:50%;" />

     代表当前Image的位置不再是(0,0), 即使他位于画面的中心, 他的坐标也要依据新锚点重新计算

     <img src="../../assets/Day05-UGUI/image-20241026145950550.png" alt="image-20241026145950550" style="zoom:50%;" />

     也就是说, 即使画面比例变化, Image的位置也不会发生变化, 也是依据左上角的锚点计算的

3.   点击预制锚点Anchor Presets, 长按Alt键, 拉拽对象到特定位置, 再长按Shift键, 将对象定位到目标位置

4.   Image->Inspector->Image->RayCast Target 设置成false, 防止被射线检测



## 人物状态制作

### 血量条图像

1.   素材在HUD(Heads up Display 抬头显示)

     <img src="../../assets/Day05-UGUI/image-20241026150854334.png" alt="image-20241026150854334" style="zoom:50%;" />

2.   自动切割

     <img src="../../assets/Day05-UGUI/image-20241026151030903.png" alt="image-20241026151030903" style="zoom:50%;" />

     不好使, 得自己手动调

3.   Image选择切好的切片

     <img src="../../assets/Day05-UGUI/image-20241026151949887.png" alt="image-20241026151949887" style="zoom:50%;" />

4.   SetNativeSize调整比例

     <img src="../../assets/Day05-UGUI/image-20241026152058798.png" alt="image-20241026152058798" style="zoom:50%;" />

5.   在Sence手动调整, 在Game查看

     <img src="../../assets/Day05-UGUI/image-20241026152611169.png" alt="image-20241026152611169" style="zoom:50%;" />

6.   将Image重命名为BloodEmpty, 再复制BloodEmpty对象, 将源换成绿色血条素材

     ![image-20241026152735243](../../assets/Day05-UGUI/image-20241026152735243.png)

     绿血条完全覆盖空血条

     ![image-20241026152913107](../../assets/Day05-UGUI/image-20241026152913107.png)

     交换对象位置, 下面的覆盖上面的

     <img src="../../assets/Day05-UGUI/image-20241026152933947.png" alt="image-20241026152933947" style="zoom:50%;" />

7.   Green->Inspector->Image->ImageType->Filled 填充

     <img src="../../assets/Day05-UGUI/image-20241026153321472.png" alt="image-20241026153321472" style="zoom:67%;" />

     FilledMode->Horizontal水平填充

     <img src="../../assets/Day05-UGUI/image-20241026153358774.png" alt="image-20241026153358774" style="zoom:50%;" />

     调整Fill Amount 模拟血量变化

     <img src="../../assets/Day05-UGUI/image-20241026153503732.png" alt="image-20241026153503732" style="zoom:50%;" />

     ![image-20241026153514710](../../assets/Day05-UGUI/image-20241026153514710.png)

8.   渐变效果, 扣血了, 绿色快速移动, 红色缓慢移动

     ![image-20241026154428127](../../assets/Day05-UGUI/image-20241026154428127.png)

9.   将几个血条移到一个父类下, 方便一起移动



### 人物头像

1.   同血条, 创建FaceFrame

     <img src="../../assets/Day05-UGUI/image-20241026155938121.png" alt="image-20241026155938121" style="zoom:50%;" />

2.   切割出人头

     1.   FaceFrame下创建Image, Cut

     2.   Cut下创建Image, Face

     3.   Face的Source随便找个人物的动作素材, 调整大小

          <img src="../../assets/Day05-UGUI/image-20241026160428383.png" alt="image-20241026160428383" style="zoom:50%;" />

          头在Cut内

     4.   选择Cut, 添加组件Mask

          <img src="../../assets/Day05-UGUI/image-20241026160537461.png" alt="image-20241026160537461" style="zoom:50%;" />

          马上就有了

     5.   ShowMaskGraphic 为false, 使背景透明

          <img src="../../assets/Day05-UGUI/image-20241026160644952.png" alt="image-20241026160644952" style="zoom:50%;" />

     6.   调整位置

          <img src="../../assets/Day05-UGUI/image-20241026160724447.png" alt="image-20241026160724447" style="zoom:50%;" />





### 减血逻辑

血量控制组件

```csharp
public class PlayerStatusBar : MonoBehaviour {
    public CharacterFeature characterFeature;
    public Image healthBar;
    public Image healthDelayBar;

    private void Update() {
        HealthChange();
    }
    
    private void HealthChange(){}
}
```



<img src="../../assets/Day05-UGUI/image-20241027202914028.png" alt="image-20241027202914028" style="zoom:50%;" />



```csharp
private void HealthChange() {
    healthBar.fillAmount = characterFeature.HealthRatio;
    if (healthDelayBar.fillAmount > healthBar.fillAmount) {
        healthDelayBar.fillAmount -= Time.deltaTime * delaySpeed;
    }

    if (healthDelayBar.fillAmount < healthBar.fillAmount) {
        healthDelayBar.fillAmount = healthBar.fillAmount;
    }
}
```

## ScriptableObject

上述代码缺点: 场景切换时会造成找不到CharacterFeature引用(空指针)

持久化文件

利用持久化存储的文件(ScriptableObject)来管理CharacterFeature



1.   修改原有代码

     ```csharp
     public class PlayerStatusBar : MonoBehaviour {
         public Image healthBar;
         public Image healthDelayBar;
         public float delaySpeed = 1;
     
         private void Update() {
             if (NeedDelayHealth()) {
                 healthDelayBar.fillAmount -= Time.deltaTime * delaySpeed;
             }
     
             if (!NeedDelayHealth()) {
                 // 减过头了
                 healthDelayBar.fillAmount = healthBar.fillAmount;
             }
         }
     	/// 需要被注册到注册中心, 监听血量变化然后进行修正
         public void HealthChange(float healthRatio) {
             healthBar.fillAmount = healthRatio;
         }
     
         private bool NeedDelayHealth() {
             return healthDelayBar.fillAmount > healthBar.fillAmount;
         }
     }
     ```

2.   创建脚本继承`ScriptableObject`, 命名以`Event`结尾

     <img src="../../assets/Day05-UGUI/image-20241027221658404.png" alt="image-20241027221658404" style="zoom:50%;" />

3.   为这个类添加描述`[CrateAssetMenu(fileName="Event/CharacterFeatureEvent")]`, 可以创建`.asset`结尾的SO文件

     ```csharp
     [CreateAssetMenu(fileName = "Event/CharacterFeatureEvent")]
     public class CharacterFeatureEvent : ScriptableObject {
     }
     ```

     <img src="../../assets/Day05-UGUI/image-20241027221802674.png" alt="image-20241027221802674" style="zoom:67%;" />

4.   之前使用`UnityEvent<T>`进行事件监听, 注册的方式是以参数的形式

     <img src="../../assets/Day05-UGUI/image-20241027215837589.png" alt="image-20241027215837589" style="zoom:50%;" />

     现使用`UnityAction<T>`, 以创建注册中心的形式

     ```csharp
     [CreateAssetMenu(fileName = "Event/CharacterFeatureEvent")]
     public class CharacterFeatureEvent : ScriptableObject {
         private UnityAction<CharacterFeature> _action;
     
         public void Execute(CharacterFeature characterFeature) {
             _action?.Invoke(characterFeature);
         }
     }
     ```

5.   为该对象创建.assets

     <img src="../../assets/Day05-UGUI/image-20241027221909582.png" alt="image-20241027221909582" style="zoom:67%;" />

6.   在`CharacterFeature`创建`UnityEvent`, 监听血量变化, 将`.asset`文件注册到这个UnityEvent

     ```csharp
     public class CharacterFeature : MonoBehaviour {
         #region 属性
         private int Health {
             get => _health;
             set {
                 if (value > healthCap) {
                     _health = healthCap;
                 } else if (value < 0) {
                     _health = 0;
                 } else {
                     _health = value;
                 }
     
                 onHealthChanged.Invoke(this);
             }
         }
         #endregion
     
         #region 事件
         public UnityEvent<CharacterFeature> onHealthChanged;
         #endregion
     }
     ```

7.   将`Assets`注册到CharacterFeature的OnHealthChange事件

     <img src="../../assets/Day05-UGUI/image-20241027222023258.png" alt="image-20241027222023258" style="zoom:67%;" />

8.   创建注册中心, 傻逼用`Manager`作为类名结尾, 创建GameObject, UIManager

     <img src="../../assets/Day05-UGUI/image-20241027222214072.png" alt="image-20241027222214072" style="zoom:67%;" />

     <img src="../../assets/Day05-UGUI/image-20241027222156382.png" alt="image-20241027222156382" style="zoom:67%;" />

9.   将同样的`.asset`文件注册到该Manager的`CharacterFeatureEvent`类字段

     ```csharp
     public class CharacterFeatureManager : MonoBehaviour {
         public CharacterFeatureEvent characterFeatureEvent;
     }
     ```

     <img src="../../assets/Day05-UGUI/image-20241027223027197.png" alt="image-20241027223027197" style="zoom:67%;" />

10.   `Manager`的`OnEnable`函数中注册事件`event.UnityAction字段+=函数`

      `OnDisable`函数中注销事件`event.UnityAction字段-=函数`

      ```csharp
      public class CharacterFeatureManager : MonoBehaviour {
          public CharacterFeatureEvent characterFeatureEvent;
      
          public void OnEnable() {
              characterFeatureEvent.HealthChange += OnHealthChange;
          }
      
          public void OnDisable() {
              characterFeatureEvent.HealthChange -= OnHealthChange;
          }
      
          private void OnHealthChange(CharacterFeature characterFeature) { }
      }
      ```

11.   将StatusBar的组件传入Manager, 在Manager中被注册的函数中调用StatusBar的函数, 完成逻辑

      ```csharp
      public class CharacterFeatureManager : MonoBehaviour {
          public CharacterFeatureEvent characterFeatureEvent;
          public PlayerStatusBar playerStatusBar;
      
          public void OnEnable(){/*....*/}
      
          public void OnDisable() {/*....*/}
      
          private void OnHealthChange(CharacterFeature characterFeature) {
              playerStatusBar.HealthChange(characterFeature.HealthRatio);
          }
      }
      ```

      <img src="../../assets/Day05-UGUI/image-20241027223044453.png" alt="image-20241027223044453" style="zoom:67%;" />
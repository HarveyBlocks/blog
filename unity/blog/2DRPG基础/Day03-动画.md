# 动画

## 查看动画素材

<img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/guide.png" alt="guide" style="zoom:67%;" />



## 动画组件Animator

>   Animator 动画控制器

### 创建待机动画



1.   Inspector->Add Component->Animator

2.   Project->Create->AnimatorController

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019154501957.png" alt="image-20241019154501957" style="zoom:67%;" />

     -   Animation是动画本身
     -   Animator Controller 是动画制作器

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019155418100.png" alt="image-20241019155418100" style="zoom:50%;" />

3.   将AnimatorController加入到Animator组件

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019154428681.png" alt="image-20241019154428681" style="zoom:50%;" />

4.   选中人物->左上角Window->Animation->Animator

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019154719051.png" alt="image-20241019154719051" style="zoom:67%;" />

     -   Any State 任何状态都可以执行的动画
     -   Entry 动画的进入
     -   Exit 动画的退出

5.   选中人物->左上角Window->Animation->Animation

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019155527188.png" alt="image-20241019155527188" style="zoom:50%;" />

6.   Create->选择文件夹

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019155638741.png" alt="image-20241019155638741" style="zoom:50%;" />

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019155713069.png" alt="image-20241019155713069" style="zoom:50%;" />

7.   Project->Shift拖拽动画素材到Animation时间轴

8.   Animation->Show Simple Rate 采样率

     Animation->Seconds/Frames时间轴以秒/帧的形式显示

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019160224521.png" alt="image-20241019160224521" style="zoom:50%;" />

     调整采样率为10, 表示10帧为1s

9.   回到Sence场景->点击Animation播放键预览

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019155928707.png" alt="image-20241019155928707" style="zoom:50%;" />

10.   预览完毕, 点击Preview, 重置动画状态

## 状态

创建新的动画Run

1.   新建新的动画Animation->Create New Clip

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019161315505.png" alt="image-20241019161315505" style="zoom:50%;" />

2.   同上, 拖动到时间轴, But Animator窗口

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019161753299.png" alt="image-20241019161753299" style="zoom:50%;" />

     Animator中, 每一个方块表示动画的状态

     创建新状态:

     1.   右键 Create State->Empty

          <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019162014679.png" alt="image-20241019162014679" style="zoom:50%;" />

     2.   Inspector->创建Motion

          <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019162222324.png" alt="image-20241019162222324" style="zoom:50%;" />

     

### 状态转换和转换条件

状态转换, 从IDLE转到RUN的状态

1.   右键->Make Transition->连接两个状态, 创建从IDLE的转换, 同时创建Run到IDLE的转换

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019162335651.png" alt="image-20241019162335651" style="zoom:50%;" />

     删除转换Inspector

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019162421321.png" alt="image-20241019162421321" style="zoom:67%;" />

     -   默认的转换方式是ExitTime, 上一个状态播放到百分之几就进行转换

         <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019162916527.png" alt="image-20241019162916527" style="zoom:67%;" />

         选中转换->Inspector->Has Exit TIme 勾选

         选中转换->Inspector->Settings->Exit TIme 0.8表示上一个状态播放了百分之八十

         <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019163106677.png" alt="image-20241019163106677" style="zoom:50%;" />

2.   Animator->左上角Parameters 创建变量参数

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019162704210.png" alt="image-20241019162704210" style="zoom:50%;" />

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019163518138.png" alt="image-20241019163518138" style="zoom:50%;" />

3.   选中转换->Inspector->Condition 创建转换条件

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019162515443.png" alt="image-20241019162515443" style="zoom:50%;" />

     X轴上的速度大于0.1(Unity对0的判断不是很准确)

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019163616058.png" alt="image-20241019163616058" style="zoom:50%;" />

     *RUN到IDEL的转换也同样设置*

4.   **取消勾选Has Exit Time**

     **取消勾选 Fixed Duration**

     **设置 Transition Duration 动画过渡为0** 因为像素游戏帧动画, 帧来实现过渡

5.   创建C#脚本, 控制Player Animation, 并将C#脚本作为组件加入Player的Inspector

     1.   在C#脚本中获取Animator组件

          ```csharp
          public class PlayerAnimation : MonoBehaviour {
          
              private Animator _animator;
          
              private void Awake() {
                  // 获取Animator组件
                  _animator = GetComponent<Animator>();
              }
          }
          ```

     2.   改变参数值

          ```csharp
          public class PlayerAnimation : MonoBehaviour {
              // 依照变量名获取Hash值
              private static readonly int VelocityX = Animator.StringToHash("velocityX");
          
              private Animator _animator;
              private Rigidbody2D _rb;
          
              private void Awake() {
                  // 获取Animator组件
                  _animator = GetComponent<Animator>();
                  _rb = GetComponent<Rigidbody2D>();
              }
          
              private void Update() {
                  Transform();
              }
          
              private void Transform() {
                  _animator.SetFloat(VelocityX, Mathf.Abs(_rb.velocity.x));
                  // Mathf.Abs是Unity的Math
              }
          }
          ```

## Bland Tree

>   混合树

跳跃动画制作

![image-20241019183443305](../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019183443305.png)



-   Jump Preparation 起跳*2
-   Flying up 起飞 *4
-   Jumping reload 空中滞留 *3
-   Falling animation 降落* 4
-   Landing animation 着陆 *3

### 创建Bland Tree

Animator->右键->Create State->From New Bland Tree

<img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019185647666.png" alt="image-20241019185647666" style="zoom:67%;" />

双击进入

### 构建Bland Tree

1.   五个跳跃做成五个Animation, 第五个作为Land

2.   混合树类型, 2D表示二维向量数值切换的动画转换

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019185906532.png" alt="image-20241019185906532" style="zoom:50%;" />

3.   对于跳跃, 使用一维(1D)Y轴方向上的速度

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019190118422.png" alt="image-20241019190118422" style="zoom:67%;" />

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019190059377.png" alt="image-20241019190059377" style="zoom:50%;" />

4.   依次添加各个动画

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019191937051.png" alt="image-20241019191937051" style="zoom:50%;" />

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019192034428.png" alt="image-20241019192034428" style="zoom:50%;" />

     

5.   蓝色的线表示当前正在运行的动画, velocityY的值, 发现蓝线指向其他动画

6.   取消勾选自动设置阈值, 手动更改阈值

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019191919694.png" alt="image-20241019191919694" style="zoom:50%;" />

7.   查看velocityY在运行过程中的取值范围

     人物->Inspector->Rigidbody2D->info->Velocity

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019190720808.png" alt="image-20241019190720808" style="zoom:50%;" />

     大概 6.666666 ?

8.   设置参数

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019195008147.png" alt="image-20241019195008147" style="zoom:50%;" />

9.   将Jump设计成能打断任何动画, 就将其连接AnyState, 并去除Has Exited等

     <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019195126162.png" alt="image-20241019195126162" style="zoom:50%;" />

10.   **将HasExitTime Settings CanTransition To Self 给取消勾选**, 否则会循环播放跳跃动画

      <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019195153255.png" alt="image-20241019195153255" style="zoom:50%;" />

11.   创建进入跳跃条件(一段跳, 二段跳都适用)

      <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019202021205.png" alt="image-20241019202021205" style="zoom:50%;" />

12.   jump->load 的转换条件, 设置 Has Exit 等, 设置从跳跃到落地的条件

      <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019195921808.png" alt="image-20241019195921808" style="zoom:67%;" />

13.   Load->Idle 的转换条件, 为Load的动画播放完毕, 故**勾选Has Exit**

      <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019202048797.png" alt="image-20241019202048797" style="zoom:50%;" />

14.   编写C#脚本

      ```csharp
      public class PlayerAnimation : MonoBehaviour {
         	// ...
          private static readonly int VelocityY = Animator.StringToHash("velocityY");
          private static readonly int OnGround = Animator.StringToHash("onGround");
      
          // 组件
      
          private void Awake() {/*...*/}
      
          private void Update() {
              Transform();
          }
      
          private void Transform() {
              // ...
              // Jump
              _animator.SetFloat(VelocityY, _rb.velocity.y);
              _animator.SetBool(OnGround, _epc.onGround);
          }
      }
      ```

15.   测试 跳跃/二段跳/从高处跳下从高处走下(竖直初速度为正)/从高处走下(竖直初速度为0)/落地后停住/落地后跑步

      <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019202138775.png" alt="image-20241019202138775" style="zoom:80%;" />

      小问题: 落地后跑步会蹲着位移一段

      -   设置如果跳到地面了
          -   且水平有速度, 则转Run而无load
          -   且水平无速度, 则转Load

      当然, 在着陆而开始播放Load动画的时候, 就会蹲着产生水平位移, 则为之奈何(简单, 略)

      <img src="../../assets/Day03-%E5%8A%A8%E7%94%BB/image-20241019203523714.png" alt="image-20241019203523714" style="zoom:80%;" />


# 攻击

## 素材

二段攻击

![image-20241021102353200](../../assets/Day04-%E6%94%BB%E5%87%BB/image-20241021102353200.png)

暴击

![image-20241021102423197](../../assets/Day04-%E6%94%BB%E5%87%BB/image-20241021102423197.png)



##攻击动画

1.   制作动画Attack1, Attack2, critcal attack

2.   制作新Layer, 权重1

3.   创建空State, 做为上级进入的State

4.   拖入三段攻击动画

5.   三段动画都可以在按下攻击键的时候状态转换

6.   设置bool: isAttack, trigger: attack(按键按下)

7.   连接关系:

     -    NewState->Attack1

         isAttack==true

         attack

     -   Attack1->Attack2

         isAttack==true

         attack

     -   Attack2->CriticalAttack

         isAttack==true

         attack


     <img src="../../assets/Day04-%E6%94%BB%E5%87%BB/image-20241021105111493.png" alt="image-20241021105111493" style="zoom:50%;" />

8.   连接关系(Has Exited TIme)

     只有在动画播放90%之前按下攻击按键, 才能触发连击

     -   Attack1->Attack2

         Exit Time>0.9=> Attack2

     -   Attack2->CirticalAttack

         Exit Time>0.9=> Attack2

     -   Attack1->Exit

         Exit Time>1=> Exit

     -   Attack2->Exit

         Exit Time>1=> Exit

     -   CirticalAttack->Exit

         Exit Time>1=> Exit

9.   添加攻击按钮

10.   编写控制C#脚本

     ```csharp
     public class PlayerController : MonoBehaviour {
         #region 组件
         #region 私有字段
         #region 公有字段
     
         #region 属性
     
         public bool IsAttack { get; private set; } = false;
     
         #endregion
     
         #region 事件
     
         public UnityEvent onAttack;
     
         #endregion
     
         #region 事件函数
         private void Awake() {
             // ...
             _inputControl.Gameplay.Attack.started += this.Attack;
         }
         // ...
         #endregion
         #region 移动
     
     
         #region 跳跃
     
         #region 攻击
     
         private void Attack(InputAction.CallbackContext obj) {
             IsAttack = true;
             onAttack?.Invoke();
         }
     
         public void RecoverFromAttack() {
             IsAttack = false;
         }
     
         #endregion
     
         #region 受击与死亡
     }
     ```

11.   编写C#动画脚本

      ```csharp
      public class PlayerAnimation : MonoBehaviour {
          参数Id 
              
          组件
      
          #region 事件函数
      	// ..
              
          private void Update() {
              Transform();
              Attack();
          }
          #endregion
      
          #region 参数设置
      	// ...
          private void Attack() {
              _animator.SetBool(IsAttack, _pc.IsAttack);
          }
          #endregion
      
          #region 触发器
      	// ...
          public void AttackTrigger() {
              _animator.SetTrigger(AttackTriggerId);
          }
          #endregion
      }
      ```

12.   设置事件回调函数

      <img src="../../assets/Day04-%E6%94%BB%E5%87%BB/image-20241021122236855.png" alt="image-20241021122236855" style="zoom:67%;" />

13.   在状态退出之后, 恢复isAttack==false

      ```csharp
      public class PlayerAttackAnimation : StateMachineBehaviour {
          public override void OnStateExit(
              Animator animator, AnimatorStateInfo stateInfo, int layerIndex) {
              animator.GetComponent<PlayerController>()?.RecoverFromAttack();
          }
      }
      ```

      加入到Animator->State->Inspector->AddBahavier中去

## 攻击碰撞判定

在有剑影的部分判定攻击

<img src="../../assets/Day04-%E6%94%BB%E5%87%BB/image-20241021125154384.png" alt="image-20241021125154384" style="zoom:50%;" />

### 子集物体

1.   Hierarchy->Player->Create Empty 在Player下创建一个子集物体, 命名为Attack Area
2.   在Attack Area 下创建三段攻击 Attack1, Attack2, CriticalAttack

### 为子集物体创建碰撞体

>   Polygon Collider 2D 多边形碰撞体

1.   Add Component->Polygon Collider 2D 多边形碰撞体

2.   勾选isTrigger触发器

     <img src="../../assets/Day04-%E6%94%BB%E5%87%BB/image-20241021125800462.png" alt="image-20241021125800462" style="zoom:50%;" />

3.   用剑影来判断范围碰撞体范围

     <img src="../../assets/Day04-%E6%94%BB%E5%87%BB/image-20241021125919717.png" alt="image-20241021125919717" style="zoom:50%;" />

4.   在这有剑影的一帧里, 启动/关闭这个碰撞体, 进行判定

     <img src="../../assets/Day04-%E6%94%BB%E5%87%BB/image-20241021130543842.png" alt="image-20241021130543842" style="zoom:50%;" />

     Animation->Add Property->Attack Area->Attack1->Game Object is Active 在这一帧里启动这个物体

     <img src="../../assets/Day04-%E6%94%BB%E5%87%BB/image-20241021130632144.png" alt="image-20241021130632144" style="zoom:50%;" />

5.   在IDLE状态下, 关闭所有的Attack

6.   Hierarchy->Player->Attack Area-> Attack1->Inspector->Add Component->Attack 加上之前就做了的攻击组件

7.   为了防止伤到玩家自己, 为Attack override Layers

     <img src="../../assets/Day04-%E6%94%BB%E5%87%BB/image-20241021133223950.png" alt="image-20241021133223950" style="zoom:50%;" />

     -   **Contact Capture Layers** 与当前层接触的层
     -   Include Layer: **Contact Capture Layers** +Include Layer
     -   Exclude Layer: **Contact Capture Layers**-Exclude Layer
     -   Callback Layer: 

8.   为三个Attack都做同样的操作, 各自设置不同的攻击伤害值

9.   此时, 敌人不会扣血, Why?

     敌人有两个碰撞箱: Box判定敌人站立, Capsule判定敌人战斗

     **两个碰撞体都有LayerOverride**

     <img src="../../assets/Day04-%E6%94%BB%E5%87%BB/image-20241021155702989.png" alt="image-20241021155702989" style="zoom:50%;" />

     且它们的Layer Override Priority都是一致的, 导致了攻击到敌人脚底的碰撞体才会判断敌人受伤

     此万不可

     故应调节敌人攻击的碰撞体同时为其受击的碰撞体

     <img src="../../assets/Day04-%E6%94%BB%E5%87%BB/image-20241021155940549.png" alt="image-20241021155940549" style="zoom:50%;" />

10.   由于敌人碰到了剑锋, 就会对剑锋发出攻击, 而剑锋没有血量, 就不能攻击. 攻击脚本更改

     ```csharp
     private void OnTriggerStay2D(Collider2D other) {
         var otherFeature = other.GetComponent<CharacterFeature>();
         if (otherFeature is null) {
             return;
         }
         otherFeature?.TakeDamage(this);
     }
     ```

11.   由于没有移动攻击的动画, 于是控制移动

      ```csharp
      private void FixedUpdate() {
          if (_isHurt || IsAttack) {
              // 失去移动控制权限
              return;
          }
      
          // 进行移动控制
          TurnAround(); // 转身
          Move();
          UpdateJumpedInAir();
      }
      ```

      注意, 上面设置IsAttack在每一段攻击动画播放结束后, 都会使IsAttack=False, 故使其在进入下一个Attack的时候使IsAttack为true(1-2-3[短暂的时间]1-2-3)

      ```csharp
      public class PlayerAttackAnimation : StateMachineBehaviour {
          public override void OnStateEnter(
              Animator animator, AnimatorStateInfo stateInfo, int layerIndex) {
              var pc = animator.GetComponent<PlayerController>();
              if (pc is null) {
                  return;
              }
      
              pc.IsAttack = true;
          }
      
          public override void OnStateExit(
              Animator animator, AnimatorStateInfo stateInfo, int layerIndex) {
              var pc = animator.GetComponent<PlayerController>();
              if (pc is null) {
                  return;
              }
      
              pc.IsAttack = false;
          }
      }
      ```

12.   由于人物材质是绝对光滑的, 在攻击过程中仍会往前滑行

      解决: 判断在地面上, 则适用0.4摩擦系数, 否则适用0摩擦系数

      ```csharp
      public PhysicsMaterial2D materialOnGround;
      public PhysicsMaterial2D materialInAir;
      
      private void Update() {
          UpdateInputDirection();
          UpdateMaterial();
      }
      
      private void UpdateMaterial() {
          // _cc CapsuleCollider2D
          _cc.sharedMaterial = _epc.onGround? materialOnGround : materialInAir;
      }
      ```


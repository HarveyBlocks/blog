# 受击

## 血量等角色特性

由于敌我双方都具有血量的概念, 故对血量维护另作类维护

```csharp
public class CharacterFeature : MonoBehaviour {
    private int _health;
    private bool _dead;

    [FormerlySerializedAs("HealthCap")]
    public int healthCap = 100;

    public int defense = 5;

    public int Health {
        get => _health;
        set {
            if (value > healthCap) {
                _health = healthCap;
            } else if (value < 0) {
                _health = 0;
            } else {
                _health = value;
            }

            _dead = _health <= 0;
        }
    }

    private void Start() {
        // init health
        Health = healthCap;
    }
    public int AddHealth(int deltaHealth) {
        Health += deltaHealth;
        return Health;
    }

	/**
     * <param name="attacker">发起攻击的一方</param>
     */
    public int TakeDamage(Attack attacker) {
        if (attacker.damage <= defense) {
            return Health;
        }
        AddHealth(-(attacker.damage - defense));
        // 检测死亡
        if (Health <= 0) {
            // ...
        }

        return Health;
    }
}
```

## 攻击与受击

```csharp
public class Attack : MonoBehaviour {
    /// 一次攻击给对方造成的伤害
    public int damage;
    /// 范围
    public float range;
    /// 频率
    public float rate;

    private void OnTriggerStay2D(Collider2D other) {
        // 触发器触发监听
        var otherHealth = other
            .GetComponent<CharacterFeature>()? // ?不为空判断, 判断对方确实有Component
            .TakeDamage(this);
        Debug.Log(otherHealth);
    }
}
```

## 无敌时间

```csharp
public class CharacterFeature : MonoBehaviour {
	// ...

    /// 无敌时间计时器
    private float _cannotBeHitCounter;
    public float cannotBeHitDuration;
    public bool CannotBeHit => _cannotBeHitCounter > 0;

    private void Start() {
        // init health...
    }

    public void Update() {
        UpdateCannotBeHitCounter();
    }

    private void UpdateCannotBeHitCounter() {
        if (CannotBeHit) {
            // 处于无敌状态
            // 倒计时
            // 用Time.deltaTime修正
            _cannotBeHitCounter -= Time.deltaTime;
        }
    }
    /**
     * <param name="attacker">发起攻击的一方</param>
     */
    public int TakeDamage(Attack attacker) {
        if (CannotBeHit) {
            return Health;
        }

        if (attacker.damage <= defense) {
            return Health;
        }

        AddHealth(-(attacker.damage - defense));
        TriggerCannotBeHit();
        // 检测死亡
        if (Health <= 0) {
            // ...
        }

        return Health;
    }

    /// 触发无敌
    private void TriggerCannotBeHit() {
        if (!CannotBeHit) {
            _cannotBeHitCounter = cannotBeHitDuration;
        }
    }
}
```

## 受伤动画

### 受伤闪烁

>   闪烁表示经历了受伤

要求闪烁时, 不会被跑动和跳跃影响

1.   Inspector->Sprite Renderer->Color ->A (alphy) 改变alphy值, 调整透明度, 就有闪烁的效果, 也可调RGB, 使其变红表示受伤

2.   Animation->create new Clip-> 创建一个HurtFlicker(受击闪烁)动画

3.   Animation->AddProperties->Sprite Renderer->SpriteRenderer.Material._Color

     <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020122123304.png" alt="image-20241020122123304" style="zoom:50%;" />

4.   点击时间轴上的0.2, 调整A

     <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020122428403.png" alt="image-20241020122428403" style="zoom:50%;" />

5.   Animator->Layers 添加动画图层

     <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020121047256.png" alt="image-20241020121047256" style="zoom:50%;" />

6.   Layer->Config->Blending->Additive 调整为**叠加**层, Weigt权重表示动画播放的优先级, 调到1

     <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020123048710.png" alt="image-20241020123048710" style="zoom:50%;" />

7.   拖入动画状态到叠加层

     <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020123351032.png" alt="image-20241020123351032" style="zoom:50%;" />

8.   Animator->Parameters->+->Trigger 添加条件参数: 触发器

     <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020123506925.png" alt="image-20241020123506925" style="zoom:50%;" />

9.   Animator->Transition->Inspector->Condition->添加Trigger hurt, 不需要转换时间, 不需要退出时间

     <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020123715970.png" alt="image-20241020123715970" style="zoom:50%;" />

10.   动画播放完, 就返回到原有状态

      <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020123916838.png" alt="image-20241020123916838" style="zoom:50%;" />

11.   C#脚本, 触发器触发

      ```csharp
      public class PlayerAnimation : MonoBehaviour {
          // ...
          private static readonly int Hurt = Animator.StringToHash("hurt");
          public void HurtTrigger() {
              _animator.SetTrigger(Hurt);
          }
      }
      ```

12.   在受击部分发送触发信号, 触发信号由Unity事件发布, 发布信号后, 所有注册在事件的方法都会被调用执行

      ```csharp
      public class CharacterFeature : MonoBehaviour {
      	// ...
          /// 受伤事件
          /// 监听到这个事件的方法可能会需要有Transform的信息, 于是传递Transform的信息
          public UnityEvent<Transform> onTakeDamage;
          // ...
      }
      ```

      角色->Inspector->CharacterFeature-> On Take Damage

      <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020144146924.png" alt="image-20241020144146924" style="zoom:50%;" />

      点击`+`添加在事件发生后会被调用的方法

      <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020144301095.png" alt="image-20241020144301095" style="zoom:50%;" />

      <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020144831870.png" alt="image-20241020144831870" style="zoom:50%;" />

      -   Runtime Only 在运行时检测
      -   Knight Player Animation C#脚本文件/组件
      -   PlayerAnimation.HurtTrigger 回调方法

13.   触发事件

      ```csharp
      /**
       * <param name="attacker">发起攻击的一方</param>
       */
      public int TakeDamage(Attack attacker) {
      	// ...
          // 触发受击事件
          onTakeDamage?.Invoke(attacker.transform); 
          // 攻击从什么方向来, 就向什么方向被击退
          // 故参数为攻击方的transform
          AddHealth(-(attacker.damage - defense));
          // 检测死亡
          if (Health <= 0) {
              onDead.Invoke(attacker.transform);
          }
      	// ...
          return Health;
      }
      ```

14.   受伤的动画关系绘制

      <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020202442182.png" alt="image-20241020202442182" style="zoom:50%;" />

      Transitions->settings->Can transition to self 取消勾选

### 受伤击退

朝敌人出现的方向的另一边反弹

1.   编写C#脚本

     ```csharp
     private bool _isHurt = false;
     // 受击的力
     public float repelledForce  = 200;
     private void FixedUpdate() {
         if (_isHurt) {
             // 受伤了就失去控制权限
             return;
         }

         // 不受伤才能进行控制
         TurnAround(); // 转身
         Move();
         UpdateJumpedInAir();
     }
     private void Jump(InputAction.CallbackContext obj) {
         if (_isHurt) {
             // 受伤了就失去控制权限
             return;
         }
     	// ....
     }
     /// 击退
     public void Repelled(Transform attackerTf) {
         Debug.Log(attackerTf);
         _isHurt = true;
         _rb.velocity = Vector2.zero;
         _rb.AddForce(
             (transform.position-attackerTf.position).normalized
             // 归一化, 保证只决定方向
             * repelledForce, // 决定大小
             ForceMode2D.Impulse);
     }
     public void RecoverFromHurt() {
         _isHurt = false;
     }
     ```

2.   注册受击事件

     <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020191904003.png" alt="image-20241020191904003" style="zoom:50%;" />

     PS: 可以再添加方法时, 关注`Dynamic XXX` 表示传递参数和事件需要的参数一致, 有助于快速选择

     <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020192019865.png" alt="image-20241020192019865" style="zoom:50%;" />

3.   在受击动画完成播放之后调用`Recover`方法, 恢复用户的控制

     Animator->Hurt状态->Inspector->Add behaviour

     <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020194345548.png" alt="image-20241020194345548" style="zoom:50%;" />

     自动生成在Assert文件夹下

4.   编写动画脚本逻辑

     ```csharp
     public class PlayerHurtAnimation : StateMachineBehaviour {
         public override void OnStateExit(
             Animator animator, AnimatorStateInfo stateInfo, int layerIndex) {
             // 在动画结束后被调用
             animator.GetComponent<PlayerController>()?.RecoverFromHurt();
         }
     }
     ```

## 死亡动画

1.   设置动画状态转换参数isDead(bool)

2.   创建死亡层

     <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020221047369.png" alt="image-20241020221047369" style="zoom:50%;" />

     override, 权重为1, 完全覆盖前面一层的动画, 

     这一层的AnyState成立的时候只动画的时候, 只执行这一层的动画

3.   设置状态转换关系

     AnyState->isDead\==true->Death Animation->isDead\==false->Exit

     如果Death Animation直接到Exit, 就会跳回上一层的动画

     <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020221147192.png" alt="image-20241020221147192" style="zoom:50%;" />

4.   C#脚本

     ```cs
     public class PlayerController : MonoBehaviour {
     	// ...
         public void Die() {
             // 关闭控制
             _inputControl.Gameplay.Disable();
             _rb.velocity = Vector2.zero;
         }
     }
     ```

     ```csharp
     public class PlayerAnimation : MonoBehaviour {
     	// ...
         public void DeadTrigger() {
             _animator.SetBool(IsDead, true);
         }
     }
     ```

5.   死亡动画只播放一次, 死亡动画播放完毕后, 再次回到AnyState判断, isDead==true依旧成立

     点击动画素材

     <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020221407024.png" alt="image-20241020221407024" style="zoom:50%;" />

     Inspector->LoopTIme 取消勾选

     <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/unity/2DRPG基础/Day03-受击/image-20241020221336803.png" alt="image-20241020221336803" style="zoom:50%;" />


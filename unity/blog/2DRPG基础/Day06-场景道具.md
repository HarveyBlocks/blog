# 陷阱

掉入水池就死亡, 碰到荆棘就受伤

## 水池

1.   制作水池Tilemap, 挂载Tilemap Collider 2D, 勾选Trigger

2.   为水添加Tag

3.   制作脚本

     ```csharp
     public class MediumTrap : MonoBehaviour {
         public int damage;
     
         public enum HurtType {
             Die,
             Hurt
         }
     
         public HurtType hurtType;
     
         /// 中陷阱=进入陷阱的碰撞体触发器
         /// <param name="other">中陷阱的对象</param>
         private void OnTriggerStay2D(Collider2D other) {
             var characterFeature = other.GetComponent<CharacterFeature>();
             switch (hurtType) {
                 case HurtType.Die:
                     characterFeature?.DieImmediately(this);
                     break;
                 case HurtType.Hurt:
                     characterFeature?.TakeTrapDamage(this);
                     break;
                 default:
                     throw new ArgumentOutOfRangeException();
             }
         }
     }
     ```

4.   所有生物掉入水池都死亡, 在CharacterFeature脚本添加受击

     ```csharp
     public class CharacterFeature : MonoBehaviour {
     	#region 中陷阱
     
         // 立刻死亡
         public void DieImmediately(MediumTrap mediumTrap) {
             Health = 0;
             onDead?.Invoke();
         }
     
         /// 收到陷阱伤害
         public int TakeTrapDamage(MediumTrap mediumTrap) => TakeDamage(mediumTrap.damage, mediumTrap.transform);
     
         #endregion
     }
     ```



## 荆棘

受伤, 直接在荆棘的TIlemap加上Attack组件

## 奖励

>   宝箱

用Windows的画图从Forest的png文件里切出宝箱的png来

没有标识"发现宝箱"的素材, 随便搞个素材

标识的标识加在人物头上表示检测到了啥

人物转动时, 标识会跟着转动, 不可

```csharp
picture.transform.localScale = player.transform.localScale;
```

宝箱打开后使其碰撞体不可用, 即可避免被检测

## 传送门

和奖励的逻辑一样, 检测到就出现标识, 然后执行传送的逻辑

详见[场景转换](Day07-场景.md##场景转换)


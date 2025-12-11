# 重写

**需要有父子类关系，且只能重写方法**

**只和非静态方法（实例）有关！！！！！！只能用public！！！！！！！！！！**不要用final

两个类里都有A()方法
1. 方法名相同
2. 参数列表相同
3. 返回值类型可以不同,但在子类重写的方法的返回值类型必须是父类方法的返回值类型的子类
4. 子类重写方法作用域不得低于父类方法

![image-20230808181918677](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java面向对象/Day10-重写/image-20230808181918677.png)

- 对父类的引用，指向子类，这其实是多态

当去掉A()的static，出现了这个![image-20230808182359371](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java面向对象/Day10-重写/image-20230808182359371.png)

![image-20230808182756279](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java面向对象/Day10-重写/image-20230808182756279.png)

!?：**重写**了父类的代码

## 与重载的异同

1. 方法名必须相同

2. 参数列表必须相同

3. 修饰符范围可以扩大，但是不能缩小（private->public扩大）

   public>protcted>default(默认的)>private

4. 抛出的异常可以被缩小，但不能扩大。(Excption->ClassNotFoundExcption缩小)

## 作用

父类的方法子类不一定需要，或者不一定满足

